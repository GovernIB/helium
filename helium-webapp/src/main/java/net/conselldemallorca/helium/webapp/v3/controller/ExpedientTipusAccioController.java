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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
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
 * Controlador per a la pipella d'accions del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusAccioController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@RequestMapping(value = "/{expedientTipusId}/accions")
	public String accions(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"accions");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			model.addAttribute("baseUrl", expedientTipus.getId());
		}
		return "v3/expedientTipusAccio";
	}
	
	@RequestMapping(value="/{expedientTipusId}/accio/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				accioService.findPerDatatable(
						expedientTipusId,
						null,
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
			
	@RequestMapping(value = "/{expedientTipusId}/accio/new", method = RequestMethod.GET)
	public String nova(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusAccioCommand command = new ExpedientTipusAccioCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusAccioCommand", command);
		this.omplirModelFormulariAccio(
				request,
				entornActual.getId(),
				expedientTipusId,
				null,
				null,
				null,
				model);
		return "v3/expedientTipusAccioForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/accio/new", method = RequestMethod.POST)
	public String novaPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusAccioCommand.Creacio.class) ExpedientTipusAccioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		this.omplirModelFormulariAccio(
    				request,
    				entornActual.getId(),
    				expedientTipusId,
    				command.getDefprocJbpmKey(),
    				command.getJbpmAction(),
    				command.getPredefinitDadesJson(),
    				model);
        	return "v3/expedientTipusAccioForm";
        } else {
        	// Verificar permisos
        	accioService.create(
    				expedientTipusId,
    				null,
        			ExpedientTipusAccioCommand.asAccioDto(command));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.accio.controller.creat"));
			return modalUrlTancar(false);			
        }
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{expedientTipusId}/accio/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		AccioDto dto = accioService.findAmbId(expedientTipusId, id);
		ExpedientTipusAccioCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusAccioCommand.class);
		if (dto.getPredefinitDades() != null) {
			try {
				command.setPredefinitDades(
						(Map<String, String>) new ObjectMapper()
							.readValue(
									dto.getPredefinitDades(), 
									new TypeReference<Map<String, String>>(){}));
			} catch (Exception e) {
				MissatgesHelper.error(
						request, 
						"Error recuperant les dades JSON del handler predefinit: " + e.getMessage());
			}
		}
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusAccioCommand", command);
		this.omplirModelFormulariAccio(
				request,
				entornActual.getId(),
				expedientTipusId,
				dto.getDefprocJbpmKey(),
				dto.getJbpmAction(),
				dto.getPredefinitDades(),
				model);
		model.addAttribute("heretat", dto.isHeretat());

		return "v3/expedientTipusAccioForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/accio/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusAccioCommand.Modificacio.class) ExpedientTipusAccioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
    		this.omplirModelFormulariAccio(
    				request,
    				entornActual.getId(),
    				expedientTipusId,
    				command.getDefprocJbpmKey(),
    				command.getJbpmAction(),
    				command.getPredefinitDadesJson(),
    				model);
    		model.addAttribute("heretat", accioService.findAmbId(
    				expedientTipusId, 
    				id).isHeretat());
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
	
	private void omplirModelFormulariAccio(
			HttpServletRequest request, 
			Long entornId, 
			Long expedientTipusId, 
			String definicioProcesCodi, 
			String jbpmAction,
			String predefinitDades, 
			Model model) {

		// Tipus
		List<ParellaCodiValorDto> tipusOpcions = new ArrayList<ParellaCodiValorDto>();
		for (AccioTipusEnumDto tipus : AccioTipusEnumDto.values()) {
			tipusOpcions.add(new ParellaCodiValorDto(tipus.toString(), getMessage(request, "accio.tipus.enum." + tipus)));
		}
		model.addAttribute("tipus", tipusOpcions);

		model.addAttribute("definicionsProces", 
				expedientTipusService.definicioProcesFindJbjmKey(
						entornId, 
						expedientTipusId, 
						false,
						true));
		model.addAttribute("accions", 
				this.getAccions(expedientTipusId, definicioProcesCodi, jbpmAction));
		
		// Grups de handlers predefinits
		List<ParellaCodiValorDto> handlersPredefinititsGrups = new ArrayList<ParellaCodiValorDto>();
		for (HandlerAgrupacioEnum agrupacio : HandlerAgrupacioEnum.values()) {
			handlersPredefinititsGrups.add(new ParellaCodiValorDto(agrupacio.toString(), getMessage(request, "handler.agrupacio.enum." + agrupacio)));
		}
		model.addAttribute("handlersPredefinititsGrups", 
				handlersPredefinititsGrups);

		model.addAttribute("handlersPredefinitsJson", 
				this.getHandlersPredefinitsJson());

		model.addAttribute("dadesPredefinidesJson", 
				predefinitDades);

		// Variables del tipus d'expedient
		model.addAttribute("variables", 
				dissenyService.findCampsOrdenatsPerCodi(
						expedientTipusId,
						null,
						true // amb herencia
						));
	}

	
	@RequestMapping(value = "/{expedientTipusId}/accio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
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
			logger.error("S'ha produit un error al intentar eliminar la accio amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			return false;
		}
	}
	
	/** Mètode per obtenir les possibles versions per al select de definicions de procés via ajax. */
	@RequestMapping(value = "/{expedientTipusId}/definicio/{definicioCodi}/accions/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> definicioAccioSelect(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable String definicioCodi,
			@RequestParam(value="jbpmActionActual", required=false) String jbpmActionActual,
			Model model) {

		// Select d'accions
		return this.getAccions(expedientTipusId, definicioCodi, jbpmActionActual);
	}
		
	/** Consulta la llista d'accions per la darrera versió de la definició de procés per codi del
	 * tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @param definicioCodi
	 * @param jbpmActionActual 
	 * @return
	 */
	private List<ParellaCodiValorDto> getAccions(Long expedientTipusId, String definicioCodi, String jbpmAction) {

		List<ParellaCodiValorDto> ret = new ArrayList<ParellaCodiValorDto>();
		List<String> accions = new ArrayList<String>();
		if(expedientTipusId != null && definicioCodi != null) {
			// Darrera versió de la definició de procés
			DefinicioProcesDto definicioProces = dissenyService.findDarreraVersioForExpedientTipusIDefProcCodi(expedientTipusId, definicioCodi);
			accions = dissenyService.findAccionsJbpmOrdenades(definicioProces.getId());
		}
		for (String accio : accions) {
			ret.add(new ParellaCodiValorDto(accio, accio));
		}
		if (jbpmAction != null 
				&& !jbpmAction.isEmpty()
				&&	!accions.contains(jbpmAction)) {
			ret.add(0, new ParellaCodiValorDto(
					jbpmAction,
					jbpmAction + " (no existeix en la darrera versió de la definició de procés '" + definicioCodi + "')"));
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


	private static final Log logger = LogFactory.getLog(ExpedientTipusAccioController.class);
}
