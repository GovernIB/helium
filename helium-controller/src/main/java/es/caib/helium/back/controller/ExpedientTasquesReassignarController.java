/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTascaReassignarCommand;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.client.engine.model.ReassignTaskData.ScriptLanguage;
import es.caib.helium.client.integracio.persones.model.Persona;
import es.caib.helium.client.model.ParellaCodiValor;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTascaService;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador per la reassignació de tasques dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTasquesReassignarController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
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
		var expedient = expedientService.findAmbId(expedientId);
		ExpedientTascaReassignarCommand expedientTascaReassignarCommand = new ExpedientTascaReassignarCommand();
		expedientTascaReassignarCommand.setMotorTipus(expedient.getTipus().getMotorTipus());
		expedientTascaReassignarCommand.setExpressionLanguage(ScriptLanguage.JAVASCRIPT_SCRIPTING_LANGUAGE);
		model.addAttribute(expedientTascaReassignarCommand);
		model.addAttribute("tipusExpressio", "user");
		model.addAttribute("languages", getLangauges());
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
				model.addAttribute("languages", getLangauges());
				model.addAttribute("tipusExpressio", tipus);
	        	return "v3/expedient/tasca/reassignar";
	        }
			try {
				String expression = expedientTascaReassignarCommand.getExpression();
				if ("user".equals(tipus)) {
					expression = "user(" + expedientTascaReassignarCommand.getUsuari() + ")";
				} else if ("grup".equals(tipus)) {
					var grups = Arrays.stream(expedientTascaReassignarCommand.getGrups())
							.filter(g -> !g.isBlank())
							.collect(Collectors.joining(","));
					expression = "group(" + grups + ")";
				}
				expedientTascaService.reassignar(
						expedientId,
						tascaId,
						expression);
				MissatgesHelper.success(request, getMessage(request, "info.tasca.reassignada"));
			} catch (Exception ex) {
				String errMsg = getMessage(request, "expedient.tasca.accio.reassignar.errror", new Object[] {tascaId, ExceptionUtils.getRootCauseMessage(ex)} );
				MissatgesHelper.error(request, errMsg);
	        	logger.error(errMsg, ex);
				model.addAttribute("languages", getLangauges());
				model.addAttribute("tipusExpressio", tipus);
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
		List<Persona> lista = aplicacioService.findPersonaLikeNomSencer(text);
		String json = "[";
		for (Persona persona: lista) {
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

	@RequestMapping(value = "/grup/{grup}/persones", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String grupPersones(
			@PathVariable String grup,
			HttpServletResponse response) {
		var persones = aplicacioService.findPersonesAmbGrup(grup);
		if (persones != null && !persones.isEmpty()) {
			var personesList = "[" + persones.stream().map(p -> "{\"nom\":\"" + p.getNomSencer() + "\"}").collect(Collectors.joining(",")) + "]";
			return personesList;
		} else {
			return "Grup sense usuaris";
		}
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
//				ValidationUtils.rejectIfEmpty(errors, "grups", "not.blank");
				ValidationUtils.rejectIfEmpty(errors, "grups[0]", "not.blank");
			} else if ("expr".equals(tipus)) {
				ValidationUtils.rejectIfEmpty(errors, "expression", "not.blank");
			}
		}
		public TascaReassignarValidator setTipus(String tipus) {
			this.tipus = tipus;
			return this;
		}
		
	}

	private List<ParellaCodiValor> getLangauges() {
		return Arrays.stream(ScriptLanguage.values()).map(s -> new ParellaCodiValor(s.name(), s.language)).collect(Collectors.toList());
	}
	private static final Log logger = LogFactory.getLog(ExpedientTasquesReassignarController.class);
}
