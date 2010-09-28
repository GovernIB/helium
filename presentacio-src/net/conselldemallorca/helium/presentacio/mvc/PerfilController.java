/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.ParellaCodiValor;
import net.conselldemallorca.helium.model.hibernate.Persona;
import net.conselldemallorca.helium.model.service.PersonaService;
import net.conselldemallorca.helium.presentacio.mvc.interceptor.PersonaInterceptor;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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



/**
 * Controlador per la gestió d'entorns
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
@RequestMapping("/perfil/*.html")
public class PerfilController extends BaseController {

	private PersonaService personaService;
	private Validator validator;



	@Autowired
	public PerfilController(
			PersonaService personaService) {
		this.personaService = personaService;
	}

	@ModelAttribute("sexes")
	public ParellaCodiValor[] populateSexes() {
		ParellaCodiValor[] resposta = new ParellaCodiValor[2];
		resposta[0] = new ParellaCodiValor("Home", Persona.Sexe.SEXE_HOME);
		resposta[1] = new ParellaCodiValor("Dona", Persona.Sexe.SEXE_DONA);
		return resposta;
	}

	@RequestMapping(value = "info")
	public String consulta(
			HttpServletRequest request,
			ModelMap model) {
		model.addAttribute("info", getPersonaActual(request));
		return "perfil/info";
	}

	@RequestMapping(value = "form", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			ModelMap model) {
		model.addAttribute("command", getPersonaActual(request));
		return "perfil/form";
	}

	@RequestMapping(value = "form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Persona command,
			BindingResult result,
			SessionStatus status) {
		if ("submit".equals(submit) || submit.length() == 0) {
			if (validator != null)
				validator.validate(command, result);
	        if (result.hasErrors()) {
	        	return "perfil/form";
	        }
	        try {
	        	personaService.updatePerfil(command);
	        	missatgeInfo(request, "El perfil s'ha guardat amb èxit");
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut guardar el perfil", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar el registre", ex);
	        	return "perfil/form";
	        }
	        return "redirect:/perfil/info.html";
		}
		return "redirect:/perfil/info.html";
	}

	@RequestMapping(value = "contrasenya", method = RequestMethod.GET)
	public String contrasenyaGet(
			ModelMap model) {
		model.addAttribute("command", new CanviContrasenyaCommand());
		return "perfil/contrasenya";
	}

	@RequestMapping(value = "contrasenya", method = RequestMethod.POST)
	public String contrasenyaPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") CanviContrasenyaCommand command,
			BindingResult result,
			SessionStatus status) {
		if ("submit".equals(submit) || submit.length() == 0) {
			if (validator != null)
				new CanviContrasenyaValidator().validate(command, result);
	        if (result.hasErrors()) {
	        	return "perfil/contrasenya";
	        }
	        try {
	        	Persona persona = getPersonaActual(request);
	        	personaService.canviContrasenyaPerfil(persona.getId(), command.getContrasenya());
	        	missatgeInfo(request, "La contrasenya s'ha modificat amb èxit");
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut guardar el perfil", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut modificar la contrasenya", ex);
	        	return "perfil/contrasenya";
	        }
	        return "redirect:/perfil/info.html";
		}
		return "redirect:/perfil/info.html";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				boolean.class,
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}



	@Resource(name = "annotationValidator")
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public class CanviContrasenyaValidator implements Validator {
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(CanviContrasenyaCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "contrasenya", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "repeticio", "not.blank");
			CanviContrasenyaCommand command = (CanviContrasenyaCommand)target;
			// La contrasenya i la repetició han de coincidir
			if (command.getContrasenya() != null) {
				if (command.getRepeticio() == null || !command.getRepeticio().equals(command.getContrasenya())) {
					errors.rejectValue("contrasenya", "error.contrasenya.repeticio");
					errors.rejectValue("repeticio", "error.contrasenya.repeticio");
				}
			}
		}
	}

	private Persona getPersonaActual(
			HttpServletRequest request) {
		net.conselldemallorca.helium.integracio.plugins.persones.Persona actual = (net.conselldemallorca.helium.integracio.plugins.persones.Persona)request.getSession().getAttribute(PersonaInterceptor.VARIABLE_SESSIO_PERSONA);
		Persona persona = personaService.findPersonaAmbCodi(actual.getCodi());
		return personaService.getPerfilInfo(persona.getId());
	}

	private static final Log logger = LogFactory.getLog(PerfilController.class);

}
