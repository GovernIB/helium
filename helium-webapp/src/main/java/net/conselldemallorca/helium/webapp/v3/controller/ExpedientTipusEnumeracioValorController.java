package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

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

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEnumeracioValorCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "expedientTipusEnumeracioValorControllerV3")
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusEnumeracioValorController extends BaseExpedientTipusController {
	
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;	
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valors", method = RequestMethod.GET)
	public String valors(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			Model model) {
		
//		if (!NodecoHelper.isNodeco(request)) {
//			return mostrarInformacioExpedientTipusPerPipelles(request, expedientTipusId, model, "enumeracions");
//		}
		ompleDadesModel(request, expedientTipusId, enumeracioId, model, true);

		return "v3/expedientTipusEnumeracioValors";
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, expedientTipusService.enumeracioValorsFindPerDatatable(expedientTipusId, enumeracioId, paginacioParams.getFiltre(), paginacioParams));
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/moure/{posicio}", method = RequestMethod.GET)
	@ResponseBody
	public boolean mourer(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			@PathVariable int posicio,
			Model model) {
		return expedientTipusService.enumeracioValorMourer(id, posicio);
	}

	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/update", method = RequestMethod.GET)
	public String modifica(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			Model model) {
		ExpedientTipusEnumeracioValorDto dto = expedientTipusService.enumeracioValorFindAmbId(id);
		ExpedientTipusEnumeracioValorCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusEnumeracioValorCommand.class);
		ompleDadesModel(request, expedientTipusId, enumeracioId, model, false);		
		model.addAttribute("expedientTipusEnumeracioValorCommand", command);
		model.addAttribute("mostraUpdate", true);
		return "v3/expedientTipusEnumeracioValors";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/update", method = RequestMethod.POST)
	public String modificaPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			@Validated(ExpedientTipusEnumeracioValorCommand.Modificacio.class) ExpedientTipusEnumeracioValorCommand command,
			BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			ompleDadesModel(request, expedientTipusId, enumeracioId, model, false);		
			model.addAttribute("expedientTipusEnumeracioValorCommand", command);
			model.addAttribute("mostraUpdate", true);
			return "v3/expedientTipusEnumeracioValors";
		} else {		
		
			ExpedientTipusEnumeracioValorDto dto = ExpedientTipusEnumeracioValorCommand.asExpedientTipusEnumeracioValorDto(command);
			
			//Conservam l´ordre anteriro
			ExpedientTipusEnumeracioValorDto dto_antic = expedientTipusService.enumeracioValorFindAmbId(id);
			dto.setOrdre(dto_antic.getOrdre());
			
			expedientTipusService.enumeracioValorUpdate(dto);
			
			ompleDadesModel(request, expedientTipusId, enumeracioId, model, true);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.modificat"));				
			
			return "v3/expedientTipusEnumeracioValors";
		}
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@PathVariable Long id,
			Model model) {
		try {
			expedientTipusService.enumeracioValorDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.eliminat"));			
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.eliminat.us"));
			logger.error("S'ha produit un error al intentar eliminar el valor del enumerat amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
		}
		
		ompleDadesModel(request, expedientTipusId, enumeracioId, model, true);
		return "v3/expedientTipusEnumeracioValors";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/enumeracio/{enumeracioId}/valor/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long enumeracioId,
			@Validated(ExpedientTipusEnumeracioValorCommand.Creacio.class) ExpedientTipusEnumeracioValorCommand command,
			BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("mostraCreate", true);
			ompleDadesModel(request, expedientTipusId, enumeracioId, model, false);
			model.addAttribute("expedientTipusEnumeracioValorCommand", command);
        	return "v3/expedientTipusEnumeracioValors";
		} else {
		
			ExpedientTipusEnumeracioValorDto dto = ExpedientTipusEnumeracioValorCommand.asExpedientTipusEnumeracioValorDto(command);
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			
			expedientTipusService.enumeracioValorsCreate(expedientTipusId, enumeracioId, entornActual.getId(), dto);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.enumeracio.valors.controller.creat"));
        	return valors(request, expedientTipusId, enumeracioId, model);
		}
	}
	
	private void ompleDadesModel(
			HttpServletRequest request,
			Long expedientTipusId,
			Long enumeracioId,
			Model model,
			boolean ficaCommand) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		if (entornActual != null) {
			
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(entornActual.getId(),	expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			
			ExpedientTipusEnumeracioDto enumeracio = expedientTipusService.enumeracioFindAmbId(enumeracioId);
			model.addAttribute("enumeracio", enumeracio);
			
			if (ficaCommand) {
				ExpedientTipusEnumeracioValorCommand command = new ExpedientTipusEnumeracioValorCommand();
				command.setExpedientTipusId(expedientTipusId);
				command.setEnumeracioId(enumeracioId);
				model.addAttribute("expedientTipusEnumeracioValorCommand", command);
			}
		}
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusDocumentController.class);
}