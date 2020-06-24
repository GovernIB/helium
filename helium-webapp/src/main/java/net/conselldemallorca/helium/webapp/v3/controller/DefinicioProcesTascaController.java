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

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
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
public class DefinicioProcesTascaController extends BaseTascaDissenyController {
	
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
					definicioProcesId,
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
						null,
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
		TascaDto dto = definicioProcesService.tascaFindAmbId(null, tascaId);
		DefinicioProcesTascaCommand command = DefinicioProcesTascaCommand.toDefinicioProcesTascaCommand(dto);	
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
	
	
	/** Omple dades comuns per totes les pàgines
	 * 
	 */
	private void omplirModelComu(
			String jbpmKey,
			Long definicioProcesId,
			Long tascaId,
			Model model) {
		
		// Especifica les URLs per la pàgina
		String basicUrl = "definicioProces/" + jbpmKey + "/" + definicioProcesId.toString() + "/tasca/" + tascaId;
		model.addAttribute("basicUrl", basicUrl);
		model.addAttribute("baseUrl", "/helium/v3/" + basicUrl);

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
						null,
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
		
		CampTascaDto campTasca = definicioProcesService.tascaCampFindById(null, campTascaId);
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
		
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);
		
		return definicioProcesService.tascaCampMourePosicio(id, 
															definicioProces.getExpedientTipus() != null ? 
																	definicioProces.getExpedientTipus().getId() // Pertany a un tipus d'expedient 
																	: null, // Global 
															posicio);
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
		Long expedientTipusId = definicioProces.getExpedientTipus() != null? definicioProces.getExpedientTipus().getId() : null;
		List<CampDto> variables = dissenyService.findCampsOrdenatsPerCodi(
				expedientTipusId,
					definicioProcesId,
					true // amb herencia
				);

		return obtenirParellesVariables(expedientTipusId, definicioProcesId, variables, tascaId);
	}	
			
	/** Omple el model de dades per la pàgina de camps de les tasques. */
	private void omplirModelVariables(
			String jbpmKey,
			Long definicioProcesId,
			Long tascaId,
			Model model) {
		model.addAttribute("jbpmKey", jbpmKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		model.addAttribute("tasca", definicioProcesService.tascaFindAmbId(null, tascaId));
		
		this.omplirModelComu(jbpmKey, definicioProcesId, tascaId, model);

		// Obté el llistat de variables
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);
		Long expedientTipusId = definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null;
		List<CampDto> variables = dissenyService.findCampsOrdenatsPerCodi(
					expedientTipusId,
					definicioProcesId,
					true // amb herencia
				);
		// Afegeix al model les variables que són heretades o sobreescriuen
		List<Long> campsHeretatsIds = new ArrayList<Long>();
		List<Long> campsSobreescriuenIds = new ArrayList<Long>();
		if (definicioProces.getExpedientTipus() != null 
				&& definicioProces.getExpedientTipus().getExpedientTipusPareId() != null) {
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
				definicioProcesId,
				variables,
				tascaId));
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
						null,
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
		
		DocumentTascaDto documentTasca = definicioProcesService.tascaDocumentFindById(null, documentTascaId);
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
		
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);

		return definicioProcesService.tascaDocumentMourePosicio(
				id, 
				definicioProces.getExpedientTipus() != null ? 
						definicioProces.getExpedientTipus().getId() // Pertany a un tipus d'expedient 
						: null, // Global 
				posicio);
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
		Long expedientTipusId = definicioProces.getExpedientTipus() != null? definicioProces.getExpedientTipus().getId() : null;
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
					definicioProcesId
					,true // amb herencia
				);
		return documentObtenirParellesDocuments(expedientTipusId, documents, tascaId);
	}	
			
	private void omplirModelDocuments(
			String jbpmKey,
			Long definicioProcesId,
			Long tascaId,
			Model model) {

		model.addAttribute("jbpmKey", jbpmKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		model.addAttribute("tasca", definicioProcesService.tascaFindAmbId(null, tascaId));

		this.omplirModelComu(jbpmKey, definicioProcesId, tascaId, model);

		// Obté el llistat de documents
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);
		Long expedientTipusId = definicioProces.getExpedientTipus() != null? definicioProces.getExpedientTipus().getId() : null;

		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
					definicioProcesId
					,true // amb herencia
				);
		// Afegeix al model els documents que són heretades o sobreescriuen
		List<Long> documentsHeretatsIds = new ArrayList<Long>();
		List<Long> documentsSobreescriuenIds = new ArrayList<Long>();
		if (definicioProces.getExpedientTipus() != null 
				&& definicioProces.getExpedientTipus().getExpedientTipusPareId() != null) {
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
						null,
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
		
		FirmaTascaDto firmaTasca = definicioProcesService.tascaFirmaFindById(null, firmaTascaId);
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
		
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);
		
		return definicioProcesService.tascaFirmaMourePosicio(id, 
															definicioProces.getExpedientTipus() != null ? 
																	definicioProces.getExpedientTipus().getId() // Pertany a un tipus d'expedient 
																	: null, // Global 
															posicio);
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
		Long expedientTipusId = definicioProces.getExpedientTipus() != null? definicioProces.getExpedientTipus().getId() : null;

		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
					definicioProcesId
					,true // amb herencia
				);

		return firmaObtenirParellesDocuments(expedientTipusId, documents, tascaId);
	}	
			
	private void omplirModelFirmes(
			String jbpmKey,
			Long definicioProcesId,
			Long tascaId,
			Model model) {

		model.addAttribute("jbpmKey", jbpmKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		model.addAttribute("tasca", definicioProcesService.tascaFindAmbId(null, tascaId));

		this.omplirModelComu(jbpmKey, definicioProcesId, tascaId, model);

		// Obté el llistat de documents
		DefinicioProcesDto definicioProces = definicioProcesService.findById(definicioProcesId);
		Long expedientTipusId = definicioProces.getExpedientTipus() != null? definicioProces.getExpedientTipus().getId() : null;

		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
					definicioProces.getExpedientTipus() != null ? definicioProces.getExpedientTipus().getId() : null,
					definicioProcesId
					,true // amb herencia
				);
		// Afegeix al model els documents que són heretades o sobreescriuen
		List<Long> documentsHeretatsIds = new ArrayList<Long>();
		List<Long> documentsSobreescriuenIds = new ArrayList<Long>();
		if (definicioProces.getExpedientTipus() != null 
				&& definicioProces.getExpedientTipus().getExpedientTipusPareId() != null) {
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
