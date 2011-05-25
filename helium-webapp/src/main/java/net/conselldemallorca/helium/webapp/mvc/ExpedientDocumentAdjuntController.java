/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;



/**
 * Controlador per a modificar els documents adjunts dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientDocumentAdjuntController extends BaseController {

	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientDocumentAdjuntController(
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public DocumentExpedientCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id") String id,
			@RequestParam(value = "adjuntId", required = false) String adjuntId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			DocumentExpedientCommand command = new DocumentExpedientCommand();
			if (adjuntId == null)
				command.setData(new Date());
			return command;
		}
		return null;
	}

	@RequestMapping(value = "/expedient/documentAdjuntForm", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				return "expedient/documentAdjuntForm";
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
			}
			return "redirect:/expedient/consulta.html";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/documentAdjuntForm", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam("contingut") final MultipartFile multipartFile,
			@ModelAttribute("command") DocumentExpedientCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					new DocumentAdjuntCrearValidator().validate(command, result);
			        if (result.hasErrors())
			        	return "expedient/documentAdjuntForm";
			        String nomArxiu = "arxiu";
			        if (multipartFile.getSize() > 0) {
						try {
							nomArxiu = multipartFile.getOriginalFilename();
						} catch (Exception ignored) {}
					}
			        try {
				        expedientService.guardarAdjunt(
				        		id,
				        		null,
				        		command.getNom(),
				        		command.getData(),
				        		nomArxiu,
				        		(multipartFile.getSize() > 0) ? command.getContingut() : null);
						missatgeInfo(request, "El document s'ha adjuntat correctament");
						status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut crear el document adjunt", ex);
			        }
				}
				return "redirect:/expedient/documents.html?id=" + id;
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	public class DocumentAdjuntCrearValidator implements Validator {
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "nom", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "data", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "contingut", "not.blank");
		}
	}



	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientDocumentAdjuntController.class);

}
