/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.integracio.ValidationException;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.TokenDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per a la gestió dels tokens d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTokenController extends BaseController {

	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientTokenController(
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}



	@ModelAttribute("command")
	public ExpedientTokenRetrocedirCommand populateCommand(
			@RequestParam(value = "tokenId", required = false) String tokenId) {
		if (tokenId != null) {
			ExpedientTokenRetrocedirCommand command = new ExpedientTokenRetrocedirCommand();
			command.setTokenId(tokenId);
			return command;
		}
		return null;
	}

	@RequestMapping(value = "/expedient/tokens")
	public String tokens(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				setCommonModelAttributes(id, expedient, model);
				model.addAttribute(
						"tokens",
						expedientService.getAllTokens(id));
				return "expedient/tokens";
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/tokenRetrocedir", method = RequestMethod.GET)
	public String tokenRetrocedirGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "tokenId", required = false) String tokenId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				setArrivingNodeNames(id, tokenId, expedient, model);
				return "expedient/tokenRetrocedir";
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/tokenRetrocedir", method = RequestMethod.POST)
	public String tokenRetrocedirPost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientTokenRetrocedirCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					new ExpedientTokenRetrocedirValidator().validate(command, result);
			        if (result.hasErrors()) {
			        	setArrivingNodeNames(id, command.getTokenId(), expedient, model);
			        	return "expedient/tokenRetrocedir";
			        }
					try {
						expedientService.tokenRetrocedir(
								command.getTokenId(),
								command.getNodeName(),
								command.isCancelTasks());
						missatgeInfo(request, getMessage("info.token.retrocedit") );
						status.setComplete();
					} catch (Exception ex) {
						if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
							missatgeError(
				        			request,
				        			ex.getCause().getMessage());
						} else {
							missatgeError(
				        			request,
				        			getMessage("error.retrocedir.token"),
				        			(ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage());
						}
			        	logger.error("No s'ha pogut retrocedit el token", ex);
					}
				}
				return "redirect:/expedient/tokens.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	public class ExpedientTokenRetrocedirValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "nodeName", "not.blank");
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

	private void setCommonModelAttributes(String id, ExpedientDto expedient, ModelMap model) {
		model.addAttribute(
				"expedient",
				expedient);
		model.addAttribute(
				"arbreProcessos",
				expedientService.getArbreInstanciesProces(id, false));
		model.addAttribute(
				"instanciaProces",
				expedientService.getInstanciaProcesById(id, true));
	}
	
	private void setArrivingNodeNames(
			String id,
			String tokenId,
			ExpedientDto expedient,
			ModelMap model) {
		setCommonModelAttributes(id, expedient, model);
		for (TokenDto token: expedientService.getAllTokens(id)) {
			if (token.getId().equals(tokenId)) {
				model.addAttribute("token", token);
				break;
			}
		}
		model.addAttribute("arrivingNodeNames", expedientService.findArrivingNodeNames(tokenId));
	}

	private static final Log logger = LogFactory.getLog(ExpedientTokenController.class);

}
