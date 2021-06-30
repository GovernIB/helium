package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesExpedientDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.DocumentTascaDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.FirmaTascaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.dto.TascaDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesExpedientDto.IdAmbEtiqueta;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesTascaCommand;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesTascaDocumentCommand;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesTascaFirmaCommand;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesTascaVariableCommand;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pipella de tasques del tipus d'expedient. Aquesta pipella
 * permet editar les variables, documents i signatures de les tasques escollint la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusTascaController extends BaseTascaDissenyController {


	/** Pipella de les tasques depenent de la definició de procés seleccionada. */
	@RequestMapping(value = "/{expedientTipusId}/tasques")
	public String tasques(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"tasques");
		}
		omplirModelTasquesPestanya(
				request,
				expedientTipusId,
				model);
		return "v3/expedientTipusTasca";
	}
	
	/** Mètode per obtenir les possibles versions per al select de definicions de procés via ajax. */
	@RequestMapping(value = "/{expedientTipusId}/definicio/{definicioId}/versions/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> definicioVersioSelect(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioId,
			Model model) {

		// Select de les versions
		List<ParellaCodiValorDto> versions = new ArrayList<ParellaCodiValorDto>();
		if (definicioId != null) {
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			DefinicioProcesExpedientDto d = dissenyService.getDefinicioProcesByEntorIdAndProcesId(
					entornActual.getId(), 
					definicioId);
			for (IdAmbEtiqueta i : d.getListIdAmbEtiqueta()) {
				versions.add(new ParellaCodiValorDto(i.getId().toString(), i.getEtiqueta()));
			}
		}		
		return versions;
	}

	
	/** Retorna les dades de les tasques pel datatables de tasques. Es filtra per definició de procés.
	 * @param request
	 * @param expedientTipusId
	 * @param definicioProcesId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/{expedientTipusId}/tasca/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) Long definicioProcesId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DatatablesResponse response;
		if (definicioProcesId != null) {
			response = DatatablesHelper.getDatatableResponse(
					request,
					null,
					definicioProcesService.tascaFindPerDatatable(
							entornActual.getId(),
							expedientTipusId,
							definicioProcesId,
							paginacioParams.getFiltre(),
							paginacioParams),
					"id");
		} else {
			response = DatatablesHelper.getEmptyDatatableResponse(request);
		}
		
		return response;		
	}
	
	private void omplirModelTasquesPestanya(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			model.addAttribute("baseUrl", expedientTipus.getId());

			// Consulta les possibles definicions de procés amb herència
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.setPaginaNum(0);
			paginacioParams.setPaginaTamany(Integer.MAX_VALUE);
			paginacioParams.afegirOrdre("jbpmKey", OrdreDireccioDto.ASCENDENT);		
			List<DefinicioProcesDto> definicions =	definicioProcesService.findPerDatatable(
							entornActual.getId(),
							expedientTipusId,
							false,
							null,
							paginacioParams).getContingut();
			// Construeix les possibles opcions per la selecció de la definició de procés
			List<ParellaCodiValorDto> opcionsDefinicions = new ArrayList<ParellaCodiValorDto>();
			for (DefinicioProcesDto definicio : definicions) {
				opcionsDefinicions.add(new ParellaCodiValorDto(definicio.getId().toString(), definicio.getJbpmKey()));
				// Si és la inicial es passa el seu id a la vista per a que la carregui
				if (definicio.getJbpmKey().equals(expedientTipus.getJbpmProcessDefinitionKey()))
					model.addAttribute("definicioInical", definicio.getId());
			}
			// Informa el model
			model.addAttribute("definicions", opcionsDefinicions);
			
			// Afegeix una llista de versions de definicions de procés buïda
			model.addAttribute("versions", new ArrayList<ParellaCodiValorDto>());
			
			// Guarda els identificadors de les definicions heretades i sobreescrites
			List<Long> definicionsHeretadesIds = new ArrayList<Long>();
			List<Long> definicionsSobreescriuenIds = new ArrayList<Long>();
			if (expedientTipus.getExpedientTipusPareId() != null) {
				for (DefinicioProcesDto d : definicions) {
					if (d.isHeretat())
						definicionsHeretadesIds.add(d.getId());
					if (d.isSobreescriu())
						definicionsSobreescriuenIds.add(d.getId());
				}
			}
			model.addAttribute("definicionsHeretadesIds", definicionsHeretadesIds);
			model.addAttribute("definicionsSobreescriuenIds", definicionsSobreescriuenIds);
		}
	}
	
	
	// Manteniment de la tasca
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			Model model) {
		
		TascaDto dto = definicioProcesService.tascaFindAmbId(expedientTipusId, tascaId);
		DefinicioProcesTascaCommand command = DefinicioProcesTascaCommand.toDefinicioProcesTascaCommand(dto);	
		model.addAttribute("definicioProcesTascaCommand", command);
		model.addAttribute("heretat", dto.isHeretat());
		return "v3/definicioProcesTascaForm";	
	}
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			@Validated(DefinicioProcesTascaCommand.Modificacio.class) DefinicioProcesTascaCommand command,
			BindingResult bindingResult,
			Model model) throws NoTrobatException, PermisDenegatException, IOException {
        if (bindingResult.hasErrors()) {
        	return "v3/definicioProcesTascaForm";
        } else {
        	definicioProcesService.tascaUpdate(
        			DefinicioProcesTascaCommand.asTascaDto(command));
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"definicio.proces.tasca.controller.modificat"));
			return modalUrlTancar(false);
        }
	}	
	
	
	/** Omple dades comuns per totes les pàgines
	 * 
	 */
	private void omplirModelComu(
			Long expedientTipusId,
			Long tascaId,
			Model model) {
		
		// Especifica les URLs per la pàgina
		String basicUrl = "expedientTipus/" + expedientTipusId  + "/tasca/" + tascaId;
		model.addAttribute("basicUrl", basicUrl);
		model.addAttribute("baseUrl", "/helium/v3/" + basicUrl);
	}
	
	
	// Manteniment de variables de la tasca
	
	/** Modal per veure els camps de la tasca de tipus filtre. */
	@RequestMapping(value = "/{expedientTipusId}/tasca/{id}/variable", method = RequestMethod.GET)
	public String variables(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {

		DefinicioProcesTascaVariableCommand command = new DefinicioProcesTascaVariableCommand();
		command.setTascaId(id);
		command.setReadFrom(true);
		command.setWriteTo(true);
		command.setAmpleCols(12);
		command.setBuitCols(0);
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("definicioProcesTascaVariableCommand", command);

		omplirModelVariables(expedientTipusId, id, model);

		return "v3/definicioProcesTascaVariable";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/variable/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse variablesDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				definicioProcesService.tascaCampFindPerDatatable(
						tascaId,
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{id}/variable/new", method = RequestMethod.POST)
	public String variableNouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(DefinicioProcesTascaVariableCommand.Creacio.class) DefinicioProcesTascaVariableCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
    		omplirModelVariables(expedientTipusId, id, model);
    		return "v3/definicioProcesTascaVariable";
        } else {
        	// Verificar permisos
    		definicioProcesService.tascaCampCreate(
    				id,
    				DefinicioProcesTascaVariableCommand.asCampTascaDto(command));    		
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"definicio.proces.tasca.controller.variable.creat"));
        	return variables(request, expedientTipusId, id, model);
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/variable/{campTascaId}/{propietat}", method = RequestMethod.POST)
	@ResponseBody
	public boolean variableUpdatePropietat(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			@PathVariable Long campTascaId,
			@PathVariable String propietat,
			@RequestParam Object valor) {
		
		CampTascaDto campTasca = definicioProcesService.tascaCampFindById(expedientTipusId, campTascaId);
		if ("readFrom".equals(propietat)) {
			campTasca.setReadFrom(Boolean.parseBoolean(valor.toString()));
		} else if ("writeTo".equals(propietat)) {
			campTasca.setWriteTo(Boolean.parseBoolean(valor.toString()));
		} else if ("required".equals(propietat)) {
			campTasca.setRequired(Boolean.parseBoolean(valor.toString()));
		} else if ("readOnly".equals(propietat)) {
			campTasca.setReadOnly(Boolean.parseBoolean(valor.toString()));
		} else if ("ampleCols".equals(propietat)) {
			campTasca.setAmpleCols(Integer.parseInt(valor.toString()));
		} else if ("buitCols".equals(propietat)) {
			campTasca.setBuitCols(Integer.parseInt(valor.toString()));
		}
		definicioProcesService.tascaCampUpdate(campTasca);
		
		return true;
	}
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/variable/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean variableDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			@PathVariable Long id,
			Model model) {
				
		definicioProcesService.tascaCampDelete(id);
		
		MissatgesHelper.success(
				request,
				getMessage(
						request,
						"definicio.proces.tasca.controller.variable.eliminat"));			
		return true;
	}
		
	/**
	 * Mètode Ajax per moure una variable d'una tasca de posició.
	 * @param request
	 * @param expedientTipusId
	 * @param id
	 * @param posicio
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/variable/{id}/moure/{posicio}")
	@ResponseBody
	public boolean variableMourePosicio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		boolean ret = false;
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		expedientTipusService.findAmbIdPermisDissenyarDelegat(
				entornActual.getId(), 
				expedientTipusId); 

		CampTascaDto estat = definicioProcesService.tascaCampFindById(expedientTipusId, id); 
		boolean correcte = true;
		if (estat.isHeretat()) {
			MissatgesHelper.error(
			request, 
			getMessage(
					request, 
					"expedient.tipus.tasca.controller.moure.heretat.error"));
			correcte = false;
		} else {
			// rectifica la posició restant tots els heretats que té per davant
			int nHeretats = 0;
			for(CampTascaDto e : definicioProcesService.tascaCampFindAll(expedientTipusId, tascaId))
				if (e.isHeretat())
					nHeretats ++;
			if (posicio < nHeretats) {
				MissatgesHelper.error(
				request, 
				getMessage(
						request, 
						"expedient.tipus.tasca.controller.moure.heretat.error"));
				correcte = false;
			}
		}
		if (correcte)
			ret = definicioProcesService.tascaCampMourePosicio(id, expedientTipusId, posicio);
		else
			ret = false;
		
		return ret;
	}
	
	/** Mètode per obtenir les possibles variables per al select a l'edició d'un registre via ajax. */
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/variable/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> variablesSelect(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			Model model) {

		List<CampDto> variables = dissenyService.findCampsOrdenatsPerCodi(
					expedientTipusId,
					null,
					true // amb herencia
				);

		TascaDto tasca = definicioProcesService.tascaFindAmbId(expedientTipusId, tascaId);
		return obtenirParellesVariables(expedientTipusId, tasca.getDefinicioProcesId(), variables, tascaId);
	}
	
	private void omplirModelVariables(
			Long expedientTipusId,
			Long tascaId,
			Model model) {
		model.addAttribute("expedientTipusId", expedientTipusId);
		TascaDto tasca = definicioProcesService.tascaFindAmbId(expedientTipusId, tascaId);
		model.addAttribute("tasca", tasca);
		
		this.omplirModelComu(expedientTipusId, tascaId, model);
		
		// Obté el llistat de variables
		List<CampDto> variables = dissenyService.findCampsOrdenatsPerCodi(
					expedientTipusId,
					tasca.getDefinicioProcesId(),
					true // amb herencia
				);
		// Afegeix al model les variables que són heretades o sobreescriuen
		List<Long> campsHeretatsIds = new ArrayList<Long>();
		List<Long> campsSobreescriuenIds = new ArrayList<Long>();
		ExpedientTipusDto expedientTipus = this.expedientTipusService.findAmbId(expedientTipusId);
		if (expedientTipus.getExpedientTipusPareId() != null) {
			for (CampDto v : variables) {
				if (v.isHeretat())
					campsHeretatsIds.add(v.getId());
				if (v.isSobreescriu())
					campsSobreescriuenIds.add(v.getId());
			}
		}
		model.addAttribute("campsHeretatsIds", campsHeretatsIds);
		model.addAttribute("campsSobreescriuenIds", campsSobreescriuenIds);
		// Construeix les parelles de variables
		model.addAttribute("variables", obtenirParellesVariables(
				expedientTipusId,
				tasca.getDefinicioProcesId(),
				variables,
				tascaId));
	}
	
		
	// Manteniment de documents de la tasca
	
	/** Modal per veure els documents de la tasca de tipus filtre. */
	@RequestMapping(value = "/{expedientTipusId}/tasca/{id}/document", method = RequestMethod.GET)
	public String documents(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {

		DefinicioProcesTascaDocumentCommand command = new DefinicioProcesTascaDocumentCommand();
		command.setTascaId(id);
		model.addAttribute("definicioProcesTascaDocumentCommand", command);

		omplirModelDocuments(expedientTipusId, id, model);

		return "v3/definicioProcesTascaDocument";
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/document/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse documentsDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				definicioProcesService.tascaDocumentFindPerDatatable(
						tascaId,
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}		
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{id}/document/new", method = RequestMethod.POST)
	public String documentNouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(DefinicioProcesTascaDocumentCommand.Creacio.class) DefinicioProcesTascaDocumentCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {

    		omplirModelDocuments(expedientTipusId, id, model);

    		return "v3/definicioProcesTascaDocument";
        } else {
        	// Verificar permisos
    		definicioProcesService.tascaDocumentCreate(
    				id,
    				DefinicioProcesTascaDocumentCommand.asDocumentTascaDto(command));    		
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"definicio.proces.tasca.controller.document.creat"));
        	return documents(request, expedientTipusId, id, model);
        }
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/document/{documentTascaId}/{propietat}", method = RequestMethod.POST)
	@ResponseBody
	public boolean documentUpdatePropietat(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			@PathVariable Long documentTascaId,
			@PathVariable String propietat,
			@RequestParam boolean valor) {
		
		DocumentTascaDto documentTasca = definicioProcesService.tascaDocumentFindById(null, documentTascaId);
		if ("required".equals(propietat)) {
			documentTasca.setRequired(valor);
		} else if ("readOnly".equals(propietat)) {
			documentTasca.setReadOnly(valor);
		}
		definicioProcesService.tascaDocumentUpdate(documentTasca);
		
		return true;
	}

	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/document/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean documentDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			@PathVariable Long id,
			Model model) {
				
		definicioProcesService.tascaDocumentDelete(id);
		
		MissatgesHelper.success(
				request,
				getMessage(
						request,
						"definicio.proces.tasca.controller.document.eliminat"));			
		return true;
	}
	
	/**
	 * Mètode Ajax per moure una validació d'una tasca de posició dins la seva agrupació.
	 * @param request
	 * @param definicioProcesId
	 * @param id
	 * @param posicio
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/document/{id}/moure/{posicio}")
	@ResponseBody
	public boolean documentMourePosicio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		boolean ret = false;
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		expedientTipusService.findAmbIdPermisDissenyarDelegat(
				entornActual.getId(), 
				expedientTipusId); 

		DocumentTascaDto estat = definicioProcesService.tascaDocumentFindById(expedientTipusId, id); 
		boolean correcte = true;
		if (estat.isHeretat()) {
			MissatgesHelper.error(
			request, 
			getMessage(
					request, 
					"expedient.tipus.tasca.controller.moure.heretat.error"));
			correcte = false;
		} else {
			// rectifica la posició restant tots els heretats que té per davant
			int nHeretats = 0;
			for(DocumentTascaDto e : definicioProcesService.tascaDocumentFindAll(expedientTipusId, tascaId))
				if (e.isHeretat())
					nHeretats ++;
			if (posicio < nHeretats) {
				MissatgesHelper.error(
				request, 
				getMessage(
						request, 
						"expedient.tipus.tasca.controller.moure.heretat.error"));
				correcte = false;
			}
		}
		if (correcte)
			ret = definicioProcesService.tascaDocumentMourePosicio(id, expedientTipusId, posicio);
		else
			ret = false;
		
		return ret;
	}	
	
	/** Mètode per obtenir les possibles documents per al select a l'edició d'un registre via ajax. */
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/document/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> documentsSelect(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			Model model) {
		// Obté el llistat de documents
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					expedientTipusId,
					null,
					true // amb herencia
				);
		return documentObtenirParellesDocuments(expedientTipusId, documents, tascaId);
	}	
			
	private void omplirModelDocuments(
			Long expedientTipusId,
			Long tascaId,
			Model model) {

		model.addAttribute("expedientTipusId", expedientTipusId);
		TascaDto tasca = definicioProcesService.tascaFindAmbId(expedientTipusId, tascaId);
		model.addAttribute("tasca", tasca);

		this.omplirModelComu(expedientTipusId, tascaId, model);

		// Obté el llistat de documents
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					expedientTipusId,
					tasca.getDefinicioProcesId(),
					true // amb herencia
				);
		// Afegeix al model els documents que són heretades o sobreescriuen
		List<Long> documentsHeretatsIds = new ArrayList<Long>();
		List<Long> documentsSobreescriuenIds = new ArrayList<Long>();
		ExpedientTipusDto expedientTipus = this.expedientTipusService.findAmbId(expedientTipusId);
		if (expedientTipus.getExpedientTipusPareId() != null) {
			for (DocumentDto d : documents) {
				if (d.isHeretat())
					documentsHeretatsIds.add(d.getId());
				if (d.isSobreescriu())
					documentsSobreescriuenIds.add(d.getId());
			}
		}
		model.addAttribute("documentsHeretatsIds", documentsHeretatsIds);
		model.addAttribute("documentsSobreescriuenIds", documentsSobreescriuenIds);
		
		// Construeix les parelles de documents
		model.addAttribute("documents", documentObtenirParellesDocuments(
				expedientTipusId,
				documents,
				tascaId));
	}

	// Manteniment de firmes de la tasca
	
	/** Modal per veure les firmes de la tasca de tipus filtre. */
	@RequestMapping(value = "/{expedientTipusId}/tasca/{id}/firma", method = RequestMethod.GET)
	public String firmes(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {

		DefinicioProcesTascaFirmaCommand command = new DefinicioProcesTascaFirmaCommand();
		command.setTascaId(id);
		model.addAttribute("definicioProcesTascaFirmaCommand", command);

		omplirModelFirmes(expedientTipusId, id, model);

		return "v3/definicioProcesTascaFirma";
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/firma/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse firmesDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				definicioProcesService.tascaFirmaFindPerDatatable(
						tascaId,
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}		
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{id}/firma/new", method = RequestMethod.POST)
	public String firmaNouPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(DefinicioProcesTascaFirmaCommand.Creacio.class) DefinicioProcesTascaFirmaCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {

    		omplirModelFirmes(expedientTipusId, id, model);

    		return "v3/definicioProcesTascaFirma";
        } else {
        	// Verificar permisos
    		definicioProcesService.tascaFirmaCreate(
    				id,
    				DefinicioProcesTascaFirmaCommand.asFirmaTascaDto(command));    		
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"definicio.proces.tasca.controller.firma.creat"));
        	return firmes(request, expedientTipusId, id, model);
        }
	}	
	
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/firma/{firmaTascaId}/{propietat}", method = RequestMethod.POST)
	@ResponseBody
	public boolean firmaUpdatePropietat(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			@PathVariable Long firmaTascaId,
			@PathVariable String propietat,
			@RequestParam boolean valor) {
		
		FirmaTascaDto firmaTasca = definicioProcesService.tascaFirmaFindById(null, firmaTascaId);
		if ("required".equals(propietat)) {
			firmaTasca.setRequired(valor);
			definicioProcesService.tascaFirmaUpdate(firmaTasca);
		} 		
		return true;
	}

	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/firma/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean firmaDelete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			@PathVariable Long id,
			Model model) {
				
		definicioProcesService.tascaFirmaDelete(id);
		
		MissatgesHelper.success(
				request,
				getMessage(
						request,
						"definicio.proces.tasca.controller.firma.eliminat"));			
		return true;
	}
	
	/**
	 * Mètode Ajax per moure una validació d'una tasca de posició dins la seva agrupació.
	 * @param request
	 * @param definicioProcesId
	 * @param id
	 * @param posicio
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/firma/{id}/moure/{posicio}")
	@ResponseBody
	public boolean firmaMourePosicio(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		boolean ret = false;
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		expedientTipusService.findAmbIdPermisDissenyarDelegat(
				entornActual.getId(), 
				expedientTipusId); 

		FirmaTascaDto estat = definicioProcesService.tascaFirmaFindById(expedientTipusId, id); 
		boolean correcte = true;
		if (estat.isHeretat()) {
			MissatgesHelper.error(
			request, 
			getMessage(
					request, 
					"expedient.tipus.tasca.controller.moure.heretat.error"));
			correcte = false;
		} else {
			// rectifica la posició restant tots els heretats que té per davant
			int nHeretats = 0;
			for(FirmaTascaDto e : definicioProcesService.tascaFirmaFindAll(expedientTipusId, tascaId))
				if (e.isHeretat())
					nHeretats ++;
			if (posicio < nHeretats) {
				MissatgesHelper.error(
				request, 
				getMessage(
						request, 
						"expedient.tipus.tasca.controller.moure.heretat.error"));
				correcte = false;
			}
		}
		if (correcte)
			ret = definicioProcesService.tascaFirmaMourePosicio(id, expedientTipusId, posicio);
		else
			ret = false;
		
		return ret;
	}	
	
	/** Mètode per obtenir els possibles firmes per al select a l'edició d'un registre via ajax. */
	@RequestMapping(value = "/{expedientTipusId}/tasca/{tascaId}/firma/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> firmesSelect(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long tascaId,
			Model model) {
		
		// Obté el llistat de documents
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					expedientTipusId,
					null,
					true // amb herencia
				);

		return firmaObtenirParellesDocuments(expedientTipusId, documents, tascaId);
	}	
			
	private void omplirModelFirmes(
			Long expedientTipusId,
			Long tascaId,
			Model model) {

		model.addAttribute("expedientTipusId", expedientTipusId);
		TascaDto tasca = definicioProcesService.tascaFindAmbId(expedientTipusId, tascaId);
		model.addAttribute("tasca", tasca);

		this.omplirModelComu(expedientTipusId, tascaId, model);

		// Obté el llistat de documents
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					expedientTipusId,
					tasca.getDefinicioProcesId(),
					true // amb herencia
				);
		// Afegeix al model els documents que són heretades o sobreescriuen
		List<Long> documentsHeretatsIds = new ArrayList<Long>();
		List<Long> documentsSobreescriuenIds = new ArrayList<Long>();
		ExpedientTipusDto expedientTipus = this.expedientTipusService.findAmbId(expedientTipusId);
		if (expedientTipus.getExpedientTipusPareId() != null) {
			for (DocumentDto d : documents) {
				if (d.isHeretat())
					documentsHeretatsIds.add(d.getId());
				if (d.isSobreescriu())
					documentsSobreescriuenIds.add(d.getId());
			}
		}
		model.addAttribute("documentsHeretatsIds", documentsHeretatsIds);
		model.addAttribute("documentsSobreescriuenIds", documentsSobreescriuenIds);
		
		// Construeix les parelles de documents
		model.addAttribute("documents", firmaObtenirParellesDocuments(
				expedientTipusId,
				documents,
				tascaId));
	}

}
