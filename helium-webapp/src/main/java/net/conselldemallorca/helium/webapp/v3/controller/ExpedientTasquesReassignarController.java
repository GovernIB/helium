/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.integracio.ValidationException;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTascaReassignarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per la reassignació de tasques dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTasquesReassignarController extends BaseExpedientController {

	@Autowired
	private TascaService tascaService;
	
	@Autowired
	private ExpedientService expedientService;

	@ModelAttribute("command")
	public ExpedientTascaReassignarCommand populateCommand(
			@RequestParam(value = "tascaId", required = false) String tascaId) {
		
		ExpedientTascaReassignarCommand command = new ExpedientTascaReassignarCommand();
		if (tascaId != null) {
			command.setTaskId(tascaId);
		}
		
		return command;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reassignar", method = RequestMethod.GET)
	public String tascaReassignarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarOReassignarExpedient(expedient)) {
				atributsModel(
	        			entorn,
	        			expedient,
	        			tascaId,
	        			model);
				NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
				return "v3/expedient/tasca/reassignar";
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
		}
		
		return "/v3/utils/modalTancar";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reassignar", method = RequestMethod.POST)
	public String tascaReassignarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientTascaReassignarCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarOReassignarExpedient(expedient)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					new TascaReassignarValidator().validate(command, result);
			        if (result.hasErrors()) {
			        	atributsModel(
			        			entorn,
			        			expedient,
			        			tascaId,
			        			model);
						NoDecorarHelper.marcarNoCapsaleraNiPeu(request);

						MissatgesHelper.error(request, result, getMessage(request, "error.validacio"));
			        	return "v3/expedient/tasca/reassignar";
			        }
					try {
						expedientService.reassignarTasca(
								entorn.getId(),
								command.getTaskId(),
								command.getExpression());
						MissatgesHelper.info(request, getMessage(request, "info.tasca.reassignada"));
					} catch (Exception ex) {
						if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
							MissatgesHelper.error(request, getMessage(request, ex.getCause().getMessage()));
						} else {
							MissatgesHelper.error(request, getMessage(request, "error.reassignar.tasca", new Object[] { command.getTaskId() } ));
						}
						atributsModel(
			        			entorn,
			        			expedient,
			        			tascaId,
			        			model);
			        	logger.error("No s'ha pogut reassignar la tasca " + command.getTaskId(), ex);
						NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
			        	return "v3/expedient/tasca/reassignar";
					}
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
		}
		
		return "/v3/utils/modalTancar";
	}
	
	private class TascaReassignarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientTascaReassignarCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "expression", "not.blank");
		}
	}
	
	private void atributsModel(
			EntornDto entorn,
			ExpedientDto expedient,
			String tascaId,
			ModelMap model) {
		ExpedientTascaDto tasca = tascaService.getTascaPerExpedientId(expedient.getId(), tascaId);
    	
		model.addAttribute("expedient",expedient);
		model.addAttribute("tasca", tasca);
		model.addAttribute("arbreProcessos",expedientService.getArbreInstanciesProces(Long.valueOf(tasca.getProcessInstanceId())));
		model.addAttribute("instanciaProces",expedientService.getInstanciaProcesById(tasca.getProcessInstanceId()));
	}

	private static final Log logger = LogFactory.getLog(ExpedientTasquesReassignarController.class);
}
