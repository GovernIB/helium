/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.handlers.HandlerAgrupacioEnum;
import net.conselldemallorca.helium.v3.core.api.dto.handlers.HandlerDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAccioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pipella d'accions de la definició de procés.
 * 
 */
@Controller(value = "definicioProcesAccioControllerV3")
@RequestMapping("/v3/definicioProces")
public class DefinicioProcesAccioController extends BaseDefinicioProcesController {

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
			model.addAttribute("baseUrl", ("/helium/v3/definicioProces/" + definicioProces.getJbpmKey() + "/" + definicioProces.getId().toString()));
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
		this.omplirModelFormulariAccio(
				request,
				definicioProcesId,
				command, model);
		
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
    		this.omplirModelFormulariAccio(
    				request,
    				definicioProcesId,
    				command, model);
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
		ExpedientTipusAccioCommand command = ConversioTipusHelper.convertir(
				dto,
				ExpedientTipusAccioCommand.class);
		// Depenent del tipus posa la classe en un atribut o altre del command.
		switch(dto.getTipus()) {
			case HANDLER_PROPI:
				command.setHandlerPropi(dto.getHandlerClasse());
				break;
			case HANDLER_PREDEFINIT:
				command.setHandlerPredefinit(dto.getHandlerClasse());
				break;
			default:
				break;		
		}

		if (dto.getHandlerDades() != null) {
			try {
				command.setHandlerDades(
						(Map<String, String>) new ObjectMapper()
							.readValue(
									dto.getHandlerDades(), 
									new TypeReference<Map<String, String>>(){}));
			} catch (Exception e) {
				MissatgesHelper.error(
						request, 
						"Error recuperant les dades JSON del handler: " + e.getMessage(),
						e);
			}
		}
		
		model.addAttribute("expedientTipusAccioCommand", command);

		this.omplirModelFormulariAccio(
				request,
				definicioProcesId,
				command, 
				model);
		
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
    		this.omplirModelFormulariAccio(
    				request,
    				definicioProcesId,
    				command, model);
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
							"expedient.tipus.accio.llistat.accio.esborrar.error"),
					e);
			logger.error("S'ha produit un error al intentar eliminar la accio amb id '" + id + "' de la definció de procés amb id '" + definicioProcesId, e);
			return false;
		}
	}
	
	private void omplirModelFormulariAccio(
			HttpServletRequest request,
			Long definicioProcesId,
			ExpedientTipusAccioCommand command,
			Model model) {

		// Per estats
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdPermisDissenyar(
				entornActual.getId(), 
				definicioProcesId);
		model.addAttribute("perEstats", false);
		command.setPerEstats(false);
		command.setExpedientTipusId(definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : 0L);

		// Tipus
		List<ParellaCodiValorDto> tipusOpcions = new ArrayList<ParellaCodiValorDto>();
		tipusOpcions.add(new ParellaCodiValorDto(AccioTipusEnumDto.ACCIO.toString(), getMessage(request, "accio.tipus.enum." + AccioTipusEnumDto.ACCIO.toString())));
		tipusOpcions.add(new ParellaCodiValorDto(AccioTipusEnumDto.HANDLER_PROPI.toString(), getMessage(request, "accio.tipus.enum." + AccioTipusEnumDto.HANDLER_PROPI.toString())));
		tipusOpcions.add(new ParellaCodiValorDto(AccioTipusEnumDto.HANDLER_PREDEFINIT.toString(), getMessage(request, "accio.tipus.enum." + AccioTipusEnumDto.HANDLER_PREDEFINIT.toString())));
		tipusOpcions.add(new ParellaCodiValorDto(AccioTipusEnumDto.SCRIPT.toString(), getMessage(request, "accio.tipus.enum." + AccioTipusEnumDto.SCRIPT.toString())));
		model.addAttribute("tipus", tipusOpcions);

		// Accions
		model.addAttribute("definicionsProces", 
				Arrays.asList(new String[] {definicioProces.getJbpmKey()}));
		model.addAttribute("accions",
				this.getAccions(definicioProcesId, command.getJbpmAction()));
		// Handlers propis
		model.addAttribute("handlersPropis", 
				this.getHandlersPropis(definicioProcesId, command.getHandlerPropi()));
		
		// Grups de handlers predefinits
		List<ParellaCodiValorDto> handlersPredefinititsGrups = new ArrayList<ParellaCodiValorDto>();
		for (HandlerAgrupacioEnum agrupacio : HandlerAgrupacioEnum.values()) {
			handlersPredefinititsGrups.add(new ParellaCodiValorDto(agrupacio.toString(), getMessage(request, "handler.agrupacio.enum." + agrupacio)));
		}
		model.addAttribute("handlersPredefinititsGrups", 
				handlersPredefinititsGrups);

		model.addAttribute("handlersPredefinitsJson", 
				this.getHandlersPredefinitsJson());

		model.addAttribute("dadesHandlerJson", 
				command.getHandlerDadesJson());

		// Variables del tipus d'expedient
		model.addAttribute("variables", 
				dissenyService.findCampsOrdenatsPerCodi(
							definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
							definicioProces.getId(),
							true // amb herencia
						));
	}
	
	
	/** Consulta la llista d'accions per la definició de procés.
	 * 
	 * @param expedientTipusId
	 * @param definicioCodi
	 * @param jbpmAction
	 * @return
	 */
	private List<ParellaCodiValorDto> getAccions(Long definicioProcesId, String jbpmAction) {

		List<ParellaCodiValorDto> ret = new ArrayList<ParellaCodiValorDto>();
		List<String> accions = dissenyService.findAccionsJbpmOrdenades(definicioProcesId);
		for (String accio : accions) {
			ret.add(new ParellaCodiValorDto(accio, accio));
		}
		if (jbpmAction != null 
				&& !jbpmAction.isEmpty()
				&&	!accions.contains(jbpmAction)) {
			ret.add(0, new ParellaCodiValorDto(
					jbpmAction,
					jbpmAction + " (no existeix en la darrera versió de la definició de procés '" + definicioProcesId + "')"));
		}
		return ret;
	}
	
	/** Consulta la llista de handlers propis de la definició de procés.
	 * 
	 * @param expedientTipusId
	 * @param definicioCodi
	 * @param handlerPropi
	 * @return
	 */
	private List<ParellaCodiValorDto> getHandlersPropis(Long definicioProcesId, String handlerPropi) {

		List<ParellaCodiValorDto> ret = new ArrayList<ParellaCodiValorDto>();
		List<String> accions = dissenyService.findHandlersJbpmOrdenats(definicioProcesId);
		for (String accio : accions) {
			ret.add(new ParellaCodiValorDto(accio, accio));
		}
		if (handlerPropi != null 
				&& !handlerPropi.isEmpty()
				&&	!accions.contains(handlerPropi)) {
			ret.add(0, new ParellaCodiValorDto(
					handlerPropi,
					handlerPropi + " (no existeix en la darrera versió de la definició de procés '" + definicioProcesId + "')"));
		}
		return ret;
	}
	
	/** Consulta els handlers predefinits 
	 * 
	 * @return
	 */
	private String getHandlersPredefinitsJson() {
		String handlersJson = "[]";		
		try {
			List<HandlerDto> handlers = dissenyService.getHandlersPredefinits();
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			handlersJson = ow.writeValueAsString(handlers);		
		} catch(Exception e) {
			logger.error("Error llegint els handlers predefinits: " + e.getMessage(), e);
		}
		return handlersJson;
	}

	private static final Log logger = LogFactory.getLog(DefinicioProcesAccioController.class);
}
