/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.handlers.HandlerAgrupacioEnum;
import net.conselldemallorca.helium.v3.core.api.dto.handlers.HandlerDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAccioCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAccioDesplegarCommand;
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
			model.addAttribute("perEstats", ExpedientTipusTipusEnumDto.ESTAT.equals(expedientTipus.getTipus()));
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
		ExpedientTipusAccioCommand command = new ExpedientTipusAccioCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusAccioCommand", command);
		this.omplirModelFormulariAccio(
				request,
				expedientTipusId,
				command,
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
    		this.omplirModelFormulariAccio(
    				request,
    				expedientTipusId,
					command,
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
		AccioDto dto = accioService.findAmbId(expedientTipusId, id);
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
		
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusAccioCommand", command);
		this.omplirModelFormulariAccio(
				request,
				expedientTipusId,
				command, 
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
    		this.omplirModelFormulariAccio(
    				request,
    				expedientTipusId,
					command, model);
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
			Long expedientTipusId,
			ExpedientTipusAccioCommand command,
			Model model) {

		// Per estats
		boolean perEstats = false;
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = null;
		String definicioProcesCodi = command.getDefprocJbpmKey();
		if (entornActual != null) {
			expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			perEstats = ExpedientTipusTipusEnumDto.ESTAT.equals(expedientTipus.getTipus());
			model.addAttribute("perEstats", perEstats);
			if (perEstats) {
				definicioProcesCodi = expedientTipus.getJbpmProcessDefinitionKey();
			}
		}
		command.setPerEstats(perEstats);

		// Tipus
		List<ParellaCodiValorDto> tipusOpcions = new ArrayList<ParellaCodiValorDto>();
		if (!perEstats) {
			tipusOpcions.add(new ParellaCodiValorDto(AccioTipusEnumDto.ACCIO.toString(), getMessage(request, "accio.tipus.enum." + AccioTipusEnumDto.ACCIO.toString())));
		}
		tipusOpcions.add(new ParellaCodiValorDto(AccioTipusEnumDto.HANDLER_PROPI.toString(), getMessage(request, "accio.tipus.enum." + AccioTipusEnumDto.HANDLER_PROPI.toString())));
		tipusOpcions.add(new ParellaCodiValorDto(AccioTipusEnumDto.HANDLER_PREDEFINIT.toString(), getMessage(request, "accio.tipus.enum." + AccioTipusEnumDto.HANDLER_PREDEFINIT.toString())));
		tipusOpcions.add(new ParellaCodiValorDto(AccioTipusEnumDto.SCRIPT.toString(), getMessage(request, "accio.tipus.enum." + AccioTipusEnumDto.SCRIPT.toString())));
		model.addAttribute("tipus", tipusOpcions);

		// Accions
		if (!perEstats) {
			model.addAttribute("definicionsProces",
					expedientTipusService.definicioProcesFindJbjmKey(
							entornActual.getId(),
							expedientTipusId,
							false,
							true));
			model.addAttribute("accions",
					this.getAccions(expedientTipusId, command.getDefprocJbpmKey(), command.getJbpmAction()));
		}
		// Handlers propis
		model.addAttribute("handlersPropis", 
				this.getHandlersPropis(expedientTipusId, definicioProcesCodi, command.getHandlerPropi()));
		
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
							"expedient.tipus.accio.llistat.accio.esborrar.error"),
					e);
			logger.error("S'ha produit un error al intentar eliminar la accio amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			return false;
		}
	}

	@RequestMapping(value = "/{expedientTipusId}/accio/desplegar", method = RequestMethod.GET)
	public String desplegar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
				entornActual.getId(),
				expedientTipusId);
		ExpedientTipusAccioDesplegarCommand command = new ExpedientTipusAccioDesplegarCommand();
		command.setExpedientTipusId(expedientTipusId);
		command.setEntornId(entornActual.getId());
		model.addAttribute("expedientTipus", expedientTipus);
		model.addAttribute("command", command);
		return "v3/expedientTipusAccioDesplegarForm";
	}

	@RequestMapping(value = "/{expedientTipusId}/accio/desplegar", method = RequestMethod.POST)
	public String desplegarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@ModelAttribute("command") @Valid ExpedientTipusAccioDesplegarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		boolean error = false;
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (bindingResult.hasErrors()) {
			error = true;
		} else {
			try {
				ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
						entornActual.getId(),
						expedientTipusId);
				if (!ExpedientTipusTipusEnumDto.ESTAT.equals(expedientTipus.getTipus())) {
					MissatgesHelper.error(request, getMessage(request, "expedient.tipus.accio.desplegar.flow"));
					error = true;
				}
				List<String> handlers = dissenyService.updateHandlersAccions(
						expedientTipusId,
						command.getFile().getOriginalFilename(),
						command.getFile().getBytes());
				MissatgesHelper.success(request, getMessage(request, "expedient.tipus.accio.desplegar.form.success",
										new Object[] {handlers.size(), handlers}));
			} catch (Exception e) {
				logger.error("Error : (" + e.getClass() + ") " + e.getLocalizedMessage());
				MissatgesHelper.error(
						request,
						getMessage(request, "definicio.proces.actualitzar.excepcio", new Object[]{e.getMessage()}),
						e);
				error = true;
			}
		}
		if (error) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			return "v3/expedientTipusAccioDesplegarForm";
		} else {
			return modalUrlTancar(false);
		}
	}

	@RequestMapping(value = "/{expedientTipusId}/accio/params", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> getParametresHandler(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(value="handler", required=true) String handler,
			Model model) {
		List<ParellaCodiValorDto> parametres = new ArrayList<ParellaCodiValorDto>();
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
				entornActual.getId(),
				expedientTipusId);
		DefinicioProcesDto definicioProces = dissenyService.findDarreraVersioForExpedientTipusIDefProcCodi(expedientTipusId, expedientTipus.getJbpmProcessDefinitionKey());
		parametres = dissenyService.findHandlerParams(definicioProces.getId(), handler);
		// Select d'accions
		return parametres;
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

	/** Mètode per obtenir els possibles handlers propois per al select de definicions de procés via ajax. */
	@RequestMapping(value = "/{expedientTipusId}/definicio/{definicioCodi}/handlersPropis/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> definicioHandlerPropiSelect(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable String definicioCodi,
			@RequestParam(value="handlerPropiActual", required=false) String handlersPropiActual,
			Model model) {

		// Select d'accions
		return this.getHandlersPropis(expedientTipusId, definicioCodi, handlersPropiActual);
	}

	/** Consulta la llista d'accions per la darrera versió de la definició de procés per codi del
	 * tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @param definicioCodi
	 * @param jbpmAction
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
	
	/** Consulta la llista d'accions per la darrera versió de la definició de procés per codi del
	 * tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @param definicioCodi
	 * @param handlerPropi
	 * @return
	 */
	private List<ParellaCodiValorDto> getHandlersPropis(Long expedientTipusId, String definicioCodi, String handlerPropi) {

		List<ParellaCodiValorDto> ret = new ArrayList<ParellaCodiValorDto>();
		List<String> accions = new ArrayList<String>();
		if(expedientTipusId != null && definicioCodi != null) {
			// Darrera versió de la definició de procés
			DefinicioProcesDto definicioProces = dissenyService.findDarreraVersioForExpedientTipusIDefProcCodi(expedientTipusId, definicioCodi);
			accions = dissenyService.findHandlersJbpmOrdenats(definicioProces.getId());
		}
		for (String accio : accions) {
			ret.add(new ParellaCodiValorDto(accio, accio));
		}
		if (handlerPropi != null 
				&& !handlerPropi.isEmpty()
				&&	!accions.contains(handlerPropi)) {
			ret.add(0, new ParellaCodiValorDto(
					handlerPropi,
					handlerPropi + " (no existeix en la darrera versió de la definició de procés '" + definicioCodi + "')"));
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
