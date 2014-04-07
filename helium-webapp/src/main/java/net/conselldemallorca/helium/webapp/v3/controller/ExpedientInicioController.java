/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientInicioPasTitolCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador per iniciar un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInicioController extends BaseExpedientController {

	public static final String CLAU_SESSIO_TASKID = "iniciexp_taskId";
	public static final String CLAU_SESSIO_TITOL = "iniciexp_titol";
	public static final String CLAU_SESSIO_NUMERO = "iniciexp_numero";
	public static final String CLAU_SESSIO_ANY = "iniciexp_any";
	public static final String CLAU_SESSIO_FORM_VALIDAT = "iniciexp_form_validat";
	public static final String CLAU_SESSIO_FORM_COMMAND = "iniciexp_form_command";
	public static final String CLAU_SESSIO_FORM_VALORS = "iniciexp_form_registres";
	public static final String CLAU_SESSIO_PREFIX_REGISTRE = "ExpedientIniciarController_reg_";
	
	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private TascaService tascaService;

	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = "/iniciar", method = RequestMethod.GET)
	public String iniciarGet(
			HttpServletRequest request,
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			List<ExpedientTipusDto> tipus = dissenyService.findExpedientTipusAmbEntorn(entorn);
			permissionService.filterAllowed(
					tipus,
					new ObjectIdentifierExtractor<ExpedientTipusDto>() {
						public Long getObjectIdentifier(ExpedientTipusDto expedientTipus) {
							return expedientTipus.getId();
						}
					},
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.CREATE});
			Map<Long, DefinicioProcesDto> definicionsProces = new HashMap<Long, DefinicioProcesDto>();
			Iterator<ExpedientTipusDto> it = tipus.iterator();
			while (it.hasNext()) {
				ExpedientTipusDto expedientTipus = it.next();
				DefinicioProcesDto darrera = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipus.getId());
				if (darrera != null)
					definicionsProces.put(expedientTipus.getId(), darrera);
				else
					it.remove();
			}
			model.addAttribute("expedientTipus", tipus);
			model.addAttribute("definicionsProces", definicionsProces);
		}  else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		
		return "v3/expedient/iniciar";
	}
	
	@RequestMapping(value = "/iniciar", method = RequestMethod.POST)
	public String iniciarPost(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potIniciarExpedientTipus(expedientTipus)) {
				netejarSessio(request);
				request.getSession().setAttribute(ExpedientInicioController.CLAU_SESSIO_TASKID,"TIE_" + System.currentTimeMillis());
				
				// Si l'expedient requereix dades inicials redirigeix al pas per demanar aquestes dades
				DefinicioProcesDto definicioProces = null;
				if (definicioProcesId != null) {
					definicioProces = dissenyService.getById(definicioProcesId);
				} else {
					definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId);
				}
				model.addAttribute("definicioProcesId", definicioProces.getId());
				
				if (definicioProces.isHasStartTask()) {					
					ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProces.getId(), new HashMap<String, Object>(), request);
					List<TascaDadaDto> dades = tascaService.findDadesPerTascaDto(tasca);
					model.addAttribute("tasca", tasca);
					model.addAttribute("dades", dades);
					model.addAttribute("entornId", entorn.getId());
					model.addAttribute("expedientTipus", expedientTipus);
					model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
					return "v3/expedient/iniciarPasForm";
				} else if (expedientTipus.isDemanaNumero() || expedientTipus.isDemanaTitol() || expedientTipus.isSeleccionarAny()) {
					// Si l'expedient no requereix dades inicials però ha de demanar titol i/o número redirigeix al pas per demanar aquestes dades
					
					ExpedientInicioPasTitolCommand command = new ExpedientInicioPasTitolCommand();
					command.setAny(Calendar.getInstance().get(Calendar.YEAR));
					command.setExpedientTipusId(expedientTipusId);
					command.setNumero(
							expedientService.getNumeroExpedientActual(
									entorn.getId(),
									expedientTipus,
									command.getAny()));
					command.setResponsableCodi(expedientTipus.getResponsableDefecteCodi());
					command.setEntornId(entorn.getId());
					model.addAttribute(command);
					model.addAttribute("entornId", entorn.getId());
					model.addAttribute("anysSeleccionables", getAnysSeleccionables());
					model.addAttribute("expedientTipus", expedientTipus);
					model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
					return "v3/expedient/iniciarPasTitol";
				} else {
					// Si no requereix cap pas addicional inicia l'expedient directament
					try {
						ExpedientDto iniciat = iniciarExpedient(entorn.getId(), expedientTipusId, definicioProces.getId());
						MissatgesHelper.info(request, getMessage(request, "info.expedient.iniciat", new Object[] {iniciat.getIdentificador()}));
					    ExpedientInicioController.netejarSessio(request);
					} catch (Exception ex) {
						MissatgesHelper.error( request, getMessage(request, "error.iniciar.expedient"));
			        	logger.error("No s'ha pogut iniciar l'expedient", ex);
					}
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.iniciar.tipus.exp") );
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return iniciarGet(request, model);
	}

	public List<ParellaCodiValorDto> getAnysSeleccionables() {
		List<ParellaCodiValorDto> anys = new ArrayList<ParellaCodiValorDto>();
		int anyActual = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 10; i++) {
			ParellaCodiValorDto par = new ParellaCodiValorDto();
			par.setCodi(String.valueOf(anyActual - i));
			par.setValor(anyActual - i);
			anys.add(par);
		}
		return anys;
	}

	@SuppressWarnings("unchecked")
	public static void netejarSessio(HttpServletRequest request) {
		Enumeration<String> atributs = request.getSession().getAttributeNames();
		while (atributs.hasMoreElements()) {
			String atribut = atributs.nextElement();
			if (atribut.startsWith(ExpedientInicioController.CLAU_SESSIO_PREFIX_REGISTRE))
				request.getSession().removeAttribute(atribut);
		}
		request.getSession().removeAttribute(ExpedientInicioController.CLAU_SESSIO_TASKID);
		request.getSession().removeAttribute(ExpedientInicioController.CLAU_SESSIO_NUMERO);
		request.getSession().removeAttribute(ExpedientInicioController.CLAU_SESSIO_TITOL);
		request.getSession().removeAttribute(ExpedientInicioController.CLAU_SESSIO_FORM_VALIDAT);
		request.getSession().removeAttribute(ExpedientInicioController.CLAU_SESSIO_FORM_COMMAND);
		request.getSession().removeAttribute(ExpedientInicioController.CLAU_SESSIO_FORM_VALORS);
	}

	private synchronized ExpedientDto iniciarExpedient(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId) {
		return expedientService.iniciar(
				entornId,
				null,
				expedientTipusId,
				definicioProcesId,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				false,
				null,
				null,
				null,
				null,
				null,
				null,
				false,
				null,
				null,
				false,
				null,
				null,
				IniciadorTipusDto.INTERN,
				null,
				null,
				null,
				null);
	}
	
	private ExpedientTascaDto obtenirTascaInicial(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors,
			HttpServletRequest request) {
		ExpedientTascaDto tasca = expedientService.getStartTask( entornId, expedientTipusId, definicioProcesId, valors);
		tasca.setId((String)request.getSession().getAttribute(ExpedientInicioController.CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(ExpedientInicioController.CLAU_SESSIO_FORM_VALIDAT); 
		if (validat != null)
			tasca.setValidada(true);
		return tasca;
	}

	private static final Log logger = LogFactory.getLog(ExpedientInicioController.class);
}
