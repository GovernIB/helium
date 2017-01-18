/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitSistraDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitSistraEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioTramitsCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioTramitsMapeigCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioTramitsSistraCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxFormResponse;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pestanya de d'integració dels tipus d'expedient amb els tràmits de Sistra.
 * També és el controlador pel mapeig de variables, documents i adjunts. Els mapejos es fan
 * sobre una mateixa taula, però en el cas dels adjunts nomès és necessari un camp mentre que per
 * documents i variables és necessària la variable. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusIntegracioTramitsController extends BaseExpedientTipusController {
	
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramits")
	public String tramits(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"consultes");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			
			ExpedientTipusIntegracioTramitsCommand command = new ExpedientTipusIntegracioTramitsCommand();			
			command.setId(expedientTipusId);
			if (expedientTipus.getSistraTramitCodi() != null) {
				command.setTramitCodi(expedientTipus.getSistraTramitCodi());
				command.setActiu(true);
			} else {
				command.setActiu(false);
			}
			
			command.setNotificacionsActivades(expedientTipus.isNotificacionsActivades());
			command.setNotificacioOrganCodi(expedientTipus.getNotificacioOrganCodi());
			command.setNotificacioOficinaCodi(expedientTipus.getNotificacioOficinaCodi());
			command.setNotificacioUnitatAdministrativa(expedientTipus.getNotificacioUnitatAdministrativa());
			command.setNotificacioCodiProcediment(expedientTipus.getNotificacioCodiProcediment());
			command.setNotificacioAvisTitol(expedientTipus.getNotificacioAvisTitol());
			command.setNotificacioAvisText(expedientTipus.getNotificacioAvisText());
			command.setNotificacioAvisTextSms(expedientTipus.getNotificacioAvisTextSms());
			command.setNotificacioOficiTitol(expedientTipus.getNotificacioOficiTitol());
			command.setNotificacioOficiText(expedientTipus.getNotificacioOficiText());
			
			model.addAttribute("expedientTipusIntegracioTramitsCommand", command);

			model.addAttribute("tipusMapeigVariable", TipusMapeig.Variable);
			model.addAttribute("tipusMapeigDocument", TipusMapeig.Document);
			model.addAttribute("tipusMapeigAdjunt", TipusMapeig.Adjunt);
			
			Map<TipusMapeig, Long> recomptes = expedientTipusService.mapeigCountsByTipus(expedientTipusId);
			model.addAttribute("variablesCount" , recomptes.get(TipusMapeig.Variable));
			model.addAttribute("documentsCount" , recomptes.get(TipusMapeig.Document));
			model.addAttribute("adjuntsCount" , recomptes.get(TipusMapeig.Adjunt));
		}
		
		return "v3/expedientTipusIntegracioTramits";
	}
	
	@RequestMapping(value="/{expedientTipusId}/integracioTramits/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatableTramits(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.tramitSistraFindPerDatatable(
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams));		
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramits", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse tramitsPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusIntegracioTramitsCommand.Modificacio.class) ExpedientTipusIntegracioTramitsCommand command,
			BindingResult bindingResult,
			Model model) throws PermisDenegatException, IOException {
		
		AjaxFormResponse response = AjaxHelper.generarAjaxFormOk();
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		if (entornActual != null) {
			// Comprova que la url estigui informada si està activat
			if (command.isActiu() && (command.getTramitCodi() == null || "".equals(command.getTramitCodi().trim()))) {
				bindingResult.rejectValue("tramitCodi", "NotEmpty");
			}
	        if (bindingResult.hasErrors()) {
	        	response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
	        } else {
	        	if (command.isActiu())
	        		expedientTipusService.updateIntegracioTramits(
	        				entornActual.getId(),
	        				expedientTipusId,
	        				command.getTramitCodi(),
	        				command.isNotificacionsActivades(),
	        				command.getNotificacioOrganCodi(),
	        				command.getNotificacioOficinaCodi(),
	        				command.getNotificacioUnitatAdministrativa(),
	        				command.getNotificacioCodiProcediment(),
	        				command.getNotificacioAvisTitol(),
	        				command.getNotificacioAvisText(),
	        				command.getNotificacioAvisTextSms(),
	        				command.getNotificacioOficiTitol(),
	        				command.getNotificacioOficiText());
	        	else
	        		expedientTipusService.updateIntegracioTramits(
	        				entornActual.getId(),
	        				expedientTipusId,
	        				null,
	        				command.isNotificacionsActivades(),
	        				command.getNotificacioOrganCodi(),
	        				command.getNotificacioOficinaCodi(),
	        				command.getNotificacioUnitatAdministrativa(),
	        				command.getNotificacioCodiProcediment(),
	        				command.getNotificacioAvisTitol(),
	        				command.getNotificacioAvisText(),
	        				command.getNotificacioAvisTextSms(),
	        				command.getNotificacioOficiTitol(),
	        				command.getNotificacioOficiText());
		        MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.integracio.tramits.controller.guardat"));
	        }
		}
    	return response;
	}	
	
	
	// Mètodes pel manteniment mapejos amb variables, documents i adjunts

	/** Modal per veure els camps de la consulta de tipus filtre. */
	@RequestMapping(value = "/{expedientTipusId}/integracioTramits/{tramitSistraId}/mapeig/{tipus}", method = RequestMethod.GET)
	public String mapeig(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tramitSistraId,
			@PathVariable TipusMapeig tipus,
			Model model) {
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("tramitSistraId", tramitSistraId);
		model.addAttribute("tipus", tipus);
		ExpedientTipusIntegracioTramitsMapeigCommand command = new ExpedientTipusIntegracioTramitsMapeigCommand();
		command.setExpedientTipusId(expedientTipusId);
		command.setTramitSistraId(tramitSistraId);
		command.setTipus(tipus);
		model.addAttribute("expedientTipusIntegracioTramitsMapeigCommand", command);
		if (tipus != TipusMapeig.Adjunt) 
			model.addAttribute("variables", obtenirParellesVariables(
					expedientTipusId,
					command.getTipus()));

		return "v3/expedientTipusIntegracioTramitsMapeig";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramits/{tramitSistraId}/mapeig/{tipus}/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse mapeigDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tramitSistraId,
			@PathVariable TipusMapeig tipus,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.mapeigFindPerDatatable(
						expedientTipusId,
						tramitSistraId,
						tipus,
						paginacioParams),
				"id");
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramits/{tramitSistraId}/mapeig/{tipus}/new", method = RequestMethod.POST)
	public String mapeigNouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tramitSistraId,
			@PathVariable TipusMapeig tipus,
			@Validated(ExpedientTipusIntegracioTramitsMapeigCommand.Creacio.class) ExpedientTipusIntegracioTramitsMapeigCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("tramitSistraId", tramitSistraId);
    		model.addAttribute("tipus", tipus);
    		if (tipus != TipusMapeig.Adjunt) 
    			model.addAttribute("variables", obtenirParellesVariables(
    				expedientTipusId,
    				command.getTipus()));
        	model.addAttribute("mostraCreate", true);
        	
        	return "v3/expedientTipusIntegracioTramitsMapeig";
        } else {
        	// Verificar permisos
    		expedientTipusService.mapeigCreate(
    				expedientTipusId,
    				tramitSistraId,
    				ExpedientTipusIntegracioTramitsMapeigCommand.asMapeigSistraDto(command));    		
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.integracio.tramits.mapeig.controller.creat"));
        	return mapeig(request, expedientTipusId, tramitSistraId, tipus, model);
        }
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramits/{tramitSistraId}/mapeig/{tipus}/{id}/update", method = RequestMethod.POST)
	public String mapeigModificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tramitSistraId,
			@PathVariable TipusMapeig tipus,
			@PathVariable Long id,
			@Validated(ExpedientTipusIntegracioTramitsMapeigCommand.Modificacio.class) ExpedientTipusIntegracioTramitsMapeigCommand command,
			BindingResult bindingResult,
			Model model) {
	    if (bindingResult.hasErrors()) {
    		model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("tramitSistraId", tramitSistraId);
    		model.addAttribute("tipus", tipus);
    		if (tipus != TipusMapeig.Adjunt) 
    			model.addAttribute("variables", obtenirParellesVariables(
    				expedientTipusId,
    				command.getTipus()));
        	model.addAttribute("mostraUpdate", true);
        	return "v3/expedientTipusIntegracioTramitsMapeig";
        } else {
        	expedientTipusService.mapeigUpdate(
        			ExpedientTipusIntegracioTramitsMapeigCommand.asMapeigSistraDto(command));
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.campRegistre.controller.modificat"));
        	return mapeig(request, expedientTipusId, tramitSistraId, tipus, model);
        }
	}	

	@RequestMapping(value = "/{expedientTipusId}/integracioTramits/{tramitSistraId}/mapeig/{tipus}/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean mapeigDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tramitSistraId,
			@PathVariable TipusMapeig tipus,
			@PathVariable Long id,
			Model model) {
				
		expedientTipusService.mapeigDelete(id);
		MissatgesHelper.success(
				request,
				getMessage(
						request,
						"expedient.tipus.integracio.tramits.mapeig.controller.eliminar.success"));			
		return true;
	}	
	
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramit/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		ExpedientTipusIntegracioTramitsSistraCommand command = new ExpedientTipusIntegracioTramitsSistraCommand();
		command.setExpedientTipusId(expedientTipusId);
		
		this.omplirModelVariableForm(
				request, 
				expedientTipusId, 
				model);
		
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("expedientTipusIntegracioTramitsSistraCommand", command);
		
		return "v3/expedientTipusTramitSistraForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramit/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusIntegracioTramitsSistraCommand.Creacio.class) ExpedientTipusIntegracioTramitsSistraCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("expedientTipusId", expedientTipusId);
        	this.omplirModelVariableForm(
        			request, 
        			expedientTipusId, 
        			model);
        	return "v3/expedientTipusTramitSistraForm";
        } else {
        	// Verificar permisos
    		expedientTipusService.tramitSistraCreate(
    				expedientTipusId,
    				ExpedientTipusIntegracioTramitsSistraCommand.asTramitSistraDto(command));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.integracio.tramits.creat"));
			return modalUrlTancar(false);	
			
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramit/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			Model model) {
		TramitSistraDto dto = expedientTipusService.tramitSistraFindAmbId(id);
		ExpedientTipusIntegracioTramitsSistraCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusIntegracioTramitsSistraCommand.class);
		
		this.omplirModelVariableForm(
				request, 
				expedientTipusId, 
				model);
		
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("expedientTipusIntegracioTramitsSistraCommand", command);
		
		return "v3/expedientTipusTramitSistraForm";
	}

	@RequestMapping(value = "/{expedientTipusId}/integracioTramit/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			@Validated(ExpedientTipusIntegracioTramitsSistraCommand.Modificacio.class) ExpedientTipusIntegracioTramitsSistraCommand command,
			BindingResult bindingResult, Model model) {
		
		if (bindingResult.hasErrors()) {
	        	model.addAttribute("expedientTipusId", expedientTipusId);
        	this.omplirModelVariableForm(
        			request,
        			expedientTipusId,
        			model);
        	return "v3/expedientTipusTramitSistraForm";
        } else {
        	TramitSistraDto dto = ExpedientTipusIntegracioTramitsSistraCommand.asTramitSistraDto(command);
			expedientTipusService.tramitSistraUpdate(expedientTipusId, dto);
			
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.integracio.tramits.modificat"));
			return modalUrlTancar(false);
			
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramit/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {

		try {
			expedientTipusService.tramitSistraDelete(expedientTipusId, id);
			MissatgesHelper.success(request, getMessage(request, "expedient.tipus.integracio.tramits.eliminat"));
			return true;
		}catch (ValidacioException ex) {
			MissatgesHelper.error(request, ex.getMessage());
			return false;
		}
	}
	
	/**
	 * Retorna les parelles codi i valor per a les possibles variables per als mapejos de variables
	 * i documents.
	 * Lleva les variables que s'han utilitzat ja en el mapeig depenent del tipus.
	 * @param expedientTipusId
	 * @param consultaId
	 * @param tipus
	 * @return
	 */
	private List<ParellaCodiValorDto> obtenirParellesVariables(
			Long expedientTipusId,
			TipusMapeig tipus) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		// Obté totes les variables del tipus d'expedient
		List<CampDto> variables = campService.findAllOrdenatsPerCodi(expedientTipusId, null);
		// Crea les parelles de codi i valor
		for (CampDto variable : variables) {
			resposta.add(new ParellaCodiValorDto(
					variable.getCodi() + " / " + variable.getEtiqueta(),
					variable.getCodi()));
		}
		return resposta;
	}		
	
	private void omplirModelVariableForm(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		// Tipus d'operaicó
		List<ParellaCodiValorDto> tipusOperacio = new ArrayList<ParellaCodiValorDto>();
		for (TramitSistraEnumDto operacioTipus : TramitSistraEnumDto.values()) {
			tipusOperacio.add(
					new ParellaCodiValorDto(operacioTipus.toString(), 
					getMessage(
							request, 
							"expedient.tipus.integracio.tramits.tipus." + operacioTipus)));
		}
		model.addAttribute("tipusOperacio",tipusOperacio);
		
		DefinicioProcesExpedientDto definicioProces = dissenyService.getDefinicioProcesByTipusExpedientById(expedientTipusId);
		List<AccioDto> accions = accioService.findAll(expedientTipusId, definicioProces.getId());
		model.addAttribute("accions", accions);
	}
	
//	private static final Log logger = LogFactory.getLog(ExpedientTipusIntegracioTramitsController.class);
}
