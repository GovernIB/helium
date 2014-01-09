/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió de tipus d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTipusFormController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private PluginService pluginService;

	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public ExpedientTipusFormController(
			DissenyService dissenyService,
			PermissionService permissionService,
			PluginService pluginService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
		this.pluginService = pluginService;
		this.additionalValidator = new ExpedientTipusValidator(dissenyService);
	}

	@ModelAttribute("command")
	public ExpedientTipus populateCommand(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			ExpedientTipus command = dissenyService.getExpedientTipusById(id);
			return command;
		}
		return new ExpedientTipus();
	}

	@RequestMapping(value = "/expedientTipus/form", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (potDissenyarEntorn(entorn)) {
				ExpedientTipus command = (ExpedientTipus)model.get("command");
				model.addAttribute(
						"responsableDefecte",
						getResponsableDefecte(command.getResponsableDefecteCodi()));
				return "expedientTipus/form";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.entorn"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedientTipus/form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientTipus command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (potDissenyarEntorn(entorn)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					SortedMap<Integer, SequenciaAny> sequenciaAny = new TreeMap<Integer, SequenciaAny>();
					
					if (command.isReiniciarCadaAny()) {
						//command.setSequencia(1);

						// eliminam les sequencies actuals
						for (SequenciaAny sa: command.getSequenciaAny().values()) {
							sa.setExpedientTipus(null);
						}
						command.getSequenciaAny().clear();
						
						String[] seqAny = request.getParameterValues("seqany");
						String[] seqSeq = request.getParameterValues("seqseq");
						
						// afegim les noves
						if (seqAny != null) {
							for (int i = 0; i < seqAny.length; i++) {
								Integer any = null;
								Long seq = null;
								try {
									any = Integer.parseInt(seqAny[i]);
									if (sequenciaAny.containsKey(any)) {
										result.rejectValue("", "error.expedientTipus.any.repetit");
										any = null;
									}
								} catch (NumberFormatException ex) {
									result.rejectValue("", "error.expedientTipus.any.format.any");
								}
								try {
									seq = Long.parseLong(seqSeq[i]);
								} catch (NumberFormatException ex) {
									result.rejectValue("", "error.expedientTipus.any.format.sequencia");
								}
								if (any != null && seq != null) {
									sequenciaAny.put(any, new SequenciaAny(command, any, seq));
								}
							}
						}
						command.setSequenciaAny(sequenciaAny);
					}
					annotationValidator.validate(command, result);
					additionalValidator.validate(command, result);
					
			        if (result.hasErrors()) {
			        	model.addAttribute(
								"responsableDefecte",
								getResponsableDefecte(command.getResponsableDefecteCodi()));
			        	return "expedientTipus/form";
			        }
			        try {
			        	if (command.getId() == null) {
			        		command.setEntorn(entorn);
			        		dissenyService.createExpedientTipus(command);
			        	} else {
			        		dissenyService.updateExpedientTipus(command);
			        	}
			        	missatgeInfo(request, getMessage("info.tipus.exp.guardat"));
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "expedientTipus/form";
			        }
			        return "redirect:/expedientTipus/info.html?expedientTipusId=" + command.getId();
				} else {
					if (command.getId() != null)
						return "redirect:/expedientTipus/info.html?expedientTipusId=" + command.getId();
					else
						return "redirect:/expedientTipus/llistat.html";
				}
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.entorn"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private class ExpedientTipusValidator implements Validator {
		private DissenyService dissenyService;
		public ExpedientTipusValidator(DissenyService dissenyService) {
			this.dissenyService = dissenyService;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientTipus.class);
		}
		public void validate(Object target, Errors errors) {
			ExpedientTipus command = (ExpedientTipus)target;
			ExpedientTipus repetit = dissenyService.findExpedientTipusAmbEntornICodi(
					command.getEntorn().getId(),
					command.getCodi());
			if (repetit != null && !repetit.getId().equals(command.getId())) {
				errors.rejectValue("codi", "error.expedienttipus.codi.repetit");
			}
		}
	}

	private PersonaDto getResponsableDefecte(String codi) {
		if (codi == null)
			return null;
		return pluginService.findPersonaAmbCodi(codi);
	}
	private boolean potDissenyarEntorn(Entorn entorn) {
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusFormController.class);

}
