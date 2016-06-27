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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAgrupacioCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCampCommand;
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
public class ExpedientTipusVariableController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@RequestMapping(value = "/{expedientTipusId}/variables")
	public String variables(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"variables");
		}
		omplirModelVariablesPestanya(
				request,
				expedientTipusId,
				model);
		return "v3/expedientTipusVariable";
	}


	
	@RequestMapping(value="/{expedientTipusId}/variable/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.campFindPerDatatable(
						expedientTipusId,
						agrupacioId,
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
			
	@RequestMapping(value = "/{expedientTipusId}/variable/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		ExpedientTipusCampCommand command = new ExpedientTipusCampCommand();
		command.setAgrupacioId(agrupacioId);
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusCampCommand", command);
		this.omplirModelVariableForm(
				request, 
				expedientTipusId, 
				model);
		return "v3/expedientTipusVariableForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/variable/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusCampCommand.Creacio.class) ExpedientTipusCampCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		this.omplirModelVariableForm(
    				request, 
    				expedientTipusId, 
    				model);
        	return "v3/expedientTipusVariableForm";
        } else {
        	// Verificar permisos
    		expedientTipusService.campCreate(
    				expedientTipusId,
        			ExpedientTipusCampCommand.asCampDto(command));    		
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus/" + expedientTipusId + "#variables",
					"expedient.tipus.camp.controller.creat");
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		CampDto dto = expedientTipusService.campFindAmbId(id);
		ExpedientTipusCampCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusCampCommand.class);
		command.setAgrupacioId(dto.getAgrupacio() != null? dto.getAgrupacio().getId() : null);
		command.setEnumeracioId(dto.getEnumeracio() != null? dto.getEnumeracio().getId() : null);
		command.setDominiId(dto.getDomini() != null? dto.getDomini().getId() : null);
		command.setConsultaId(dto.getConsulta() != null? dto.getConsulta().getId() : null);
		
		model.addAttribute("expedientTipusCampCommand", command);
		this.omplirModelVariableForm(
				request, 
				expedientTipusId, 
				model);
		return "v3/expedientTipusVariableForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusCampCommand.Modificacio.class) ExpedientTipusCampCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		this.omplirModelVariableForm(
    				request, 
    				expedientTipusId, 
    				model);
        	return "v3/expedientTipusVariableForm";
        } else {
        	expedientTipusService.campUpdate(
        			ExpedientTipusCampCommand.asCampDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus/" + expedientTipusId + "#variables",
					"expedient.tipus.camp.controller.modificat");
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/agrupar/{agrupacioId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean agrupar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@PathVariable Long agrupacioId,
			Model model) {
		
		return expedientTipusService.campAfegirAgrupacio(id, agrupacioId);
	}
	
	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/desagrupar", method = RequestMethod.GET)
	@ResponseBody
	public boolean desagrupar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		return expedientTipusService.campRemoureAgrupacio(id);
	}

	@RequestMapping(value = "/{expedientTipusId}/variable/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		expedientTipusService.campDelete(id);
		
		MissatgesHelper.success(
				request,
				getMessage(
						request,
						"expedient.tipus.camp.controller.eliminat"));
		
		return "redirect:/v3/expedientTipus/" + expedientTipusId + "#variables";
	}
	
	
	// Mètodes pel manteniment d'agrupacions
	
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/new", method = RequestMethod.GET)
	public String agrupacioNova(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		ExpedientTipusAgrupacioCommand command = new ExpedientTipusAgrupacioCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusAgrupacioCommand", command);
		return "v3/expedientTipusAgrupacioForm";
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/new", method = RequestMethod.POST)
	public String agrupacioNovaPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusAgrupacioCommand.Creacio.class) ExpedientTipusAgrupacioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusAgrupacioForm";
        } else {
        	// Verificar permisos
    		expedientTipusService.agrupacioCreate(
    				expedientTipusId,
    				conversioTipusHelper.convertir(
    						command,
    						CampAgrupacioDto.class));    		
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus/" + expedientTipusId + "#variables",
					"expedient.tipus.campAgrupacio.controller.creat");
        }
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/{id}/update", method = RequestMethod.GET)
	public String agrupacioModificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		CampAgrupacioDto dto = expedientTipusService.agrupacioFindAmbId(id);
		ExpedientTipusAgrupacioCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusAgrupacioCommand.class);
		model.addAttribute("expedientTipusAgrupacioCommand", command);
		return "v3/expedientTipusAgrupacioForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/agrupacio/{id}/update", method = RequestMethod.POST)
	public String agrupacioModificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusAgrupacioCommand.Modificacio.class) ExpedientTipusAgrupacioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusAgrupacioForm";
        } else {
        	expedientTipusService.agrupacioUpdate(
        			expedientTipusId,
        			conversioTipusHelper.convertir(
    						command,
    						CampAgrupacioDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/expedientTipus/" + expedientTipusId + "#variables",
					"expedient.tipus.campAgrupacio.controller.modificat");
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/agrupacio/{id}/delete", method = RequestMethod.GET)
	public String agrupacioDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		expedientTipusService.agrupacioDelete(id);
		
		MissatgesHelper.success(
				request,
				getMessage(
						request,
						"expedient.tipus.campAgrupacio.controller.eliminat"));
		
		return "redirect:/v3/expedientTipus/" + expedientTipusId + "#variables";
	}
	
	private void omplirModelVariablesPestanya(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPerDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		this.omplirModelAgrupacions(
				request, 
				expedientTipusId, 
				model);
	}
	
	private void omplirModelVariableForm(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		
		// TipusCamp
		List<ParellaCodiValorDto> tipusCamp = new ArrayList<ParellaCodiValorDto>();
		for (CampTipusDto campTipus : CampTipusDto.values()) {
			tipusCamp.add(new ParellaCodiValorDto(campTipus.toString(), campTipus));
		}
		model.addAttribute("tipusCamp",tipusCamp);
		
		// Agrupacions
		this.omplirModelAgrupacions(
				request, 
				expedientTipusId, 
				model);
		
		// Enumeracions
		model.addAttribute("enumeracions", expedientTipusService.enumeracioFindAll(expedientTipusId));
		
		// Dominis
		model.addAttribute("dominis", expedientTipusService.dominiFindAll(expedientTipusId));
		
		// Consultes
		model.addAttribute("consultes", expedientTipusService.consultaFindAll(expedientTipusId));
		
		//accionsJbpm
		//TODO: encara falta pensar com es lligaran 		
	}
		
	private void omplirModelAgrupacions(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		List<CampAgrupacioDto> agrupacions = expedientTipusService.agrupacioFindAll(expedientTipusId);
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (CampAgrupacioDto agrupacio : agrupacions) {
			resposta.add(new ParellaCodiValorDto(agrupacio.getId().toString(), agrupacio.getNom()));
		}
		model.addAttribute("agrupacions", resposta);		
	}	
}
