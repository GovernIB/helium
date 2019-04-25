/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTascaService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTascaReassignarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/**
 * Controlador per la reassignació de tasques dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTasquesReassignarController extends BaseExpedientController {

	@Autowired
	private ExpedientTascaService expedientTascaService;
	@Autowired
	private AplicacioService aplicacioService;



	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reassignar", method = RequestMethod.GET)
	public String tascaReassignarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		ExpedientTascaReassignarCommand expedientTascaReassignarCommand = new ExpedientTascaReassignarCommand();
		model.addAttribute(expedientTascaReassignarCommand);
		return "v3/expedient/tasca/reassignar";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reassignar", method = RequestMethod.POST)
	public String tascaReassignarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("expedientTascaReassignarCommand") ExpedientTascaReassignarCommand expedientTascaReassignarCommand,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		String tipus = request.getParameter("tipusExpressio"); 
		if ("submit".equals(submit) || submit.length() == 0) {
			new TascaReassignarValidator().setTipus(tipus).validate(expedientTascaReassignarCommand, result);
	        if (result.hasErrors()) {
				MissatgesHelper.error(request, result, getMessage(request, "error.validacio"));
	        	return "v3/expedient/tasca/reassignar";
	        }
			try {
				String expression = expedientTascaReassignarCommand.getExpression();
				if ("user".equals(tipus)) {
					expression = "user(" + expedientTascaReassignarCommand.getUsuari() + ")";
				} else if ("grup".equals(tipus)) {
					expression = "group(" + expedientTascaReassignarCommand.getGrup() + ")";
				}
				expedientTascaService.reassignar(
						expedientId,
						tascaId,
						expression);
				MissatgesHelper.success(request, getMessage(request, "info.tasca.reassignada"));
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.reassignar.tasca", new Object[] { tascaId } ));
	        	logger.error("No s'ha pogut reassignar la tasca " + tascaId, ex);
	        	return "v3/expedient/tasca/reassignar";
			}
		}
		
		return modalUrlTancar(false);
	}
	
	@RequestMapping(value = "/persona/suggest/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String personaSuggest(
			@PathVariable String text,
			Model model) {
		List<PersonaDto> lista = aplicacioService.findPersonaLikeNomSencer(text);
		String json = "[";
		for (PersonaDto persona: lista) {
			json += "{\"codi\":\"" + persona.getCodi() + "\", \"nom\":\"" + persona.getNomSencer() + "\"},";
		}
		if (json.length() > 1) json = json.substring(0, json.length() - 1);
		json += "]";
		return json;
	}

	@RequestMapping(value = "/persona/suggestInici/{text}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String personaSuggestInici(
			@PathVariable String text,
			Model model) {
		PersonaDto persona = aplicacioService.findPersonaAmbCodi(text);
		if (persona != null) {
			return "{\"codi\":\"" + persona.getCodi() + "\", \"nom\":\"" + persona.getNomSencer() + "\"}";
		}
		return null;
	}
	
	private class TascaReassignarValidator implements Validator {
		private String tipus;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientTascaReassignarCommand.class);
		}
		public void validate(Object target, Errors errors) {
			if ("user".equals(tipus)) {
				ValidationUtils.rejectIfEmpty(errors, "usuari", "not.blank");
			} else if ("grup".equals(tipus)) {
				ValidationUtils.rejectIfEmpty(errors, "grup", "not.blank");
			} else if ("expr".equals(tipus)) {
				ValidationUtils.rejectIfEmpty(errors, "expression", "not.blank");
			}
		}
		public TascaReassignarValidator setTipus(String tipus) {
			this.tipus = tipus;
			return this;
		}
		
	}

	private static final Log logger = LogFactory.getLog(ExpedientTasquesReassignarController.class);
}
