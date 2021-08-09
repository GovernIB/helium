/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusAccioCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.AccioDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
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

/**
 * Controlador per a la pipella d'accions de la definició de procés.
 * 
 */
@Controller
@RequestMapping("/v3/definicioProces")
public class DefinicioProcesAccioController extends BaseDefinicioProcesController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/accions")
	public String accions(
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
					"accions");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdPermisDissenyar(entornActual.getId(),
					definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			model.addAttribute("baseUrl", ("/heliumback/v3/definicioProces/" + definicioProces.getJbpmKey() + "/" + definicioProces.getId().toString()));
		}
		return "v3/expedientTipusAccio";
	}
	
	@RequestMapping(value="/{jbmpKey}/{definicioProcesId}/accio/datatable", method = RequestMethod.GET)
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
				accioService.findPerDatatable(
						null,
						definicioProcesId,
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
			
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/accio/new", method = RequestMethod.GET)
	public String nova(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			Model model) {
		ExpedientTipusAccioCommand command = new ExpedientTipusAccioCommand();
		command.setDefinicioProcesId(definicioProcesId);
		command.setDefprocJbpmKey(jbmpKey);
		model.addAttribute("expedientTipusAccioCommand", command);
		model.addAttribute("handlers", dissenyService.findAccionsJbpmOrdenades(definicioProcesId));
		return "v3/expedientTipusAccioForm";
	}
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/accio/new", method = RequestMethod.POST)
	public String novaPost(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@Validated(ExpedientTipusAccioCommand.Creacio.class) ExpedientTipusAccioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		model.addAttribute("handlers", dissenyService.findAccionsJbpmOrdenades(definicioProcesId));
        	return "v3/expedientTipusAccioForm";
        } else {
        	// Verificar permisos
        	accioService.create(
    				null,
    				definicioProcesId,
        			ExpedientTipusAccioCommand.asAccioDto(command));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.accio.controller.creat"));
			return modalUrlTancar(false);			
        }
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/accio/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@PathVariable Long id,
			Model model) {
		AccioDto dto = accioService.findAmbId(null, id);
		ExpedientTipusAccioCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusAccioCommand.class);
		command.setDefinicioProcesId(definicioProcesId);
		command.setDefprocJbpmKey(jbmpKey);
		model.addAttribute("expedientTipusAccioCommand", command);
		model.addAttribute("handlers", dissenyService.findAccionsJbpmOrdenades(definicioProcesId));
		return "v3/expedientTipusAccioForm";
	}
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/accio/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@PathVariable Long id,
			@Validated(ExpedientTipusAccioCommand.Modificacio.class) ExpedientTipusAccioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		model.addAttribute("handlers", dissenyService.findAccionsJbpmOrdenades(definicioProcesId));
        	return "v3/expedientTipusAccioForm";
        } else {
        	accioService.update(
        			ExpedientTipusAccioCommand.asAccioDto(command));
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.accio.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/accio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request,
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@PathVariable Long id,
			Model model) {
		
		try {
			accioService.delete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.accio.llistat.accio.esborrar.correcte"));
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.accio.llistat.accio.esborrar.error"));
			logger.error("S'ha produit un error al intentar eliminar la accio amb id '" + id + "' de la definció de procés amb id '" + definicioProcesId, e);
			return false;
		}
	}
		
	private static final Log logger = LogFactory.getLog(DefinicioProcesAccioController.class);
}
