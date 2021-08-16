/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusConsultaCommand;
import es.caib.helium.back.command.ExpedientTipusConsultaParamCommand;
import es.caib.helium.back.command.ExpedientTipusConsultaVarCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.view.ArxiuView;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.ConsultaCampDto;
import es.caib.helium.logic.intf.dto.ConsultaCampDto.TipusConsultaCamp;
import es.caib.helium.logic.intf.dto.ConsultaDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.util.Constants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Controlador per a les diferents consultes dels tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusConsultaController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private DissenyService dissenyService;


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
	
	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/var/{id}/{propietat}")
	@ResponseBody
	public boolean cols(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@PathVariable String propietat,
			@RequestParam int valor) {
		
		expedientTipusService.consultaCampCols(id, propietat, valor);
		
		return true;
	}
	
	/**
	 * Mètode Ajax per moure una consulta de posició dins del tipus d'expedient.
	 * @param request
	 * @param expedientTipusId
	 * @param id
	 * @param posicio
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
		return this.variables(request, expedientTipusId, consultaId, model, TipusConsultaCamp.FILTRE, null);
	}

	/** Modal per veure els camps de la consulta de tipus informe. */
	@RequestMapping(value = "/{expedientTipusId}/consulta/{consultaId}/varInforme", method = RequestMethod.GET)
	public String variablesInforme(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			Model model) {
		
		return this.variables(request, expedientTipusId, consultaId, model, TipusConsultaCamp.INFORME, null);
	}
	
	@RequestMapping(value = "/{expedientTipusId}/consulta/reportDownload")
	public String downloadAction(
			HttpServletRequest request,
			@RequestParam(value = "consultaId", required = true) Long consultaId,
			ModelMap model) {
		try {
			ConsultaDto consulta = dissenyService.getConsultaById(consultaId);
			String report = dissenyService.getPlantillaReport(consultaId);

			String nomInforme = consulta.getInformeNom();
			if (nomInforme==null) nomInforme = "report_"+consulta.getCodi()+".jrxml";
			byte[] byteArray = report.getBytes();
			model.addAttribute(
					ArxiuView.MODEL_ATTRIBUTE_FILENAME,
					nomInforme);
			model.addAttribute(
					ArxiuView.MODEL_ATTRIBUTE_DATA,
					byteArray);
			return "arxiuView";
		} catch (Exception e) {
			logger.error(e);
			return "redirect:/consulta/llistat.html";
		}
	}
	
	/** Mètode privat comú per veure els camps de la consulta per tipus. */
	private String variables(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			Model model,
			TipusConsultaCamp tipus,
			Long origen) {

		ExpedientTipusConsultaVarCommand command = new ExpedientTipusConsultaVarCommand();
		command.setExpedientTipusId(expedientTipusId);
		command.setConsultaId(consultaId);
		command.setTipus(tipus);
		if (origen != null)
			command.setOrigen(origen);
		else
			command.setOrigen(ExpedientTipusConsultaVarCommand.ORIGEN_EXPEDIENT);
		model.addAttribute("expedientTipusConsultaVarCommand", command);

		omplirModelVariables(
				request,
				command,
				model);

		return "v3/expedientTipusConsultaVar";
	}	
	
	/** Mètode privat per omplir el model pel formulari de variables. */
	private void omplirModelVariables(
			HttpServletRequest request, 
			ExpedientTipusConsultaVarCommand command,
			Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		List<DefinicioProcesDto> definicions = definicioProcesService.findAll(
				entornActual.getId(),
				command.getExpedientTipusId(),
				true); //incloureGlobals
	
		model.addAttribute("definicionsProces", definicions);

		model.addAttribute("expedientTipusId", command.getExpedientTipusId());
		model.addAttribute("consulta", expedientTipusService.consultaFindAmbId(command.getConsultaId()));
		model.addAttribute("tipus", command.getTipus());

		model.addAttribute("variables", obtenirParellesVariables(
				request,
				command.getExpedientTipusId(),
				command.getConsultaId(),
				command.getOrigen(),
				command.getTipus()));
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
    		omplirModelVariables(
    				request,
    				command,
    				model);
        	return "v3/expedientTipusConsultaVar";
        } else {
        	List<ConsultaCampDto> commandDtos = ExpedientTipusConsultaVarCommand.asConsultaCampDto(command);
        	if (command.getOrigen() >= 0) {
        		DefinicioProcesDto definicioProces = definicioProcesService.findById(command.getOrigen());
        		// Completa les dades de la definicó de procés
        		for (ConsultaCampDto commandDto : commandDtos) {
	        		commandDto.setDefprocJbpmKey(definicioProces.getJbpmKey());
	        		commandDto.setDefprocVersio(definicioProces.getVersio());
        		}
        	}
        	// Crea les variables i les afegeix
        	int nSuccess = 0;
    		for (ConsultaCampDto commandDto : commandDtos) {
    			try {
	        		expedientTipusService.consultaCampCreate(
	        				consultaId,
	        				commandDto);
	        		nSuccess++;
    			} catch (Exception e) {
    				MissatgesHelper.error(
    						request, 
    						getMessage(
    								request,
    								"expedient.tipus.consulta.vars.controller.crear.error",
    								new Object[] {commandDto.getCampCodi(), e.getLocalizedMessage()}));
    			}
    		}
        	// Esborra de la sessió el filtre
			SessionHelper.removeAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);
    		if (nSuccess > 0)
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"expedient.tipus.consulta.vars.controller.creat",
								new Object [] {nSuccess}));
        	return variables(request, expedientTipusId, consultaId, model, command.getTipus(), command.getOrigen());
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
			
        	// Esborra de la sessió el filtre
			SessionHelper.removeAttribute(request, SessionHelper.VARIABLE_FILTRE_CONSULTA_TIPUS + consultaId);

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
			@RequestParam Long origen,
			@RequestParam TipusConsultaCamp tipus,
			Model model) {
		return obtenirParellesVariables(request, expedientTipusId, consultaId, origen, tipus);
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
			HttpServletRequest request,
			Long expedientTipusId,
			Long consultaId,
			Long origen,
			TipusConsultaCamp tipus) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		if (origen == ExpedientTipusConsultaVarCommand.ORIGEN_EXPEDIENT) {
			// Variables del tipus d'expedient
			resposta.add(new ParellaCodiValorDto(
					Constants.EXPEDIENT_CAMP_ID,
					getMessage(request, "etiqueta.exp.id")));
			resposta.add(new ParellaCodiValorDto(
					Constants.EXPEDIENT_CAMP_NUMERO,
					getMessage(request, "etiqueta.exp.numero")));
			resposta.add(new ParellaCodiValorDto(
					Constants.EXPEDIENT_CAMP_TITOL,
					getMessage(request, "etiqueta.exp.titol")));
			resposta.add(new ParellaCodiValorDto(
					Constants.EXPEDIENT_CAMP_DATA_INICI,
					getMessage(request, "etiqueta.exp.data_ini")));
			resposta.add(new ParellaCodiValorDto(
					Constants.EXPEDIENT_CAMP_ESTAT,
					getMessage(request, "etiqueta.exp.estat")));
			
			boolean isGeorefActiu = "true".equalsIgnoreCase(aplicacioService.getGlobalProperties().getProperty("es.caib.helium.georef.actiu"));
			boolean isGeorefAmbReferencia = "ref".equalsIgnoreCase(aplicacioService.getGlobalProperties().getProperty("es.caib.helium.georef.tipus"));
			
			if (isGeorefActiu)
				if (isGeorefAmbReferencia)
					resposta.add(new ParellaCodiValorDto(
							Constants.EXPEDIENT_CAMP_GEOREF,
							getMessage(request, "comuns.georeferencia.codi")));
				else {
					resposta.add(new ParellaCodiValorDto(
							Constants.EXPEDIENT_CAMP_GEOX,
							getMessage(request, "comuns.georeferencia.coordenadaX")));
					resposta.add(new ParellaCodiValorDto(
							Constants.EXPEDIENT_CAMP_GEOY,
							getMessage(request, "comuns.georeferencia.coordenadaY")));
				}
		} else if (origen == ExpedientTipusConsultaVarCommand.ORIGEN_TIPUS_EXPEDIENT){
			// Variables del tipus d'expedient
			// Obté totes les variables del tipus d'expedient
			List<CampDto> variables = campService.findAllOrdenatsPerCodi(expedientTipusId, null);
			// Crea les parelles de codi i valor
			for (CampDto variable : variables) {
				resposta.add(new ParellaCodiValorDto(
						variable.getCodi(), 
						variable.getCodi() + " / " + variable.getEtiqueta()));
			}
		} else {
			// Variables de la definició de procés
			// Obté totes les variables del tipus d'expedient
			List<CampDto> variables = campService.findAllOrdenatsPerCodi(null, origen);
			// Crea les parelles de codi i valor
			for (CampDto variable : variables) {
				resposta.add(new ParellaCodiValorDto(
						variable.getCodi(), 
						variable.getCodi() + " / " + variable.getEtiqueta()));
			}
		}
		// Consulta els camps existents de la consulta segons el tipus
		Set<String> campsExistents = new HashSet<String>();
		for (ConsultaCampDto consultaCamp : expedientTipusService.consultaCampFindCampAmbConsultaIdAndTipus(
				consultaId,
				tipus))
			campsExistents.add(consultaCamp.getCampCodi());
		// Lleva les variables que coincideixin amb algun codi ja utilitzat. Si es volen utilitzar variables amb
		// el mateix codi i de diferent origen s'haurà de modificar
		Iterator<ParellaCodiValorDto> it = resposta.iterator();
		while (it.hasNext()) {
			ParellaCodiValorDto parellaCodiValor = it.next();
			if (campsExistents.contains(parellaCodiValor.getCodi())) 
				it.remove();
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
		model.addAttribute("tipus", TipusConsultaCamp.PARAM);

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
						TipusConsultaCamp.PARAM,
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
    		model.addAttribute("tipus", TipusConsultaCamp.PARAM);
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
    		model.addAttribute("tipus", TipusConsultaCamp.PARAM);
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
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusConsultaController.class);
}
