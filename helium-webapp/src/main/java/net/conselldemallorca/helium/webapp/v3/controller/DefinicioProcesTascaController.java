package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
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
 * Controlador per a la pestanya de tasques del disseny de les definicions de procés.
 *
 */
@Controller(value = "definicioProcesTascaControllerV3")
@RequestMapping("/v3/definicioProces")
public class DefinicioProcesTascaController extends BaseDefinicioProcesController {
	
	@Autowired
	ExpedientTipusService expedientTipusService;
	@Autowired
	DissenyService dissenyService;
	@Autowired
	DocumentService documentService;
	
	/** Pipella del tasques. */
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca")
	public String tasques(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioDefinicioProcesPerPipelles(
					request,
					jbpmKey,
					model,
					"tasques");
		}
		model.addAttribute("jbpmKey", jbpmKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
				
		return "v3/definicioProcesTasques";
	}	
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/datatable")
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				definicioProcesService.tascaFindPerDatatable(
						entornActual.getId(),
						definicioProcesId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}	
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			Model model) {
		TascaDto dto = definicioProcesService.tascaFindAmbId(tascaId);
		DefinicioProcesTascaCommand command = DefinicioProcesTascaCommand.toDefinicioProcesTascaCommand(dto);	
		command.setDefinicioProcesId(definicioProcesId);
		command.setJbpmName(dto.getJbpmName());
		model.addAttribute("definicioProcesTascaCommand", command);
		return "v3/definicioProcesTascaForm";
	}
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
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
	
	
	// Manteniment de variables de la tasca
	
	/** Modal per veure els camps de la tasca de tipus filtre. */
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{id}/variable", method = RequestMethod.GET)
	public String variables(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			Model model) {

		DefinicioProcesTascaVariableCommand command = new DefinicioProcesTascaVariableCommand();
		command.setTascaId(id);
		command.setReadFrom(true);
		command.setWriteTo(true);
		command.setAmpleCols(12);
		command.setBuitCols(0);
		model.addAttribute("definicioProcesTascaVariableCommand", command);

		omplirModelVariables(jbpmKey, definicioProcesId, id, model);

		return "v3/definicioProcesTascaVariable";
	}	
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/variable/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse variablesDatatable(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				definicioProcesService.tascaCampFindPerDatatable(
						tascaId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}		
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{id}/variable/new", method = RequestMethod.POST)
	public String variableNouPost(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			@Validated(DefinicioProcesTascaVariableCommand.Creacio.class) DefinicioProcesTascaVariableCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {


    		omplirModelVariables(jbpmKey, definicioProcesId, id, model);
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
        	return variables(request, jbpmKey, definicioProcesId, id, model);
        }
	}	
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/variable/{campTascaId}/{propietat}", method = RequestMethod.POST)
	@ResponseBody
	public boolean variableUpdatePropietat(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			@PathVariable Long campTascaId,
			@PathVariable String propietat,
			@RequestParam Object valor) {
		
		CampTascaDto campTasca = definicioProcesService.tascaCampFindById(campTascaId);
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

	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/variable/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean variableDelete(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
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
	 * Mètode Ajax per moure una validació d'una tasca de posició dins la seva agrupació.
	 * @param request
	 * @param definicioProcesId
	 * @param id
	 * @param posicio
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/variable/{id}/moure/{posicio}")
	@ResponseBody
	public boolean variableMourePosicio(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return definicioProcesService.tascaCampMourePosicio(id, posicio);
	}	
	
	/** Mètode per obtenir les possibles variables per al select a l'edició d'un registre via ajax. */
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/variable/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> variablesSelect(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			Model model) {

		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);
		List<CampDto> variables = dissenyService.findCampsOrdenatsPerCodi(
					definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
					definicioProcesId,
					true // amb herencia
				);

		return obtenirParellesVariables(definicioProcesId, variables, tascaId);
	}	
		
	
	private void omplirModelVariables(
			String jbpmKey,
			Long definicioProcesId,
			Long tascaId,
			Model model) {
		model.addAttribute("jbpmKey", jbpmKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		model.addAttribute("tasca", definicioProcesService.tascaFindAmbId(tascaId));
		
		// Obté el llistat de variables
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);

		List<CampDto> variables = dissenyService.findCampsOrdenatsPerCodi(
					definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
					definicioProcesId,
					true // amb herencia
				);
		// Afegeix al model les variables que són heretades o sobreescriuen
		List<Long> campsHeretatsIds = new ArrayList<Long>();
		List<Long> campsSobreescriuenIds = new ArrayList<Long>();
		if (definicioProces.getExpedientTipus().getExpedientTipusPareId() != null) {
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
				definicioProcesId,
				variables,
				tascaId));
	}
	
	/**
	 * Retorna les parelles codi i valor per a les possibles variables per als camps de les consultes.
	 * Lleva les variables que s'han utilitzat ja en la tasca.
	 * @param definicioProcesId
	 * @param tascaId
	 * @return
	 */
	private List<ParellaCodiValorDto> obtenirParellesVariables(
			Long definicioProcesId,
			List<CampDto> variables,
			Long tascaId) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		// Tasca els camps de la tasca segons el tipus
		List<CampTascaDto> camps = definicioProcesService.tascaCampFindCampAmbTascaId(tascaId);
		// Lleva les variables que ja pertanyin a algun camp
		Iterator<CampDto> it = variables.iterator();
		while (it.hasNext()) {
			CampDto variable = it.next();
			for (CampTascaDto camp : camps) {
				if (variable.getId().equals(camp.getCamp().getId())) {
					it.remove();
					break;
				}
			}
		}
		// Si és la tasca inicial treu les variables de tipus acció
		String startTaskName = definicioProcesService.consultarStartTaskName(definicioProcesId);
		TascaDto tasca = definicioProcesService.tascaFindAmbId(tascaId);
		if (startTaskName != null && tasca.getNom().equals(startTaskName)) {
			it = variables.iterator();
			CampDto camp;
			while (it.hasNext()) {
				camp = it.next();
				if (camp.getTipus().equals(CampTipusDto.ACCIO))
					it.remove();
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
	
	// Manteniment de documents de la tasca
	
	/** Modal per veure els documents de la tasca de tipus filtre. */
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{id}/document", method = RequestMethod.GET)
	public String documents(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			Model model) {

		DefinicioProcesTascaDocumentCommand command = new DefinicioProcesTascaDocumentCommand();
		command.setTascaId(id);
		model.addAttribute("definicioProcesTascaDocumentCommand", command);

		omplirModelDocuments(jbpmKey, definicioProcesId, id, model);

		return "v3/definicioProcesTascaDocument";
	}	
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/document/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse documentsDatatable(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				definicioProcesService.tascaDocumentFindPerDatatable(
						tascaId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}		
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{id}/document/new", method = RequestMethod.POST)
	public String documentNouPost(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			@Validated(DefinicioProcesTascaDocumentCommand.Creacio.class) DefinicioProcesTascaDocumentCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {

    		omplirModelDocuments(jbpmKey, definicioProcesId, id, model);

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
        	return documents(request, jbpmKey, definicioProcesId, id, model);
        }
	}	
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/document/{documentTascaId}/{propietat}", method = RequestMethod.POST)
	@ResponseBody
	public boolean documentUpdatePropietat(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			@PathVariable Long documentTascaId,
			@PathVariable String propietat,
			@RequestParam boolean valor) {
		
		DocumentTascaDto documentTasca = definicioProcesService.tascaDocumentFindById(documentTascaId);
		if ("required".equals(propietat)) {
			documentTasca.setRequired(valor);
		} else if ("readOnly".equals(propietat)) {
			documentTasca.setReadOnly(valor);
		}
		definicioProcesService.tascaDocumentUpdate(documentTasca);
		
		return true;
	}

	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/document/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean documentDelete(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
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
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/document/{id}/moure/{posicio}")
	@ResponseBody
	public boolean documentMourePosicio(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return definicioProcesService.tascaDocumentMourePosicio(id, posicio);
	}	
	
	/** Mètode per obtenir les possibles documents per al select a l'edició d'un registre via ajax. */
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/document/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> documentsSelect(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			Model model) {
		// Obté el llistat de documents
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
					definicioProcesId
					,true // amb herencia
				);
		return documentObtenirParellesDocuments(definicioProcesId, documents, tascaId);
	}	
			
	private void omplirModelDocuments(
			String jbpmKey,
			Long definicioProcesId,
			Long tascaId,
			Model model) {

		model.addAttribute("jbpmKey", jbpmKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		model.addAttribute("tasca", definicioProcesService.tascaFindAmbId(tascaId));

		// Obté el llistat de documents
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);

		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
					definicioProcesId
					,true // amb herencia
				);
		// Afegeix al model els documents que són heretades o sobreescriuen
		List<Long> documentsHeretatsIds = new ArrayList<Long>();
		List<Long> documentsSobreescriuenIds = new ArrayList<Long>();
		if (definicioProces.getExpedientTipus().getExpedientTipusPareId() != null) {
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
				definicioProcesId,
				documents,
				tascaId));
	}
	
	/**
	 * Retorna les parelles codi i valor per a les possibles documents per als documents de les consultes.
	 * Lleva les documents que s'han utilitzat ja en la tasca.
	 * @param definicioProcesId
	 * @param documents 
	 * @param tascaId
	 * @return
	 */
	private List<ParellaCodiValorDto> documentObtenirParellesDocuments(
			Long definicioProcesId,
			List<DocumentDto> documents, 
			Long tascaId) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		// Documents de la tasca existents
		List<DocumentTascaDto> documentsTasca = definicioProcesService.tascaDocumentFindDocumentAmbTascaId(tascaId);
		// Lleva les documents que ja pertanyin a algun document
		Iterator<DocumentDto> it = documents.iterator();
		while (it.hasNext()) {
			DocumentDto document = it.next();
			for (DocumentTascaDto documentTasca : documentsTasca) {
				if (document.getId().equals(documentTasca.getDocument().getId())) {
					it.remove();
					break;
				}
			}
		}
		// Crea les parelles de codi i valor
		for (DocumentDto document : documents) {
			resposta.add(new ParellaCodiValorDto(
					document.getId().toString(), 
					document.getCodi() + " / " + document.getNom()));
		}			
		return resposta;
	}	

	// Manteniment de firmes de la tasca
	
	/** Modal per veure les firmes de la tasca de tipus filtre. */
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{id}/firma", method = RequestMethod.GET)
	public String firmes(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			Model model) {

		DefinicioProcesTascaFirmaCommand command = new DefinicioProcesTascaFirmaCommand();
		command.setTascaId(id);
		model.addAttribute("definicioProcesTascaFirmaCommand", command);

		omplirModelFirmes(jbpmKey, definicioProcesId, id, model);

		return "v3/definicioProcesTascaFirma";
	}	
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/firma/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse firmesDatatable(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				definicioProcesService.tascaFirmaFindPerDatatable(
						tascaId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}		
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{id}/firma/new", method = RequestMethod.POST)
	public String firmaNouPost(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long id,
			@Validated(DefinicioProcesTascaFirmaCommand.Creacio.class) DefinicioProcesTascaFirmaCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {

    		omplirModelFirmes(jbpmKey, definicioProcesId, id, model);

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
        	return firmes(request, jbpmKey, definicioProcesId, id, model);
        }
	}	
	
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/firma/{firmaTascaId}/{propietat}", method = RequestMethod.POST)
	@ResponseBody
	public boolean firmaUpdatePropietat(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			@PathVariable Long firmaTascaId,
			@PathVariable String propietat,
			@RequestParam boolean valor) {
		
		FirmaTascaDto firmaTasca = definicioProcesService.tascaFirmaFindById(firmaTascaId);
		if ("required".equals(propietat)) {
			firmaTasca.setRequired(valor);
			definicioProcesService.tascaFirmaUpdate(firmaTasca);
		} 		
		return true;
	}

	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/firma/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean firmaDelete(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
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
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/firma/{id}/moure/{posicio}")
	@ResponseBody
	public boolean firmaMourePosicio(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			@PathVariable Long id,
			@PathVariable int posicio) {
		
		return definicioProcesService.tascaFirmaMourePosicio(id, posicio);
	}	
	
	/** Mètode per obtenir els possibles firmes per al select a l'edició d'un registre via ajax. */
	@RequestMapping(value = "/{jbpmKey}/{definicioProcesId}/tasca/{tascaId}/firma/select", method = RequestMethod.GET)
	@ResponseBody
	public List<ParellaCodiValorDto> firmesSelect(
			HttpServletRequest request,
			@PathVariable String jbpmKey,
			@PathVariable Long definicioProcesId,
			@PathVariable Long tascaId,
			Model model) {
		
		// Obté el llistat de documents
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);

		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
					definicioProcesId
					,true // amb herencia
				);

		return firmaObtenirParellesDocuments(definicioProcesId, documents, tascaId);
	}	
			
	private void omplirModelFirmes(
			String jbpmKey,
			Long definicioProcesId,
			Long tascaId,
			Model model) {

		model.addAttribute("jbpmKey", jbpmKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		model.addAttribute("tasca", definicioProcesService.tascaFindAmbId(tascaId));

		// Obté el llistat de documents
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);

		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
					definicioProcesId
					,true // amb herencia
				);
		// Afegeix al model els documents que són heretades o sobreescriuen
		List<Long> documentsHeretatsIds = new ArrayList<Long>();
		List<Long> documentsSobreescriuenIds = new ArrayList<Long>();
		if (definicioProces.getExpedientTipus().getExpedientTipusPareId() != null) {
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
				definicioProcesId,
				documents,
				tascaId));
	}

	/**
	 * Retorna les parelles codi i valor per als possibles docuemnts per a les firmes de les tasques.
	 * Lleva els documents que s'han utilitzat ja en la tasca.
	 * @param definicioProcesId
	 * @param tascaId
	 * @return
	 */
	private List<ParellaCodiValorDto> firmaObtenirParellesDocuments(
			Long definicioProcesId,
			List<DocumentDto> documents,
			Long tascaId) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		// Documents de la tasca existents
		List<FirmaTascaDto> firmesTasca = definicioProcesService.tascaFirmaFindAmbTascaId(tascaId);
		// Lleva les firmes que ja pertanyin a algun firma
		Iterator<DocumentDto> it = documents.iterator();
		while (it.hasNext()) {
			DocumentDto firma = it.next();
			for (FirmaTascaDto firmaTasca : firmesTasca) {
				if (firma.getId().equals(firmaTasca.getDocument().getId())) {
					it.remove();
					break;
				}
			}
		}
		// Crea les parelles de codi i valor
		for (DocumentDto firma : documents) {
			resposta.add(new ParellaCodiValorDto(
					firma.getId().toString(), 
					firma.getCodi() + " / " + firma.getNom()));
		}			
		return resposta;
	}	
	
}
