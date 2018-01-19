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

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusTerminiCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

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
					model,
					"terminis");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdAndEntorn(entornActual.getId(),
					definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			model.addAttribute("baseUrl", (definicioProces.getJbpmKey() + "/" + definicioProces.getId().toString()));
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
