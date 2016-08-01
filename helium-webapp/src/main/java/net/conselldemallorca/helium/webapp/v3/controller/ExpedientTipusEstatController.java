/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEstatCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusEstatController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@ModelAttribute("listEstats")
	public List<ParellaCodiValorDto> populateValorEstats() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i = 0; i <= 12; i++) {
			resposta.add(new ParellaCodiValorDto(Integer.toString(i), i));
		}
		return resposta;
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estats")
	public String documents(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"estats");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		return "v3/expedientTipusEstat";
	}

	@RequestMapping(value="/{expedientTipusId}/estats/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.estatFindPerDatatable(
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams));		
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		ExpedientTipusEstatCommand command = new ExpedientTipusEstatCommand();
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("expedientTipusEstatCommand", command);
		return "v3/expedientTipusEstatForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusEstatCommand.Creacio.class) ExpedientTipusEstatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
        	return "v3/expedientTipusEstatForm";
        } else {
        	// Verificar permisos
    		expedientTipusService.estatCreate(
    				expedientTipusId,
    				conversioTipusHelper.convertir(
    						command,
    						EstatDto.class));    		
//			return getModalControllerReturnValueSuccess(
//					request,
//					"redirect:/v3/expedientTipus/" + expedientTipusId + "#estats",
//					"expedient.tipus.estat.controller.creat");
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.controller.creat"));
			return modalUrlTancar(false);	
			
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/estat/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		EstatDto dto = expedientTipusService.estatFindAmbId(id);
		ExpedientTipusEstatCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusEstatCommand.class);
		model.addAttribute("expedientTipusEstatCommand", command);
		model.addAttribute("expedientTipusId", expedientTipusId);
		return "v3/expedientTipusEstatForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/estat/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusEstatCommand.Modificacio.class) ExpedientTipusEstatCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
        	return "v3/expedientTipusEstatForm";
        } else {
        	expedientTipusService.estatUpdate(
        			conversioTipusHelper.convertir(
    						command,
    						EstatDto.class));
        	MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.estat.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean borrar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		try {
			expedientTipusService.estatDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.estat.controller.eliminat"));
			return true;
//			return getModalControllerReturnValueSuccess(
//					request,
//					"redirect:/v3/expedientTipus/" + expedientTipusId + "#estats",
//					"expedient.tipus.estat.controller.eliminat");
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(request, "expedient.tipus.estat.controller.eliminat"));
			return false;
//			redirectAttributes.addAttribute("pipellaActiva", "estats");
//			return getModalControllerReturnValueError(
//					request,
//					"redirect:/v3/expedientTipus/" + expedientTipusId,
//					null);
		}
//		return "v3/expedientTipusEstatForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/estat/{estatId}/moure/{posicio}", method = RequestMethod.GET)
	@ResponseBody
	public boolean moure(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long estatId,
			@PathVariable int posicio,
			Model model) {
		return expedientTipusService.estatMoure(estatId, posicio);
	}
}
