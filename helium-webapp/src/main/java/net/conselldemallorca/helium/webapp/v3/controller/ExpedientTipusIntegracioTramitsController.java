/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.MapeigSistraDto.TipusMapeig;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioTramitsCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioTramitsMapeigCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxFormResponse;
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
			// Validació en el controlador
			this.validarCommand(command, bindingResult);
	        if (bindingResult.hasErrors()) {
	        	response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
	        } else {
        		expedientTipusService.updateIntegracioTramits(
        				entornActual.getId(),
        				expedientTipusId,
        				command.isActiu() ? 
        						command.getTramitCodi() 
        						: null,
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
	

	/** Per comprovar si un text és null o buit */
	private boolean isNullOrEmpty(String str) {
		return str == null? true : "".equals(str.trim());
	}

	/** Valida els camps del command depenent de les opcions que estiguin actives. 
	 * @param bindingResult */
	private void validarCommand(ExpedientTipusIntegracioTramitsCommand command, BindingResult bindingResult) {
		// Comprova que la url estigui informada si està activat
		if (command.isActiu()) {
			if (isNullOrEmpty(command.getTramitCodi())){
				// tramitCodi
				if (isNullOrEmpty(command.getNotificacioOrganCodi())) {
					bindingResult.rejectValue("tramitCodi", "NotEmpty");
				}				
			}
		}
		if (command.isNotificacionsActivades()) {
			// notificacioOrganCodi
			if (isNullOrEmpty(command.getNotificacioOrganCodi())) {
				bindingResult.rejectValue("notificacioOrganCodi", "NotEmpty");
			}				
			// notificacioOficinaCodi
			if (isNullOrEmpty(command.getNotificacioOficinaCodi())) {
				bindingResult.rejectValue("notificacioOficinaCodi", "NotEmpty");
			}				
			// notificacioUnitatAdministrativa
			if (isNullOrEmpty(command.getNotificacioUnitatAdministrativa())) {
				bindingResult.rejectValue("notificacioUnitatAdministrativa", "NotEmpty");
			}				
			// notificacioCodiProcediment
			if (isNullOrEmpty(command.getNotificacioCodiProcediment())) {
				bindingResult.rejectValue("notificacioCodiProcediment", "NotEmpty");
			}				
		}
	}

	// Mètodes pel manteniment mapejos amb variables, documents i adjunts


	/** Modal per veure els camps de la consulta de tipus filtre. */
	@RequestMapping(value = "/{expedientTipusId}/integracioTramits/mapeig/{tipus}", method = RequestMethod.GET)
	public String mapeig(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable TipusMapeig tipus,
			Model model) {
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("tipus", tipus);
		ExpedientTipusIntegracioTramitsMapeigCommand command = new ExpedientTipusIntegracioTramitsMapeigCommand();
		command.setExpedientTipusId(expedientTipusId);
		command.setTipus(tipus);
		model.addAttribute("expedientTipusIntegracioTramitsMapeigCommand", command);
		if (tipus != TipusMapeig.Adjunt) 
			model.addAttribute("variables", obtenirParellesVariables(
					SessionHelper.getSessionManager(request).getEntornActual().getId(),
					expedientTipusId,
					command.getTipus()));

		return "v3/expedientTipusIntegracioTramitsMapeig";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramits/mapeig/{tipus}/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse mapeigDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable TipusMapeig tipus,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.mapeigFindPerDatatable(
						expedientTipusId,
						tipus,
						paginacioParams),
				"id");
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramits/mapeig/{tipus}/new", method = RequestMethod.POST)
	public String mapeigNouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable TipusMapeig tipus,
			@Validated(ExpedientTipusIntegracioTramitsMapeigCommand.Creacio.class) ExpedientTipusIntegracioTramitsMapeigCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("tipus", tipus);
    		if (tipus != TipusMapeig.Adjunt) 
    			model.addAttribute("variables", obtenirParellesVariables(
   					SessionHelper.getSessionManager(request).getEntornActual().getId(),
    				expedientTipusId,
    				command.getTipus()));
        	model.addAttribute("mostraCreate", true);
        	
        	return "v3/expedientTipusIntegracioTramitsMapeig";
        } else {
        	// Verificar permisos
    		expedientTipusService.mapeigCreate(
    				expedientTipusId,
    				ExpedientTipusIntegracioTramitsMapeigCommand.asMapeigSistraDto(command));    		
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.integracio.tramits.mapeig.controller.creat"));
        	return mapeig(request, expedientTipusId, tipus, model);
        }
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/integracioTramits/mapeig/{tipus}/{id}/update", method = RequestMethod.POST)
	public String mapeigModificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable TipusMapeig tipus,
			@PathVariable Long id,
			@Validated(ExpedientTipusIntegracioTramitsMapeigCommand.Modificacio.class) ExpedientTipusIntegracioTramitsMapeigCommand command,
			BindingResult bindingResult,
			Model model) {
	    if (bindingResult.hasErrors()) {
    		model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("tipus", tipus);
    		if (tipus != TipusMapeig.Adjunt) 
    			model.addAttribute("variables", obtenirParellesVariables(
   					SessionHelper.getSessionManager(request).getEntornActual().getId(),
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
        	return mapeig(request, expedientTipusId, tipus, model);
        }
	}	

	@RequestMapping(value = "/{expedientTipusId}/integracioTramits/mapeig/{tipus}/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean mapeigDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
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
			Long entornId,
			Long expedientTipusId,
			TipusMapeig tipus) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
				entornId, 
				expedientTipusId);
		DefinicioProcesDto definicioProces = null;
		if (!expedientTipus.isAmbInfoPropia() && expedientTipus.getJbpmProcessDefinitionKey() != null)
			definicioProces = definicioProcesService.findByEntornIdAndJbpmKey(entornId, expedientTipus.getJbpmProcessDefinitionKey());
		
		if (tipus.equals(TipusMapeig.Variable)) {
			// Variables
			List<CampDto> variables;
			if (expedientTipus.isAmbInfoPropia())
				// Obté totes les variables del tipus d'expedient
				variables = campService.findAllOrdenatsPerCodi(expedientTipusId, null);
			else {
				if (definicioProces != null)
					// Obté les variables de la definició de procés del tipus d'expedient
					variables = campService.findAllOrdenatsPerCodi(null, definicioProces.getId());	
				else 
					variables = new ArrayList<CampDto>();
			}
			// Crea les parelles de codi i valor
			for (CampDto variable : variables) {
				resposta.add(new ParellaCodiValorDto(
						variable.getCodi() + " / " + variable.getEtiqueta(),
						variable.getCodi()));
			}
		} else if (tipus.equals(TipusMapeig.Document)) {
			// Documents
			List<DocumentDto> documents;
			if (expedientTipus.isAmbInfoPropia())
				// Obté totes els documents del tipus d'expedient
				documents = documentService.findAll(expedientTipusId, null);
			else {
				if (definicioProces != null)
					// Obté els documents de la definició de procés del tipus d'expedient
					documents = documentService.findAll(null, definicioProces.getId());	
				else 
					documents = new ArrayList<DocumentDto>();
			}
			// Crea les parelles de codi i valor
			for (DocumentDto document : documents) {
				resposta.add(new ParellaCodiValorDto(
						document.getCodi() + " / " + document.getNom(),
						document.getCodi()));
			}			
		}
		return resposta;
	}		
}
