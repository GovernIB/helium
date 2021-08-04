package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusEnumeracioCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EnumeracioDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusEnumeracioValorDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EnumeracioService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Controlador per a la pipella de enumeracions del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusEnumeracioController extends BaseExpedientTipusController {

	@Autowired
	protected EnumeracioService enumeracioService;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracions")
	public String enumeracions(HttpServletRequest request, @PathVariable Long expedientTipusId, Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(request, expedientTipusId, model, "enumeracions");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			model.addAttribute("pipellaActiva", "enumeracions");
		}
		return "v3/expedientTipusEnumeracio";
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long expedientTipusId, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, enumeracioService
				.findPerDatatable(
						entornActual.getId(),
						expedientTipusId, 
						false, // incloure globals
						paginacioParams.getFiltre(), 
						paginacioParams));
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/new", method = RequestMethod.GET)
	public String nova(HttpServletRequest request, @PathVariable Long expedientTipusId, Model model) {
		ExpedientTipusEnumeracioCommand command = new ExpedientTipusEnumeracioCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusEnumeracioCommand", command);
		return "v3/expedientTipusEnumeracioForm";
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/new", method = RequestMethod.POST)
	public String novaPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusEnumeracioCommand.Creacio.class) ExpedientTipusEnumeracioCommand command,
			BindingResult bindingResult, Model model) {
		try {
			if (bindingResult.hasErrors()) {
				return "v3/expedientTipusEnumeracioForm";
			} else {
			
				EnumeracioDto dto = ExpedientTipusEnumeracioCommand.asEnumeracioDto(command);
				EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
				
				enumeracioService.create(entornActual.getId(), expedientTipusId, dto);
				
	    		MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.enumeracio.controller.creat"));
				return modalUrlTancar(false);
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar l'enumeració", ex);
    		MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.enumeracio.controller.creat.error",
							new Object[] {ex.getLocalizedMessage()}));
			return "v3/expedientTipusEnumeracioForm";
	    }
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			Model model) {
		EnumeracioDto dto = enumeracioService.findAmbId(expedientTipusId, id);
		ExpedientTipusEnumeracioCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusEnumeracioCommand.class);
		model.addAttribute("expedientTipusEnumeracioCommand", command);
		model.addAttribute("heretat", dto.isHeretat());
		return "v3/expedientTipusEnumeracioForm";
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			@Validated(ExpedientTipusEnumeracioCommand.Modificacio.class) ExpedientTipusEnumeracioCommand command,
			BindingResult bindingResult, Model model) {
		try {
			if (bindingResult.hasErrors()) {
	    		model.addAttribute("heretat", enumeracioService.findAmbId(expedientTipusId, id).isHeretat());
				return "v3/expedientTipusEnumeracioForm";
			} else {
				EnumeracioDto dto = ExpedientTipusEnumeracioCommand.asEnumeracioDto(command);
				enumeracioService.update(dto);
				
	    		MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.enumeracio.controller.modificat"));
				return modalUrlTancar(false);
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar l'enumerat: " + id, ex);
    		MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.enumeracio.controller.modificat.error",
							new Object[] {ex.getLocalizedMessage()}));
			return "v3/expedientTipusEnumeracioForm";
	    }
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(HttpServletRequest request, @PathVariable Long expedientTipusId, @PathVariable Long id,
			Model model) {

		try {
			enumeracioService.delete(id);
			MissatgesHelper.success(request, getMessage(request, "expedient.tipus.enumeracio.controller.eliminat"));
			return true;
		}catch (Exception ex) {
    		MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.tipus.enumeracio.controller.eliminat.error",
							new Object[] {ex.getLocalizedMessage()}));
			return false;
		}
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/exportar", method = RequestMethod.GET)
	@ResponseBody
	public void exportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId) throws Exception {

    	try {
			EnumeracioDto enumeracio = enumeracioService.findAmbId(expedientTipusId, enumeracioId);
    		List<ExpedientTipusEnumeracioValorDto> enumeracioValors = enumeracioService.valorsFind(enumeracioId);
    		
    		String estatsString = "";
    		for(ExpedientTipusEnumeracioValorDto enumeracioValor: enumeracioValors){
    			estatsString +=
    					enumeracioValor.getCodi()+";"+enumeracioValor.getNom()+";"+enumeracioValor.getOrdre()+"\n";
    		}
    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.enumeracio.valors.exportats"));        			
    		
    		response.setHeader("Pragma", "");
    		response.setHeader("Expires", "");
    		response.setHeader("Cache-Control", "");
    		response.setHeader("Content-Disposition", "attachment; filename=\""
    				+ enumeracio.getCodi() + ".csv" + "\"");
    		response.setContentType("text/plain");
    		response.getOutputStream().write(estatsString.getBytes());        
    	} catch(Exception e) {
    		logger.error("Error exportant valors per l'enumeració amb id " + enumeracioId, e);
    		MissatgesHelper.error(
    				request,
    				getMessage(
    						request, 
    						"expedient.tipus.enumeracio.valors.exportats.error",
    						new Object[]{e.getLocalizedMessage()}));
    		throw(e);
    	}        
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusEnumeracioController.class);
}
