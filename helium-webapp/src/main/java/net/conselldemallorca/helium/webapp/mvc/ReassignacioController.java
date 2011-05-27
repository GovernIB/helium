package net.conselldemallorca.helium.webapp.mvc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;
import net.conselldemallorca.helium.core.model.service.ReassignacioService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Controlador per la reassignació de tasques entre usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Controller
public class ReassignacioController extends BaseController {
	
	private ReassignacioService reassignacioService;
	private Validator additionalValidator;
	
	@Autowired
	public ReassignacioController(
			ReassignacioService reassignacioService) {
		this.reassignacioService = reassignacioService;
		additionalValidator = new ReassignacioValidator(reassignacioService);
	}
	
	@ModelAttribute("command")
	public ReassignacioCommand populateCommand(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			Reassignacio reassignacio = reassignacioService.findReassignacioById(id);
			ReassignacioCommand command = new ReassignacioCommand();
			command.setId(reassignacio.getId());
			command.setUsuariOrigen(reassignacio.getUsuariOrigen());
			command.setUsuariDesti(reassignacio.getUsuariDesti());
			command.setDataInici(reassignacio.getDataInici());
			command.setDataFi(reassignacio.getDataFi());
			command.setDataCancelacio(reassignacio.getDataCancelacio());
			return command;
		}
		return new ReassignacioCommand();
	}
	
	@RequestMapping(value = "/reassignar/llistat", method = RequestMethod.GET)
	public String reassignarGet(
			HttpServletRequest request,
			ModelMap model) {
		List<Reassignacio> reassignacions = reassignacioService.llistaReassignacions();
		model.addAttribute("llistat", reassignacions);
		return "reassignar/llistat";
	}
	
	@RequestMapping(value = "/reassignar/form", method = RequestMethod.GET)
	public String formGet() {
		return "reassignar/form";
	}
	
	@RequestMapping(value = "/reassignar/form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ReassignacioCommand command,
			BindingResult result,
			SessionStatus status) {
		if ("submit".equals(submit) || submit.length() == 0) {
			additionalValidator.validate(command, result);
	        if (result.hasErrors()) {
	        	return "reassignar/form";
	        }
	        try {
	        	if (command.getId() == null) {
	        		reassignacioService.createReassignacio(
	        				command.getUsuariOrigen(),
	        				command.getUsuariDesti(),
	        				command.getDataInici(),
	        				command.getDataFi(),
	        				command.getDataCancelacio());
	        	} else {
	        		reassignacioService.updateReassignacio(
	        				command.getId(),
	        				command.getUsuariOrigen(),
	        				command.getUsuariDesti(),
	        				command.getDataInici(),
	        				command.getDataFi(),
	        				command.getDataCancelacio());
	        	}
	        	missatgeInfo(request, getMessage("info.reassignacio.produit") );
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar el registre", ex);
	        	return "reassignar/form";
	        }
	        return "redirect:/reassignar/llistat.html";
		} else {
			return "redirect:/reassignar/llistat.html";
		}
	}
	
	@RequestMapping(value = "/reassignar/cancelar")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		reassignacioService.deleteReassignacio(id);
		missatgeInfo(request, getMessage("info.reassignacio.cancelat") );
		return "redirect:/reassignar/llistat.html";
	}
	
	@SuppressWarnings("unused")
	private class ReassignacioValidator implements Validator {
		private ReassignacioService reassignacioService;
		public ReassignacioValidator(ReassignacioService reassignacioService) {
			this.reassignacioService = reassignacioService;
		}
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ReassignacioCommand.class);
		}
		public void validate(Object target, Errors errors) {
			Date avui = new Date();
			ReassignacioCommand command = (ReassignacioCommand)target;
			ValidationUtils.rejectIfEmpty(errors, "usuariOrigen", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "usuariDesti", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "dataInici", "not.blank");
			ValidationUtils.rejectIfEmpty(errors, "dataFi", "not.blank");
			if ((command.getDataInici() != null) && (avui.compareTo(command.getDataInici()) > 0)) {
				errors.rejectValue("dataInici", "error.data.anterior");
			}
			if ((command.getDataFi() != null) && (avui.compareTo(command.getDataFi()) > 0)) {
				errors.rejectValue("dataFi", "error.data.anterior");
			}
			if ((command.getDataInici() != null) && (command.getDataFi() != null) && ((command.getDataFi()).compareTo(command.getDataInici()) < 0)) {
				errors.rejectValue("dataFi", "error.dataFi.anterior");
			}
		}
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
	private static final Log logger = LogFactory.getLog(PersonaController.class);
}
