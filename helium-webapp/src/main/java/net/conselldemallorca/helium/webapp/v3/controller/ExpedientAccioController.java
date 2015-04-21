package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina d'accions de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientAccioController extends BaseExpedientController {

	@RequestMapping(value = "/{expedientId}/accions", method = RequestMethod.GET)
	public String documents(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);		
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, List<AccioDto>> accions = new LinkedHashMap<InstanciaProcesDto, List<AccioDto>>();
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			accions.put(instanciaProces, expedientService.findAccionsVisiblesAmbProcessInstanceId(instanciaProces.getId()));
		}
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());		
		model.addAttribute("expedient", expedient);
		model.addAttribute("accions", accions);
		return "v3/expedientAccio";
	}

	@RequestMapping(value = "/{expedientId}/accions/{procesId}", method = RequestMethod.GET)
	public String refrescarTasca(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
		Map<InstanciaProcesDto, List<AccioDto>> accions = new LinkedHashMap<InstanciaProcesDto, List<AccioDto>>();
		accions.put(instanciaProces, expedientService.findAccionsVisiblesAmbProcessInstanceId(instanciaProces.getId()));
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("expedient", expedient);
		model.addAttribute("accions", accions);	
		return "v3/procesAccions";
	}
	
	@RequestMapping(value = "/{expedientId}/accio/{accioId}", method = RequestMethod.GET)
	public String accio(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			@PathVariable Long accioId, 
			Model model) {
		try {
			dissenyService.executarAccio(accioId, expedientId);
			MissatgesHelper.success(request, getMessage(request, "info.accio.executat"));
		} catch (Exception ex) {
			String nomAccio = accioId.toString();
			try {
				AccioDto accio = dissenyService.findAccioAmbId(accioId);
				nomAccio = accio.getNom();
			} catch (Exception e) {}
			if (ex.getCause() != null && (ex instanceof ValidationException || ex.getCause() instanceof ValidationException)) {
				MissatgesHelper.error(
		    			request,
		    			getMessage(request, "error.executar.accio") + " " + nomAccio + ": " + ex.getCause().getMessage());
			} else {
				MissatgesHelper.error(
		    			request,
		    			getMessage(request, "error.executar.accio") + " " + nomAccio + ": " + 
		    					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
//				MissatgesHelper.error(request, getMessage(request, "error.executar.accio") +" "+ accioId + ": "+ ex.getLocalizedMessage());
				logger.error(getMessage(request, "error.executar.accio") +" "+ accioId + ": "+ ex.getLocalizedMessage(), ex);
			}
		}
		model.addAttribute("pipellaActiva", "accions");
		return "redirect:/v3/expedient/" + expedientId;
	}

	protected static final Log logger = LogFactory.getLog(ExpedientAccioController.class);
}
