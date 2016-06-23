package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.TokenDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTokenService;
import net.conselldemallorca.helium.webapp.v3.command.TokenExpedientCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTokenV3Controller extends BaseExpedientController {

	@Autowired
	private ExpedientTokenService expedientTokenService;
	@Resource
	private JbpmHelper jbpmHelper;



	@RequestMapping(value = "/{expedientId}/tokens", method = RequestMethod.GET)
	public String tokens(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);	
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, List<TokenDto>> tokens = new LinkedHashMap<InstanciaProcesDto, List<TokenDto>>();
		if (expedient.isPermisAdministration()) {
			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
				List<TokenDto> tokensInstanciaProces = null;
				if (instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
					tokensInstanciaProces = expedientTokenService.findAmbInstanciaProces(
							expedientId,
							instanciaProces.getId());
				}
				tokens.put(instanciaProces, tokensInstanciaProces);
			}
			model.addAttribute("expedient",expedient);
			model.addAttribute("tokens",tokens);
			model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		}		
		return "v3/expedientToken";
	}
	
	@RequestMapping(value = "/{expedientId}/tokens/{procesId}", method = RequestMethod.GET)
	public String subTokens(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
		Map<InstanciaProcesDto, List<TokenDto>> tokens = new LinkedHashMap<InstanciaProcesDto, List<TokenDto>>();
		if (expedient.isPermisAdministration()) {
			tokens.put(
					instanciaProces,
					expedientTokenService.findAmbInstanciaProces(
							expedientId,
							instanciaProces.getId()));
			model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
			model.addAttribute("expedient",expedient);
			model.addAttribute("tokens",tokens);
		}
		return "v3/procesTokens";
	}
	
	@RequestMapping(value = "/{expedientId}/{tokenId}/tokenActivar", method = RequestMethod.GET)
	@ResponseBody
	public boolean tokenActivar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tokenId,
			Model model) {
		boolean response = false; 
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		TokenDto token = expedientTokenService.findById(
				expedientId,
				tokenId.toString());
		boolean activar = token.getEnd()!=null;
		String cadenaMissatgeOk = activar ? "expedient.info.token.activat" : "expedient.info.token.desactivat";
		String cadenaMissatgeError = activar ? "error.activar.token" : "error.desactivar.token";
		if (expedient.isPermisAdministration()){
			try {
				if (expedientTokenService.canviarEstatActiu(
						expedientId,
						tokenId,
						activar)){
					MissatgesHelper.success(request, getMessage(request, cadenaMissatgeOk));
					response = true;
				} else {
					MissatgesHelper.error(request, getMessage(request, cadenaMissatgeError));
				}
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, cadenaMissatgeError));
				logger.error("No s'ha pogut activar/desactivar el token", ex);
			}
		}
		return response;
	}
	
	@RequestMapping(value = "/{expedientId}/{tokenId}/tokenRetrocedir", method = RequestMethod.GET)
	public String tokenRetrocedir(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tokenId,
			Model model) {
		TokenExpedientCommand command= new TokenExpedientCommand();
		TokenDto token = expedientTokenService.findById(
				expedientId,
				tokenId);
		model.addAttribute("token",token);
		model.addAttribute(
				"arrivingNodeNames",
				expedientTokenService.findArrivingNodeNames(
						expedientId,
						tokenId.toString()));
		model.addAttribute("tokenExpedientCommand",command);
		return "v3/expedientTokenRetrocedir";
	}

	@RequestMapping(value="/{expedientId}/{tokenId}/tokenRetrocedir", method = RequestMethod.POST)
	public String tokenRetrocedirPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tokenId,
			@ModelAttribute TokenExpedientCommand command, 
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		try{
			new TokenRetrocedirValidator().validate(command, result);
			if (result.hasErrors()) {
				model.addAttribute(
						"arrivingNodeNames",
						expedientTokenService.findArrivingNodeNames(
								expedientId,
								tokenId.toString()));
	        	return "v3/expedientTokenRetrocedir";
	        }
			expedientTokenService.retrocedir(
					expedientId,
					tokenId,
					command.getNodeRetrocedir(),
					command.isCancelar());
			MissatgesHelper.success(request, getMessage(request, "info.token.retrocedit") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.retrocedir.token", new Object[] {String.valueOf(tokenId)} ));
	    	logger.error("No s'ha pogut retrocedir el token " + String.valueOf(tokenId), ex);
		}
		
		return modalUrlTancar(false);
	}

	public class TokenRetrocedirValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "nodeRetrocedir", "not.blank");
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTokenV3Controller.class);

}
