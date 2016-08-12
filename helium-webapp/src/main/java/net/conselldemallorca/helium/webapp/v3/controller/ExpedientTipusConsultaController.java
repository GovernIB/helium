/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
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
import org.springframework.web.multipart.MultipartFile;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusConsultaCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusConsultaParamCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusConsultaVarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a les diferents consultes dels tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller("expedientTipusConsultaControllerV3")
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusConsultaController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@RequestMapping(value = "/{expedientTipusId}/consultes")
	public String consultes(
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
		}
		return "v3/expedientTipusConsulta";
	}
	
	@RequestMapping(value="/{expedientTipusId}/consulta/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.consultaFindPerDatatable(
						entornActual.getId(),
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}	
			
	@RequestMapping(value = "/{expedientTipusId}/consulta/new", method = RequestMethod.GET)
	public String nova(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		ExpedientTipusConsultaCommand command = new ExpedientTipusConsultaCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusConsultaCommand", command);
		this.omplirModelFormats(request, expedientTipusId, model);
		return "v3/expedientTipusConsultaForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/consulta/new", method = RequestMethod.POST)
	public String novaPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(value = "informeContingut_multipartFile", required = false) final MultipartFile multipartFile,
			@Validated(ExpedientTipusConsultaCommand.Creacio.class) ExpedientTipusConsultaCommand command,
			BindingResult bindingResult,
			Model model) throws PermisDenegatException, IOException {
        if (bindingResult.hasErrors()) {
        	this.omplirModelFormats(request, expedientTipusId, model);
        	return "v3/expedientTipusConsultaForm";
        } else {
        	command.setInformeContingut(IOUtils.toByteArray(multipartFile.getInputStream()));
        	// Verificar permisos
    		expedientTipusService.consultaCreate(
    				expedientTipusId,
        			ExpedientTipusConsultaCommand.asConsultaDto(command));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.consulta.controller.creat"));
			return modalUrlTancar(false);			
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/consulta/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		ConsultaDto dto = expedientTipusService.consultaFindAmbId(id);
		ExpedientTipusConsultaCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusConsultaCommand.class);		
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusConsultaCommand", command);
    	this.omplirModelFormats(request, expedientTipusId, model);
		return "v3/expedientTipusConsultaForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/consulta/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@RequestParam(value = "informeContingut_multipartFile", required = false) final MultipartFile multipartFile,
			@RequestParam(value = "informeContingut_deleted", required = false) final boolean deleted,
			@Validated(ExpedientTipusConsultaCommand.Modificacio.class) ExpedientTipusConsultaCommand command,
			BindingResult bindingResult,
			Model model) throws NoTrobatException, PermisDenegatException, IOException {
        if (bindingResult.hasErrors()) {
        	this.omplirModelFormats(request, expedientTipusId, model);
        	return "v3/expedientTipusConsultaForm";
        } else {
        	boolean actualitzarContingut = false;
        	if (deleted) {
        		command.setInformeContingut(null);
        		actualitzarContingut = true;
        	}
        	if (multipartFile != null && multipartFile.getSize() > 0) {
				command.setInformeContingut(IOUtils.toByteArray(multipartFile.getInputStream()));
				actualitzarContingut = true;
			}
        	expedientTipusService.consultaUpdate(
        			ExpedientTipusConsultaCommand.asConsultaDto(command),
        			actualitzarContingut);
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.consulta.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/consulta/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		try {
			expedientTipusService.consultaDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.consulta.llistat.accio.esborrar.correcte"));
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.consulta.llistat.accio.esborrar.error"));
			logger.error("S'ha produit un error al intentar eliminar la consulta amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			return false;
		}
	}
	
	/**
	 * Mètode Ajax per moure una consulta de posició dins del tipus d'expedient.
	 * @param request
	 * @param expedientTipusId
	 * @param id
	 * @param posicio
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{expedientTipusId}/consulta/{id}/moure/{posicio}")
	@ResponseBody
	public boolean mourePosicio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return expedientTipusService.consultaMourePosicio(id, posicio);
	}
	
	/** Mètode per descarregar l'informe de la consulta. */
	@RequestMapping(value="/{expedientTipusId}/consulta/{consultaId}/download", method = RequestMethod.GET)
	public String desacarregarInforme(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long consultaId,
			Model model) {
		ConsultaDto consulta = expedientTipusService.consultaFindAmbId(consultaId);
		if (consulta != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, consulta.getInformeNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, consulta.getInformeContingut());
		}
		return "arxiuView";
	}	
	
	private void omplirModelFormats(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		model.addAttribute("formats", new String[] {"PDF","ODT","RTF","HTML","CSV","XLS","XML"});		
	}
	
	// Mètodes pel manteniment de les variables de les consultes

	/** Modal per veure els camps de la consulta de tipus filtre. */
	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/varFiltre", method = RequestMethod.GET)
	public String variablesFiltre(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			Model model) {
		return this.variables(request, expedientTipusId, consultaId, model, TipusConsultaCamp.FILTRE);
	}

	/** Modal per veure els camps de la consulta de tipus informe. */
	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/varInforme", method = RequestMethod.GET)
	public String variablesInforme(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			Model model) {
		
		return this.variables(request, expedientTipusId, consultaId, model, TipusConsultaCamp.INFORME);
	}
	
	/** Mètode privat comú per veure els camps de la consulta per tipus. */
	private String variables(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			Model model,
			TipusConsultaCamp tipus) {
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("consulta", expedientTipusService.consultaFindAmbId(consultaId));
		model.addAttribute("tipus", tipus);

		ExpedientTipusConsultaVarCommand command = new ExpedientTipusConsultaVarCommand();
		command.setExpedientTipusId(expedientTipusId);
		command.setConsultaId(consultaId);
		command.setTipus(tipus);
		model.addAttribute("expedientTipusConsultaVarCommand", command);
		model.addAttribute("variables", obtenirParellesVariables(
				expedientTipusId,
				consultaId,
				command.getTipus()));

		return "v3/expedientTipusConsultaVar";
	}	
	
	@RequestMapping(value="/{expedientTipusId}/consulta/{consultaId}/var/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse variablesDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			@RequestParam(value="tipus", defaultValue="FILTRE", required = false) TipusConsultaCamp tipus,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.consultaCampFindPerDatatable(
						consultaId,
						tipus,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}		
	
	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/var/new", method = RequestMethod.POST)
	public String variableNouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			@Validated(ExpedientTipusConsultaVarCommand.Creacio.class) ExpedientTipusConsultaVarCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("consulta", expedientTipusService.consultaFindAmbId(consultaId));
    		model.addAttribute("tipus", command.getTipus());
    		model.addAttribute("variables", obtenirParellesVariables(
    				expedientTipusId,
    				consultaId,
    				command.getTipus()));
        	return "v3/expedientTipusConsultaVar";
        } else {
        	// Verificar permisos
    		expedientTipusService.consultaCampCreate(
    				consultaId,
    				ExpedientTipusConsultaVarCommand.asConsultaCampDto(command));    		
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.consulta.vars.controller.creat"));
        	return variables(request, expedientTipusId, consultaId, model, command.getTipus());
        }
	}	

	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/var/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean variableDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			@PathVariable Long id,
			Model model) {
				
		try {
			expedientTipusService.consultaCampDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.consulta.vars.controller.eliminar.success"));			
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.consulta.vars.controller.eliminar.error"));
			logger.error("S'ha produit un error al intentar eliminar la variable de la consulta amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId + "'", e);
			return false;
		}
	}
	
	/**
	 * Mètode Ajax per moure una validació d'una consulta de posició dins la seva agrupació.
	 * @param request
	 * @param expedientTipusId
	 * @param id
	 * @param posicio
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/var/{id}/moure/{posicio}")
	@ResponseBody
	public boolean variableMourePosicio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return expedientTipusService.consultaCampMourePosicio(id, posicio);
	}	
	
	/** Mètode per obtenir les possibles variables per al select a l'edició d'un registre via ajax. */
	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/var/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> variablesSelect(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			@RequestParam TipusConsultaCamp tipus,
			Model model) {
		return obtenirParellesVariables(expedientTipusId, consultaId, tipus);
	}	
			
	/**
	 * Retorna les parelles codi i valor per a les possibles variables per als camps de les consultes.
	 * Lleva les variables que s'han utilitzat ja en la consulta depenent del tipus.
	 * @param expedientTipusId
	 * @param consultaId
	 * @param tipus
	 * @return
	 */
	private List<ParellaCodiValorDto> obtenirParellesVariables(
			Long expedientTipusId,
			Long consultaId,
			TipusConsultaCamp tipus) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		// Obté totes les variables del tipus d'expedient
		List<CampDto> variables = expedientTipusService.campFindAllOrdenatsPerCodi(expedientTipusId);
		// Consulta els camps de la consulta segons el tipus
		List<ConsultaCampDto> camps = expedientTipusService.consultaCampFindCampAmbConsultaIdAndTipus(
				consultaId,
				tipus);
		// Lleva les variables que ja pertanyin a algun camp
		Iterator<CampDto> it = variables.iterator();
		while (it.hasNext()) {
			CampDto variable = it.next();
			for (ConsultaCampDto camp : camps) {
				if (variable.getId().equals(camp.getCampId())) {
					it.remove();
					break;
				}
			}
		}
		// Crea les parelles de codi i valor
		for (CampDto variable : variables) {
			resposta.add(new ParellaCodiValorDto(
					variable.getId().toString(), 
					variable.getCodi() + " / " + variable.getEtiqueta()));
		}
		return resposta;
	}		

	/** Modal per veure els paràmetres de la consulta de tipus informe. */
	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/parametre", method = RequestMethod.GET)
	public String parametres(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			Model model) {
		
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("consulta", expedientTipusService.consultaFindAmbId(consultaId));
		model.addAttribute("tipus", ConsultaCampDto.TipusConsultaCamp.PARAM);

		ExpedientTipusConsultaParamCommand command = new ExpedientTipusConsultaParamCommand();
		command.setExpedientTipusId(expedientTipusId);
		command.setConsultaId(consultaId);
		command.setTipus(TipusConsultaCamp.PARAM);
		model.addAttribute("expedientTipusConsultaParamCommand", command);
		model.addAttribute("tipusParameteres", ConsultaCampDto.TipusParamConsultaCamp.values());

		return "v3/expedientTipusConsultaParam";
	}
	
	@RequestMapping(value="/{expedientTipusId}/consulta/{consultaId}/parametre/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse parametresDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.consultaCampFindPerDatatable(
						consultaId,
						ConsultaCampDto.TipusConsultaCamp.PARAM,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/parametre/new", method = RequestMethod.POST)
	public String parametreNouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			@Validated(ExpedientTipusConsultaParamCommand.Creacio.class) ExpedientTipusConsultaParamCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("consulta", expedientTipusService.consultaFindAmbId(consultaId));
    		model.addAttribute("tipus", ConsultaCampDto.TipusConsultaCamp.PARAM);
        	model.addAttribute("mostraCreate", true);
        	return "v3/expedientTipusConsultaParam";
        } else {
        	// Verificar permisos
    		expedientTipusService.consultaCampCreate(
    				consultaId,
    				ExpedientTipusConsultaParamCommand.asConsultaCampDto(command));    		
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.consulta.params.controller.creat"));
        	return parametres(request, expedientTipusId, consultaId, model);
        }
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/parametre/{id}/update", method = RequestMethod.POST)
	public String parametreModificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			@PathVariable Long id,
			@Validated(ExpedientTipusConsultaParamCommand.Modificacio.class) ExpedientTipusConsultaParamCommand command,
			BindingResult bindingResult,
			Model model) {
	    if (bindingResult.hasErrors()) {
    		model.addAttribute("expedientTipusId", expedientTipusId);
    		model.addAttribute("consulta", expedientTipusService.consultaFindAmbId(consultaId));
    		model.addAttribute("tipus", ConsultaCampDto.TipusConsultaCamp.PARAM);
        	model.addAttribute("mostraUpdate", true);
        	return "v3/expedientTipusConsultaParam";
        } else {
        	expedientTipusService.consultaCampUpdate(
        			ExpedientTipusConsultaParamCommand.asConsultaCampDto(command));
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.campRegistre.controller.modificat"));
        	return parametres(request, expedientTipusId, consultaId, model);
        }
	}	

	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/parametre/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean parametreDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			@PathVariable Long id,
			Model model) {
				
		try {
			expedientTipusService.consultaCampDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.consulta.params.controller.eliminar.success"));			
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.consulta.params.controller.eliminar.error"));
			logger.error("S'ha produit un error al intentar eliminar el paràmetre de la consulta registre amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId + "'", e);
			return false;
		}
	}	
	private static final Log logger = LogFactory.getLog(ExpedientTipusConsultaController.class);
}
