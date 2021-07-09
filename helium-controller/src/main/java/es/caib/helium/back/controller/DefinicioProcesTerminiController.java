/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusTerminiCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.dto.TerminiDto;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per a la pipella de terminis de la definició de procés.
 * 
 */
@Controller(value = "definicioProcesTerminiControllerV3")
@RequestMapping("/v3/definicioProces")
public class DefinicioProcesTerminiController extends BaseDefinicioProcesController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@ModelAttribute("listTerminis")
	public List<ParellaCodiValorDto> populateValorTerminis() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i = 0; i <= 12; i++) {
			resposta.add(new ParellaCodiValorDto(Integer.toString(i), i));
		}
		return resposta;
	}
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/terminis")
	public String documents(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioDefinicioProcesPerPipelles(
					request,
					jbmpKey,
					definicioProcesId,
					model,
					"terminis");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdPermisDissenyar(entornActual.getId(),
					definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			model.addAttribute("baseUrl", ("/helium/v3/definicioProces/" + definicioProces.getJbpmKey() + "/" + definicioProces.getId().toString()));
		}
		return "v3/expedientTipusTermini";
	}

	@RequestMapping(value="/{jbmpKey}/{definicioProcesId}/termini/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				terminiService.findPerDatatable(
						null,
						definicioProcesId,
						paginacioParams.getFiltre(),
						paginacioParams));		
	}
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/termini/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		ExpedientTipusTerminiCommand command = new ExpedientTipusTerminiCommand();
		command.setDefinicioProcesId(definicioProcesId);
		model.addAttribute("expedientTipusTerminiCommand", command);
		return "v3/expedientTipusTerminiForm";
	}
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/termini/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@Validated(ExpedientTipusTerminiCommand.Creacio.class) ExpedientTipusTerminiCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusTerminiForm";
        } else {
        	// Verificar permisos
    		terminiService.create(
    				null,
    				definicioProcesId,
    				conversioTipusHelper.convertir(
    						command,
    						TerminiDto.class));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.termini.controller.creat"));
			return modalUrlTancar(false);	
			
        }
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/termini/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@PathVariable Long id,
			Model model) {
		TerminiDto dto = terminiService.findAmbId(null, id);
		ExpedientTipusTerminiCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusTerminiCommand.class);
		command.setDefinicioProcesId(definicioProcesId);
		model.addAttribute("expedientTipusTerminiCommand", command);
		return "v3/expedientTipusTerminiForm";
	}
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/termini/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@PathVariable Long id,
			@Validated(ExpedientTipusTerminiCommand.Modificacio.class) ExpedientTipusTerminiCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusTerminiForm";
        } else {
        	terminiService.update(
        			conversioTipusHelper.convertir(
    						command,
    						TerminiDto.class));
        	MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.termini.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/termini/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean borrar(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@PathVariable Long id,
			Model model) {
		try {
			terminiService.delete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.termini.controller.eliminat"));
			return true;
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(request, "expedient.tipus.termini.controller.eliminat"));
			return false;
		}
	}
}
