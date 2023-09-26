/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesIniciFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesSimpleTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.service.PortafirmesFluxService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDocumentCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NtiHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.UrlHelper;

/**
 * Controlador per a la pipella de variables del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusDocumentController extends BaseExpedientTipusController {

	@Autowired
	private NtiHelper ntiHelper;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;



	@RequestMapping(value = "/{expedientTipusId}/documents")
	public String documents(HttpServletRequest request, @PathVariable Long expedientTipusId, Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(request, expedientTipusId, model, "documents");
		}
		// Omple el model per a la pestanya
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyarDelegat(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			model.addAttribute("baseUrl", expedientTipus.getId());
		}
		return "v3/expedientTipusDocument";
	}

	@RequestMapping(value = "/{expedientTipusId}/document/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long expedientTipusId, Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request, 
				null, 
				documentService.findPerDatatable(
						expedientTipusId,
						null,
						paginacioParams.getFiltre(), 
						paginacioParams));
	}

	@RequestMapping(value = "/{expedientTipusId}/document/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		ExpedientTipusDocumentCommand command = new ExpedientTipusDocumentCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusDocumentCommand", command);
		omplirModelComu(request, expedientTipusId, model);
		return "v3/expedientTipusDocumentForm";
	}

	@RequestMapping(value = "/{expedientTipusId}/document/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@RequestParam(value = "arxiuContingut_multipartFile", required = false) final CommonsMultipartFile arxiuContingut,
			@Validated(ExpedientTipusDocumentCommand.Creacio.class) ExpedientTipusDocumentCommand command,
			BindingResult bindingResult, Model model) {
		try {
			if (bindingResult.hasErrors()) {
				omplirModelComu(request, expedientTipusId, model);
				return "v3/expedientTipusDocumentForm";
			} else {
				byte[] contingutArxiu = IOUtils.toByteArray(arxiuContingut.getInputStream());
				DocumentDto dto = ExpedientTipusDocumentCommand.asDocumentDto(command);
				dto.setArxiuContingut(contingutArxiu);
				documentService.create(
						expedientTipusId,
						null,
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

	@RequestMapping(value = "/{expedientTipusId}/document/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			Model model) {
		DocumentDto dto = documentService.findAmbId(expedientTipusId, id);
		ExpedientTipusDocumentCommand command = ConversioTipusHelper.convertir(
				dto,
				ExpedientTipusDocumentCommand.class);
		command.setExpedientTipusId(expedientTipusId);
		command.setCampId(dto.getCampData() != null ? dto.getCampData().getId() : null);
		omplirModelComu(request, expedientTipusId, model);
		model.addAttribute("heretat", dto.isHeretat());
		model.addAttribute("portafirmesFluxSeleccionat", dto.getPortafirmesFluxId());
		model.addAttribute("expedientTipusDocumentCommand", command);
		return "v3/expedientTipusDocumentForm";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/document/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			@RequestParam(value = "arxiuContingut_multipartFile", required = false) final MultipartFile arxiuContingut,
			@RequestParam(value = "arxiuContingut_deleted", required = false) final boolean eliminarContingut,
			@Validated(ExpedientTipusDocumentCommand.Modificacio.class) ExpedientTipusDocumentCommand command,
			BindingResult bindingResult, Model model) {
		try {
			if (bindingResult.hasErrors()) {
				omplirModelComu(request, expedientTipusId, model);
				model.addAttribute("heretat", documentService.findAmbId(expedientTipusId, id).isHeretat());
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
			model.addAttribute("heretat", documentService.findAmbId(expedientTipusId, id).isHeretat());
			return "v3/expedientTipusDocumentForm";
	    }
	}

	@RequestMapping(value = "/{expedientTipusId}/document/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			Model model) {
		try {
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			expedientTipusService.findAmbIdPermisDissenyarDelegat(
						entornActual.getId(),
						expedientTipusId);
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
			logger.error("S'ha produït un error al intentar eliminar el document amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			return false;
		}
	}
	
	@RequestMapping(value="/{expedientTipusId}/document/{id}/download", method = RequestMethod.GET)
	public String documentDesacarregar(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			@PathVariable Long id,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		expedientTipusService.findAmbIdPermisDissenyarDelegat(
					entornActual.getId(),
					expedientTipusId);
		ArxiuDto arxiu = documentService.getArxiu(id);
		if (arxiu != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
		}
		return "arxiuView";
	}

	@RequestMapping(value = "/{expedientTipusId}/document/flux/plantilles", method = RequestMethod.GET)
	@ResponseBody
	public List<PortafirmesFluxRespostaDto> portasigPlantillesDisponibles(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId, 
			Model model) {		
		List<PortafirmesFluxRespostaDto> resposta = portafirmesFluxService.recuperarPlantillesDisponibles(expedientTipusId, null);
		return resposta;
	}


	private void omplirModelComu(
			HttpServletRequest request,
			Long expedientTipusId,
			Model model) {
		List<CampDto> camps = campService.findTipusData(expedientTipusId, null);
						
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (CampDto camp: camps) {
			resposta.add(new ParellaCodiValorDto(camp.getId().toString(), (camp.getCodi() + "/" + camp.getEtiqueta())));
		}
		model.addAttribute("camps", resposta);
		ntiHelper.omplirOrigen(model);
		ntiHelper.omplirEstadoElaboracion(model);
		ntiHelper.omplirTipoDocumental(model);

		model.addAttribute(
				"fluxtipEnumOptions",
				EnumHelper.getOptionsForEnum(
						PortafirmesTipusEnumDto.class
						,"enum.document.tipus.portafirmes."));
		model.addAttribute(
				"portafirmesSequenciaTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						PortafirmesSimpleTipusEnumDto.class
						,"enum.document.tipus.portafirmes.sequencia."));
	}
	
	/** Mètode Ajax per iniciar l'edició d'un flux de firma i retornar la URL a carregar per a poder-lo editar des d'Helium.
	 * 
	 * @param request
	 * @param plantillaId
	 * @param expedientTipusId
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{expedientTipusId}/document/iniciarTransaccio", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto portasigIniciarTransaccio(
			HttpServletRequest request,
			@RequestParam(value = "plantillaId", required = false) String plantillaId,
			@PathVariable Long expedientTipusId, 
			Model model) throws UnsupportedEncodingException {
		String urlReturn;
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;
		try {
			urlReturn = UrlHelper.getAbsoluteControllerBase(
							request,
							(ModalHelper.isModal(request) ? "/modal" : "") + "/v3/expedientTipus/" +expedientTipusId+ "/document/flux/returnurl/");
			if (plantillaId != null && !plantillaId.isEmpty()) {
				transaccioResponse = new PortafirmesIniciFluxRespostaDto();
				String urlEdicio = portafirmesFluxService.recuperarUrlEdicioPlantilla(plantillaId, urlReturn);
				transaccioResponse.setUrlRedireccio(urlEdicio);
			} else {
				transaccioResponse = portafirmesFluxService.iniciarFluxFirma(expedientTipusId, null, urlReturn, true);
			}
		} catch (Exception ex) {
			logger.error("Error al iniciar transacio", ex);
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}
		return transaccioResponse;
	}


	
	@RequestMapping(value = "/{expedientTipusId}/document/flux/esborrar/{plantillaId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean portasigEsborrarPlantilla(
			HttpServletRequest request,
			@PathVariable String plantillaId,
			Model model) {
		
		return portafirmesFluxService.esborrarPlantilla(plantillaId);
	}
	
	
	@RequestMapping(value = "/{expedientTipusId}/document/tancarTransaccio/{idTransaccio}", method = RequestMethod.GET)
	@ResponseBody
	public void portasigTancarTransaccio(
			HttpServletRequest request, 
			@PathVariable String idTransaccio, 
			Model model) {
		portafirmesFluxService.tancarTransaccio(idTransaccio);
	}
	
	@RequestMapping(value = "/{expedientTipusId}/document/flux/returnurl/{transactionId}", method = RequestMethod.GET)
	public String portasigTransaccioEstat(
			HttpServletRequest request, 
			@PathVariable String transactionId, 
			Model model) {
		PortafirmesFluxRespostaDto resposta = portafirmesFluxService.recuperarFluxFirma(transactionId);

		if (resposta.isError() && resposta.getEstat() != null) {
			model.addAttribute(
					"FluxError",
					getMessage(request, "expedient.tipus.document.form.camp.portafirmes.flux.enum." + resposta.getEstat()));
		} else {
			model.addAttribute(
					"FluxCreat",
					getMessage(request, "expedient.tipus.document.form.camp.portafirmes.flux.enum.FINAL_OK"));
			model.addAttribute("fluxId", resposta.getFluxId());
			model.addAttribute("FluxNom", resposta.getNom());
		}
		return "v3/portafirmesModalTancar";
	}

	@RequestMapping(value = "/{expedientTipusId}/document/flux/returnurl/", method = RequestMethod.GET)
	public String portasigTransaccioEstat(HttpServletRequest request, Model model) {
		model.addAttribute(
				"FluxCreat",
				getMessage(request, "expedient.tipus.document.form.camp.portafirmes.flux.edicio.enum.FINAL_OK"));
		return "v3/portafirmesModalTancar";
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusDocumentController.class);
}
