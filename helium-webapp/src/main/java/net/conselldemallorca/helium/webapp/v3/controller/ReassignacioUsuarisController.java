package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.service.ReassignacioUsuarisService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.command.ReassignacioUsuarisCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ReassignacioUsuarisValidatorHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/v3/expedient")
public class ReassignacioUsuarisController extends BaseExpedientController {
	
	@Autowired
	private ReassignacioUsuarisService reassignacioUsuarisService;
	
	@Autowired
	private TascaService tascaService;
	
	private Validator additionalValidator;
	
	@ModelAttribute("command")
	public ReassignacioUsuarisCommand populateCommand(
			@RequestParam(value = "id", required = false) Long id) {
		ReassignacioUsuarisCommand command = new ReassignacioUsuarisCommand();
		if (id != null) {
			ReassignacioDto reassignacio = reassignacioUsuarisService.findReassignacioById(id);
			command.setId(reassignacio.getId());
			command.setUsuariOrigen(reassignacio.getUsuariOrigen());
			command.setUsuariDesti(reassignacio.getUsuariDesti());
			command.setDataInici(reassignacio.getDataInici());
			command.setDataFi(reassignacio.getDataFi());
			command.setDataCancelacio(reassignacio.getDataCancelacio());
			command.setTipusExpedientId(reassignacio.getTipusExpedientId());
		}
		
		return command;
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reassignarUsuaris", method = RequestMethod.GET)
	public String formGet(HttpServletRequest request, 
			@PathVariable Long expedientId,
			@PathVariable String tascaId, 
			Model model) {
		// TODO
		//NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
		
		List<ReassignacioDto> reassignacions = reassignacioUsuarisService.llistaReassignacions();
		model.addAttribute("llistat", reassignacions);
		model.addAttribute("expedientId", expedientId);
		model.addAttribute(
				"tasca",
				tascaService.findAmbIdPerExpedient(
						tascaId,
						expedientId));
		
		return "v3/expedient/tasca/reassignarUsuaris";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reassignarUsuaris", method = RequestMethod.POST)
	public String formPost(HttpServletRequest request, 
			@PathVariable Long expedientId,
			@PathVariable String tascaId, 
			@ModelAttribute("command") ReassignacioUsuarisCommand command,
			Model model,
			BindingResult result,
			SessionStatus status) {
		
			// TODO
			//NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
			
			additionalValidator = new ReassignacioUsuarisValidatorHelper();
			
			additionalValidator.validate(command, result);
	        if (result.hasErrors()) {
	        	return "v3/expedient/tasca/reassignarUsuaris";
	        }
			
	        try {
	        	if (command.getId() == null) {
	        		reassignacioUsuarisService.createReassignacio(
	        				command.getUsuariOrigen(),
	        				command.getUsuariDesti(),
	        				command.getDataInici(),
	        				command.getDataFi(),
	        				command.getDataCancelacio(),
	        				command.getTipusExpedientId());
	        	} else {
	        		reassignacioUsuarisService.updateReassignacio(
	        				command.getId(),
	        				command.getUsuariOrigen(),
	        				command.getUsuariDesti(),
	        				command.getDataInici(),
	        				command.getDataFi(),
	        				command.getDataCancelacio(),
	        				command.getTipusExpedientId());
	        	}
	        	MissatgesHelper.info(request, getMessage(request, "info.reassignacio.produit") );
	        	status.setComplete();
	        } catch (Exception ex) {
	        	MissatgesHelper.error(request, getMessage(request, "error.proces.peticio"));
	        	logger.error("No s'ha pogut guardar el registre", ex);
	        	return "v3/expedient/tasca/reassignarUsuaris";
	        }
	        
	        return "redirect:/v3/expedient/"+expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reassignarCancelar", method = RequestMethod.POST)
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {		
		reassignacioUsuarisService.deleteReassignacio(id);
		MissatgesHelper.info(request, getMessage(request, "info.reassignacio.cancelat") );
		return "v3/expedient/tasca/reassignarUsuaris";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
	private static final Log logger = LogFactory.getLog(ReassignacioUsuarisController.class);
}
