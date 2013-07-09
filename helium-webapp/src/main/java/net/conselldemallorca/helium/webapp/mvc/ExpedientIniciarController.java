/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.exception.ExpedientRepetitException;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador per iniciar un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientIniciarController extends BaseController {

	public static final String CLAU_SESSIO_TASKID = "iniciexp_taskId";
	public static final String CLAU_SESSIO_TITOL = "iniciexp_titol";
	public static final String CLAU_SESSIO_NUMERO = "iniciexp_numero";
	public static final String CLAU_SESSIO_ANY = "iniciexp_any";
	public static final String CLAU_SESSIO_FORM_VALIDAT = "iniciexp_form_validat";
	public static final String CLAU_SESSIO_FORM_COMMAND = "iniciexp_form_command";
	public static final String CLAU_SESSIO_FORM_VALORS = "iniciexp_form_registres";
	public static final String CLAU_SESSIO_PREFIX_REGISTRE = "ExpedientIniciarController_reg_";

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientIniciarController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public ExpedientIniciarPasTitolCommand populateCommand(
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId) {
		if (expedientTipusId != null) {
			ExpedientIniciarPasTitolCommand command = new ExpedientIniciarPasTitolCommand();
			command.setExpedientTipusId(expedientTipusId);
			return command;
		}
		return null;
	}

	@RequestMapping(value = "/expedient/iniciar", method = RequestMethod.GET)
	public String iniciarGet(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			List<ExpedientTipus> tipus = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
			permissionService.filterAllowed(
					tipus,
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.CREATE});
			Map<Long, DefinicioProcesDto> definicionsProces = new HashMap<Long, DefinicioProcesDto>();
			Iterator<ExpedientTipus> it = tipus.iterator();
			while (it.hasNext()) {
				ExpedientTipus expedientTipus = it.next();
				DefinicioProcesDto darrera = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipus.getId(), true);
				if (darrera != null)
					definicionsProces.put(expedientTipus.getId(), darrera);
				else
					it.remove();
			}
			model.addAttribute("expedientTipus", tipus);
			model.addAttribute("definicionsProces", definicionsProces);
			return "expedient/iniciar";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/iniciar", method = RequestMethod.POST)
	public String iniciarPost(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = true) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus tipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potIniciarExpedientTipus(tipus)) {
				netejarSessio(request);
				generarTaskIdSessio(request);
				// Si l'expedient requereix dades inicials redirigeix al pas per demanar
				// aquestes dades
				DefinicioProcesDto definicioProces = null;
				if (definicioProcesId != null)
					definicioProces = dissenyService.getById(definicioProcesId, true);
				else
					definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId, true);
				if (definicioProces.isHasStartTask()) {
					if (definicioProcesId != null)
						return "redirect:/expedient/iniciarPasForm.html?expedientTipusId=" + expedientTipusId + "&definicioProcesId=" + definicioProcesId;
					else
						return "redirect:/expedient/iniciarPasForm.html?expedientTipusId=" + expedientTipusId;
				}
				// Si l'expedient no requereix dades inicials però ha de demanar titol i/o número
				// redirigeix al pas per demanar aquestes dades
				if (tipus.getDemanaNumero().booleanValue() || tipus.getDemanaTitol().booleanValue()) {
					if (definicioProcesId != null)
						return "redirect:/expedient/iniciarPasTitol.html?expedientTipusId=" + expedientTipusId + "&definicioProcesId=" + definicioProcesId;
					else
						return "redirect:/expedient/iniciarPasTitol.html?expedientTipusId=" + expedientTipusId;
				}
				// Si no requereix cap pas addicional inicia l'expedient directament
				try {
					ExpedientDto iniciat = iniciarExpedient(
							entorn.getId(),
							expedientTipusId,
							definicioProcesId);
					missatgeInfo(
							request,
							getMessage(
									"info.expedient.iniciat",
									new Object[] {iniciat.getIdentificador()}));
				} catch (ExpedientRepetitException ex) {
					missatgeError(
							request,
							getMessage("error.exist.exp.mateix.numero"));
				} catch (Exception ex) {
					missatgeError(
							request,
							getMessage("error.iniciar.expedient"),
							(ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage());
		        	logger.error("No s'ha pogut iniciar l'expedient", ex);
				}
			} else {
				missatgeError(request, getMessage("error.permisos.iniciar.tipus.exp") );
			}
			return "redirect:/expedient/iniciar.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	@SuppressWarnings("unchecked")
	public static void netejarSessio(HttpServletRequest request) {
		Enumeration<String> atributs = request.getSession().getAttributeNames();
		while (atributs.hasMoreElements()) {
			String atribut = atributs.nextElement();
			if (atribut.startsWith(ExpedientIniciarController.CLAU_SESSIO_PREFIX_REGISTRE))
				request.getSession().removeAttribute(atribut);
		}
		request.getSession().removeAttribute(ExpedientIniciarController.CLAU_SESSIO_TASKID);
		request.getSession().removeAttribute(ExpedientIniciarController.CLAU_SESSIO_NUMERO);
		request.getSession().removeAttribute(ExpedientIniciarController.CLAU_SESSIO_TITOL);
		request.getSession().removeAttribute(ExpedientIniciarController.CLAU_SESSIO_FORM_VALIDAT);
		request.getSession().removeAttribute(ExpedientIniciarController.CLAU_SESSIO_FORM_COMMAND);
		request.getSession().removeAttribute(ExpedientIniciarController.CLAU_SESSIO_FORM_VALORS);
	}
	public static String getClauSessioCampRegistre(String campCodi) {
		return ExpedientIniciarController.CLAU_SESSIO_PREFIX_REGISTRE + campCodi;
	}



	private boolean potIniciarExpedientTipus(ExpedientTipus expedientTipus) {
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.CREATE}) != null;
	}

	private void generarTaskIdSessio(HttpServletRequest request) {
		request.getSession().setAttribute(
				ExpedientIniciarController.CLAU_SESSIO_TASKID,
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
				IniciadorTipus.INTERN,
				null,
				null,
				null,
				null);
	}



	private static final Log logger = LogFactory.getLog(ExpedientIniciarController.class);

}
