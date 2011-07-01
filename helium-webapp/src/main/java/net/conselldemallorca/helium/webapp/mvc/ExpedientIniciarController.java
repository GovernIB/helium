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
import net.conselldemallorca.helium.core.model.exception.ExpedientRepetitException;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
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
				// Si l'expedient té titol i/o número redirigeix al pas per demanar aquestes dades
				if (tipus.getDemanaNumero().booleanValue() || tipus.getDemanaTitol().booleanValue()) {
					generarTaskIdSessio(request);
					if (definicioProcesId != null)
						return "redirect:/expedient/iniciarPasTitol.html?expedientTipusId=" + expedientTipusId + "&definicioProcesId=" + definicioProcesId;
					else
						return "redirect:/expedient/iniciarPasTitol.html?expedientTipusId=" + expedientTipusId;
				}
				// Si l'expedient no té titol i/o número però requereix dades inicials redirigeix
				// al pas per demanar aquestes dades
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
				// Si no requereix cap dada inicia l'expedient directament
				try {
					expedientService.iniciar(
							entorn.getId(),
							expedientTipusId,
							definicioProcesId,
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
					missatgeInfo(request, getMessage("info.expedient.iniciat") );
				} catch (ExpedientRepetitException ex) {
					missatgeError(
							request,
							getMessage("error.exist.exp.mateix.numero") );
				}catch (Exception ex) {
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
				ExpedientIniciarPasTitolController.CLAU_SESSIO_TASKID,
				"TIE_" + System.currentTimeMillis());
	}

	@SuppressWarnings("unchecked")
	private void netejarSessio(HttpServletRequest request) {
		Enumeration<String> atributs = request.getSession().getAttributeNames();
		while (atributs.hasMoreElements()) {
			String atribut = atributs.nextElement();
			if (atribut.startsWith(ExpedientIniciarRegistreController.PREFIX_REGISTRE_SESSIO))
				request.getSession().removeAttribute(atribut);
		}
		request.getSession().removeAttribute(ExpedientIniciarPasTitolController.CLAU_SESSIO_TASKID);
		request.getSession().removeAttribute(ExpedientIniciarPasTitolController.CLAU_SESSIO_NUMERO);
		request.getSession().removeAttribute(ExpedientIniciarPasTitolController.CLAU_SESSIO_TITOL);
		request.getSession().removeAttribute(ExpedientIniciarPasTitolController.CLAU_SESSIO_FORM_VALIDAT);
	}

	private static final Log logger = LogFactory.getLog(ExpedientIniciarController.class);

}
