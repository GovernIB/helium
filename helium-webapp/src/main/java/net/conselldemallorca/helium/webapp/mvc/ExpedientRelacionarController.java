/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
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
 * Controlador per a la relació d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientRelacionarController extends BaseController {

	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientRelacionarController(
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("relacionarCommand")
	public ExpedientRelacionarCommand populateRelacionarCommand() {
		return new ExpedientRelacionarCommand();
	}

	@RequestMapping(value = "/expedient/relacionar", method = RequestMethod.GET)
	public String relacionarGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(id, false, false, false);
				model.addAttribute(
						"instanciaProces",
						instanciaProces);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(id));
				model.addAttribute(
						"relacionarCommand",
						new ExpedientRelacionarCommand());
				return "expedient/relacionar";
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/relacionar", method = RequestMethod.POST)
	public String relacionarPost(
			HttpServletRequest request,
			@ModelAttribute("relacionarCommand") ExpedientRelacionarCommand command,
			@RequestParam(value = "submit", required = true) String action,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (command.getExpedientIdDesti() != null && "submit".equals(action)) {
				ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(command.getInstanciaProcesId());
				if (potModificarExpedient(expedient)) {
					try {
						expedientService.createRelacioExpedient(
								command.getExpedientIdOrigen(),
								command.getExpedientIdDesti());
						missatgeInfo(request, getMessage("expedient.relacionar.ok"));
					} catch (Exception ex) {
					  	Long entornId = entorn.getId();
						String numeroExpedient = expedient.getIdentificador();
						logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+"No s'ha pogut relacionar l'expedient " + expedient.getIdentificador(), ex);
						missatgeError(request, getMessage("error.expedient.relacionar"), ex.getLocalizedMessage());
			        	return "expedient/info";
					}
				} else {
					missatgeError(request, getMessage("error.permisos.modificar.expedient"));
					return "redirect:/expedient/consulta.html";
				}
			}
			return "redirect:/expedient/info.html?id=" + command.getInstanciaProcesId();
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/relacioDelete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@ModelAttribute("relacionarCommand") ExpedientRelacionarCommand command,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.getById(command.getExpedientIdOrigen());
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.deleteRelacioExpedient(
							command.getExpedientIdOrigen(),
							command.getExpedientIdDesti());
					missatgeInfo(request, getMessage("expedient.relacio.esborrar.ok"));
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.expedient.relacio.esborrar"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut relacionar l'expedient " + expedient.getIdentificador(), ex);
				}
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
			}
			return "redirect:/expedient/info.html?id=" + expedient.getProcessInstanceId();
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/suggest", method = RequestMethod.GET)
	public String suggestAction(
			HttpServletRequest request,
			@RequestParam(value = "q", required = true) String text,
			ModelMap model) {
		String textDecoded = null;
		try {
			textDecoded = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + textDecoded + ": " + e.getMessage());
		}
		Entorn entorn = getEntornActiu(request);
		if (entorn != null && textDecoded != null) {
			model.addAttribute(
					"expedients",
					expedientService.findAmbEntornLikeIdentificador(
							entorn.getId(),
							textDecoded));
		}
		return "expedient/suggest";
	}



	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientRelacionarController.class);

}
