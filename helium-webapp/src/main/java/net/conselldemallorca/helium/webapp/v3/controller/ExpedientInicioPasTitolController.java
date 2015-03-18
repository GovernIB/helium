/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientInicioPasTitolCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador pel pas del titol de l'inici d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInicioPasTitolController extends BaseExpedientController {
	@Autowired
	protected ExpedientService expedientService;

	@RequestMapping(value = "form", method = RequestMethod.POST)
	public String iniciarPasTitolPost(
			HttpServletRequest request, 
			@RequestParam(value = "definicioProcesId", required = false) 
			Long definicioProcesId, 
			@RequestParam(value = "accio", required = false) String accio, 
			@Valid @ModelAttribute ExpedientInicioPasTitolCommand expedientInicioPasTitolCommand, 
			BindingResult result, 
			SessionStatus status, 
			Model model) {
		if ("iniciar".equals(accio)) {
			ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientInicioPasTitolCommand.getExpedientTipusId());
			
			Validator validator = null;
			try {
				validator = new ExpedientInicioPasTitolValidator(expedientTipus, expedientService);
				validator.validate(expedientInicioPasTitolCommand, result);
			} catch (Exception ex) {
				validator = null;
				MissatgesHelper.error(request, getMessage(request, "error.validacio") + ": " + ex.getLocalizedMessage());
				logger.error(getMessage(request, "error.validacio"), ex);
			}
			DefinicioProcesDto definicioProces = null;
			if (definicioProcesId != null) {
				definicioProces = dissenyService.getById(definicioProcesId);
			} else {
				definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientInicioPasTitolCommand.getExpedientTipusId());
			}
			if (result.hasErrors() || validator == null) {
				if (validator != null)
					MissatgesHelper.error(request, result, getMessage(request, "error.validacio"));
				model.addAttribute(expedientInicioPasTitolCommand);
				model.addAttribute("definicioProces", definicioProces);
				model.addAttribute("anysSeleccionables", getAnysSeleccionables());
				model.addAttribute("expedientTipus", expedientTipus);
				model.addAttribute("entornId", expedientInicioPasTitolCommand.getEntornId());
				model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
				return "v3/expedient/iniciarPasTitol";
			}
			try {
				ExpedientDto iniciat = iniciarExpedient(request, expedientInicioPasTitolCommand.getEntornId(), expedientInicioPasTitolCommand.getExpedientTipusId(), definicioProcesId, expedientInicioPasTitolCommand.getNumero(), expedientInicioPasTitolCommand.getTitol(), expedientInicioPasTitolCommand.getAny());
				MissatgesHelper.info(request, getMessage(request, "info.expedient.iniciat", new Object[] { iniciat.getIdentificador() }));
				ExpedientIniciController.netejarSessio(request);
			} catch (Exception ex) {
				if (ex.getCause() != null && ex instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " : " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.iniciar.expedient") + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut iniciar l'expedient", ex);
		        }
			}
		}
		return modalUrlTancar();
	}

	@SuppressWarnings("unchecked")
	protected synchronized ExpedientDto iniciarExpedient(HttpServletRequest request, Long entornId, Long expedientTipusId, Long definicioProcesId, String numero, String titol, Integer any) {
		Map<String, Object> valorsSessio = (Map<String, Object>) request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_FORM_VALORS);
		return expedientService.create(entornId, null, expedientTipusId, definicioProcesId, any, numero, titol, null, null, null, null, false, null, null, null, null, null, null, false, null, null, false, valorsSessio, null, IniciadorTipusDto.INTERN, null, null, null, null);
	}

	@RequestMapping(value = "/canviAny/{anySel}/{entornId}/{expedientTipusId}", method = RequestMethod.GET)
	@ResponseBody
	public String getExpedientTipus(HttpServletRequest request, @PathVariable int anySel, @PathVariable Long entornId, @PathVariable Long expedientTipusId) {
		String number = expedientService.getNumeroExpedientActual(entornId, expedientTipusId, anySel);
		return JSONValue.toJSONString(number);
	}

	public List<ParellaCodiValorDto> getAnysSeleccionables() {
		List<ParellaCodiValorDto> anys = new ArrayList<ParellaCodiValorDto>();
		int anyActual = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 5; i++) {
			anys.add(new ParellaCodiValorDto(String.valueOf(anyActual - i), anyActual - i));
		}
		return anys;
	}

	protected class ExpedientInicioPasTitolValidator implements Validator {
		private ExpedientTipusDto tipus;
		private ExpedientService expedientService;

		public ExpedientInicioPasTitolValidator(ExpedientTipusDto tipus, ExpedientService expedientService) {
			this.tipus = tipus;
			this.expedientService = expedientService;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientInicioPasTitolCommand.class);
		}

		public void validate(Object target, Errors errors) {
			ExpedientInicioPasTitolCommand command = (ExpedientInicioPasTitolCommand) target;
			if (tipus == null) {
				errors.reject("error.expedienttipus.desconegut");
			} else {
				if (tipus.isTeNumero() && tipus.isDemanaNumero()) {
					if (command.getNumero() == null || command.getNumero().isEmpty())
						errors.rejectValue("numero", "not.blank");
					else if (expedientService.existsExpedientAmbEntornTipusINumero(command.getEntornId(), command.getExpedientTipusId(), command.getNumero()))
						errors.rejectValue("numero", "error.expedient.numerorepetit");
				}				
				if (tipus.isTeNumero() && tipus.isDemanaTitol()) {
					if (command.getTitol() == null || command.getTitol().isEmpty())
						errors.rejectValue("titol", "not.blank");
					else if (expedientService.existsExpedientAmbEntornTipusITitol(command.getEntornId(), command.getExpedientTipusId(), command.getTitol()))
						errors.rejectValue("titol", "error.expedient.titolrepetit");
				}
				if (tipus.isSeleccionarAny() && command.getAny() == null)
					errors.rejectValue("any", "not.blank");
			}
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
//				new CustomBooleanEditor(false));
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
//		binder.registerCustomEditor(
//				TerminiDto.class,
//				new TerminiTypeEditorHelper());
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}

	private static final Log logger = LogFactory.getLog(ExpedientIniciController.class);
}
