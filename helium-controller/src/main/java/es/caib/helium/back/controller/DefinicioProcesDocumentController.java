/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusDocumentCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.NodecoHelper;
import es.caib.helium.back.helper.NtiHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.back.view.ArxiuView;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.ParellaCodiValorDto;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per a la pipella de variables del disseny de les definicions de
 * procés.
 * 
 */
@Controller
@RequestMapping("/v3/definicioProces")
public class DefinicioProcesDocumentController extends BaseDefinicioProcesController {
	
	@Autowired
	private DefinicioProcesService definicioProcesService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private NtiHelper ntiHelper;

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/documents")
	public String documents(
			HttpServletRequest request, 
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioDefinicioProcesPerPipelles(request, jbmpKey, definicioProcesId, model, "documents");
		}
		// Omple el model per a la pestanya
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdPermisDissenyarDelegat(entornActual.getId(),
					definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			model.addAttribute("baseUrl", ("/helium/v3/definicioProces/" + definicioProces.getJbpmKey() + "/" + definicioProces.getId().toString()));
		}
		model.addAttribute("jbpmKey", jbmpKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		
		return "v3/expedientTipusDocument";
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/document/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request, 
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request, 
				null, 
				documentService.findPerDatatable(
							null,
							definicioProcesId, 
							paginacioParams.getFiltre(), 
							paginacioParams));
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/document/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request, 
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			Model model) {
		ExpedientTipusDocumentCommand command = new ExpedientTipusDocumentCommand();
		command.setDefinicioProcesId(definicioProcesId);
		model.addAttribute("expedientTipusDocumentCommand", command);
		omplirModelComu(request, definicioProcesId, model);
		return "v3/expedientTipusDocumentForm";
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/document/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request, 
			@PathVariable String jbmpKey, @PathVariable Long definicioProcesId,
			@RequestParam(value = "arxiuContingut_multipartFile", required = false) final CommonsMultipartFile arxiuContingut,
			@Validated(ExpedientTipusDocumentCommand.Creacio.class) ExpedientTipusDocumentCommand command,
			BindingResult bindingResult, Model model) {
		try {
			if (bindingResult.hasErrors()) {
				omplirModelComu(request, definicioProcesId, model);
				return "v3/expedientTipusDocumentForm";
			} else {
				byte[] contingutArxiu = IOUtils.toByteArray(arxiuContingut.getInputStream());
				DocumentDto dto = ExpedientTipusDocumentCommand.asDocumentDto(command);
				dto.setArxiuContingut(contingutArxiu);
				documentService.create(
						null,
						definicioProcesId,
						dto);
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.document.controller.creat"));
				return modalUrlTancar(false);
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document", ex);
			return "v3/expedientTipusDocumentForm";
	    }
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/document/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@PathVariable Long id,
			Model model) {
		DocumentDto dto = documentService.findAmbId(null, id);
		ExpedientTipusDocumentCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusDocumentCommand.class);
		command.setDefinicioProcesId(definicioProcesId);
		command.setCampId(dto.getCampData() != null ? dto.getCampData().getId() : null);
		model.addAttribute("expedientTipusDocumentCommand", command);
		omplirModelComu(request, definicioProcesId, model);
		return "v3/expedientTipusDocumentForm";
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/document/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request, 
			@PathVariable String jbmpKey, @PathVariable Long definicioProcesId, 
			@PathVariable Long id,
			@RequestParam(value = "arxiuContingut_multipartFile", required = false) final CommonsMultipartFile arxiuContingut,
			@RequestParam(value = "arxiuContingut_deleted", required = false) final boolean eliminarContingut,
			@Validated(ExpedientTipusDocumentCommand.Modificacio.class) ExpedientTipusDocumentCommand command,
			BindingResult bindingResult, Model model) {
		try {
			if (bindingResult.hasErrors()) {
				omplirModelComu(request, definicioProcesId, model);
				return "v3/expedientTipusDocumentForm";
			} else {
	        	boolean actualitzarContingut = false;
	        	if (eliminarContingut) {
	        		command.setArxiuContingut(null);
	        		actualitzarContingut = true;
	        	}
	        	if (arxiuContingut != null && arxiuContingut.getSize() > 0) {
					command.setArxiuContingut(IOUtils.toByteArray(arxiuContingut.getInputStream()));
					actualitzarContingut = true;
				}
				documentService.update(
						ExpedientTipusDocumentCommand.asDocumentDto(command),
						actualitzarContingut);
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.document.controller.modificat"));
				return modalUrlTancar(false);
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document: " + id, ex);
			return "v3/expedientTipusDocumentForm";
	    }
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/document/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request, 
			@PathVariable String jbmpKey, @PathVariable Long definicioProcesId, 
			@PathVariable Long id,
			Model model) {
		try {
			documentService.delete(id);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.document.controller.eliminat"));
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.document.llistat.accio.esborrar.error"));
			logger.error("S'ha produït un error al intentar eliminar el document amb id '" + id + "' de la definicio de proces amb id '" + definicioProcesId, e);
			return false;
		}
	}
	
	@RequestMapping(value="/{jbmpKey}/{definicioProcesId}/document/{id}/download", method = RequestMethod.GET)
	public String documentDownload(
			HttpServletRequest request, 
			@PathVariable String jbmpKey, 
			@PathVariable Long definicioProcesId, 
			@PathVariable Long id,
			Model model) {
		ArxiuDto arxiu = documentService.getArxiu(id);
		if (arxiu != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
		}
		return "arxiuView";
	}
	
	private void omplirModelComu(
			HttpServletRequest request,
			Long definicioProcesId,
			Model model) {
		List<CampDto> camps = campService.findTipusData(null, definicioProcesId);
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (CampDto camp: camps) {
			resposta.add(new ParellaCodiValorDto(camp.getId().toString(), (camp.getCodi() + "/" + camp.getEtiqueta())));
		}
		model.addAttribute("camps", resposta);
		ntiHelper.omplirOrigen(model);
		ntiHelper.omplirEstadoElaboracion(model);
		ntiHelper.omplirTipoDocumental(model);
	}

	private static final Log logger = LogFactory.getLog(DefinicioProcesDocumentController.class);

}
