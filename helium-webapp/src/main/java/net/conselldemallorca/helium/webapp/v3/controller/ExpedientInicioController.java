/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PermissionService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

	@Resource(name="dissenyServiceV3")
	private DissenyService dissenyService;

	@Resource(name="expedientServiceV3")
	private ExpedientService expedientService;
	
	@Resource(name = "tascaServiceV3")
	private TascaService tascaService;

	@Resource(name = "pluginServiceV3")
	private PluginService pluginService;

	@Autowired
	public ExpedientInicioController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@RequestMapping(value = "/iniciar", method = RequestMethod.GET)
	public String iniciarGet(
			HttpServletRequest request,
			ModelMap model) {
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
				DefinicioProcesDto darrera = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipus.getId(), true);
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
	
	private ExpedientTascaDto obtenirTascaInicial(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors,
			HttpServletRequest request) {
		ExpedientTascaDto tasca = expedientService.getStartTask(
				entornId,
				expedientTipusId,
				definicioProcesId,
				valors);
		tasca.setId(
				(String)request.getSession().getAttribute(
						ExpedientInicioController.CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(
				ExpedientInicioController.CLAU_SESSIO_FORM_VALIDAT); 
		if (validat != null)
			tasca.setValidada(true);
		return tasca;
	}

	@RequestMapping(value = "/iniciar", method = RequestMethod.POST)
	public String iniciarPost(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			model.addAttribute("entornId", entorn.getId());
			ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			if (potIniciarExpedientTipus(expedientTipus)) {
				netejarSessio(request);
				generarTaskIdSessio(request);
				// Si l'expedient requereix dades inicials redirigeix al pas per demanar
				// aquestes dades
				DefinicioProcesDto definicioProces = null;
				if (definicioProcesId != null) {
					definicioProces = dissenyService.getById(definicioProcesId, true);
					model.addAttribute("definicioProcesId", definicioProcesId);
				} else {
					definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId, true);
				}				
				
				if (definicioProces.isHasStartTask()) {					
					ExpedientTascaDto tasca = obtenirTascaInicial(
							entorn.getId(),
							expedientTipusId,
							definicioProces.getId(),
							new HashMap<String, Object>(),
							request);
					model.addAttribute("tasca", tasca);
					model.addAttribute("dades", tascaService.findDadesPerTascaDto(tasca));
					model.addAttribute("definicioProcesId", (definicioProcesId != null)? definicioProcesId : tasca.getDefinicioProces().getId());
					return "v3/expedient/iniciarPasForm";
				} else if (expedientTipus.isDemanaNumero() || expedientTipus.isDemanaTitol() || expedientTipus.isSeleccionarAny()) {
					// Si l'expedient no requereix dades inicials però ha de demanar titol i/o número
					// redirigeix al pas per demanar aquestes dades
					omplirModelPerMostrarFormulari(expedientTipus, model);
					model.addAttribute("any", Calendar.getInstance().get(Calendar.YEAR));
					model.addAttribute("numero", expedientService.getNumeroExpedientActual(
							entorn.getId(),
							expedientTipus,
							Calendar.getInstance().get(Calendar.YEAR)));
					model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
					return "v3/expedient/iniciarPasTitol";
				} else {
					// Si no requereix cap pas addicional inicia l'expedient directament
					try {
						ExpedientDto iniciat = iniciarExpedient(
								entorn.getId(),
								expedientTipusId,
								definicioProcesId);
						MissatgesHelper.info(
								request,
								getMessage(
										request,
										"info.expedient.iniciat"));
					} catch (Exception ex) {
						MissatgesHelper.error(
								request,
								getMessage(request, "error.iniciar.expedient"));
			        	logger.error("No s'ha pogut iniciar l'expedient", ex);
					}
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.iniciar.tipus.exp") );
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "v3/expedient/iniciar";
	}
	
	private void omplirModelPerMostrarFormulari(
			ExpedientTipusDto tipus,
			ModelMap model) {
		model.addAttribute(
				"responsable",
				pluginService.findPersonaAmbCodi(tipus.getResponsableDefecteCodi()));
		model.addAttribute(
				"expedientTipus",
				tipus);
		Integer[] anys = new Integer[10];
		int anyActual = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 10; i++) {
			anys[i] = new Integer(anyActual - i);
		}
		model.addAttribute("anysSeleccionables", anys);
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
	public static String getClauSessioCampRegistre(String campCodi) {
		return ExpedientInicioController.CLAU_SESSIO_PREFIX_REGISTRE + campCodi;
	}

	private boolean potIniciarExpedientTipus(ExpedientTipusDto expedientTipus) {
		return permissionService.isGrantedAny(
				expedientTipus.getId(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.CREATE});
	}

	private void generarTaskIdSessio(HttpServletRequest request) {
		request.getSession().setAttribute(
				ExpedientInicioController.CLAU_SESSIO_TASKID,
				"TIE_" + System.currentTimeMillis());
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

	private static final Log logger = LogFactory.getLog(ExpedientInicioController.class);
}
