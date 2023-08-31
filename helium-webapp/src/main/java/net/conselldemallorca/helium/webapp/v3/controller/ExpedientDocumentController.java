package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.StatusSignature;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalViaTipus;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTipusFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.IdiomaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxBlocDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesIniciFluxRespostaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesPrioritatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesSimpleTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ServeiTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PortafirmesFluxService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Create;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Update;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientEnviarPortasignaturesCommand;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientEnviarPortasignaturesCommand.EnviarPortasignatures;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientFirmaPassarelaCommand;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientFirmaPassarelaCommand.FirmaPassarela;
import net.conselldemallorca.helium.webapp.v3.command.DocumentNotificacioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NtiHelper;
import net.conselldemallorca.helium.webapp.v3.passarelafirma.PassarelaFirmaConfig;
import net.conselldemallorca.helium.webapp.v3.passarelafirma.PassarelaFirmaHelper;

/**
 * Controlador per a la pàgina de documents de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientDocumentController extends BaseExpedientController {

	@Autowired
	private ExpedientDocumentService expedientDocumentService;
	// TODO: eliminar la referencia al core 2.6 i passar el mètode processarDocumentPendentPortasignatures al pluginHelper
	@Autowired
	private PluginService pluginService;
	@Autowired
	private NtiHelper ntiHelper;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private AplicacioService aplicacioService;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Autowired
	private PassarelaFirmaHelper passarelaFirmaHelper;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	protected DocumentService documentService;

	/** comparador per ordenar documents per codi de document primer i per títol de l'adjunt si són adjunts després.
	 *
	 */
	protected class  ExpedientDocumentDtoComparador implements Comparator<ExpedientDocumentDto>{
		
		/** Ordre per defecte, data o títol. */
		private String sort;

		public ExpedientDocumentDtoComparador(String sort) {
			this.sort = sort;
		}

		/** Compara primer per codi de document i si són annexs i no en tenen llavors per títol de l'annex. */
		@Override
		public int compare(ExpedientDocumentDto doc1, ExpedientDocumentDto doc2) {
			int ret;
			if ("data".equals(this.sort)) {
				// Ordena per data
				ret = doc1.getDataDocument().compareTo(doc2.getDataDocument());
			} else if ("titol".equals(this.sort)) {
				// Ordena per títol
				String titol1 = doc1.isAdjunt() ? doc1.getAdjuntTitol() : doc1.getDocumentNom();
				String titol2 = doc2.isAdjunt() ? doc2.getAdjuntTitol() : doc2.getDocumentNom();
				ret = titol1.toLowerCase().compareTo(titol2.toLowerCase());
			} else {
				// Compara per codi de document primer
				if (doc1.getDocumentCodi() != null && doc2.getDocumentCodi() != null)
					ret = doc1.getDocumentCodi().compareTo(doc2.getDocumentCodi());
				else if (doc1.getDocumentCodi() != null)
					ret = -1;
				else if (doc2.getDocumentCodi() != null)
					ret = 1;
				else 
					// els annexos van darrera ordenats per adjuntTitol contemplant que pugui ser null
					ret = (doc1.getAdjuntTitol() == null ? 
							(doc2.getAdjuntTitol() == null ? 0 : 1) 
							: (doc2.getAdjuntTitol() == null ? 
									-1 
									: doc1.getAdjuntTitol().compareTo(doc2.getAdjuntTitol())));
			}
			return ret;
		}
	}

	@RequestMapping(value = "/{expedientId}/document", method = RequestMethod.GET)
	public String document(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(defaultValue = "default") String sort,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documents = new LinkedHashMap<InstanciaProcesDto, List<ExpedientDocumentDto>>();
		List<PortasignaturesDto> portasignaturesPendent = expedientDocumentService.portasignaturesFindPendents(
				expedientId,
				expedient.getProcessInstanceId());
		// Per a cada instància de procés ordenem les dades per agrupació  
		// (si no tenen agrupació les primeres) i per ordre alfabètic de la etiqueta
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			List<ExpedientDocumentDto> documentsInstancia = null;
			if (instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
				documentsInstancia = expedientDocumentService.findAmbInstanciaProces(
						expedientId,
						instanciaProces.getId());
				// Ordena els documents per codi de document i si són annexos i no en tenen llavors van darrera ordenats per nom
				Collections.sort(documentsInstancia, new ExpedientDocumentDtoComparador(sort));	
			}
			
			documents.put(instanciaProces, documentsInstancia);
		}
		model.addAttribute("expedient", expedient);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("documents", documents);
		model.addAttribute("portasignaturesPendent", portasignaturesPendent);
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"documents");
		}
		return "v3/expedientDocument";
	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document", method = RequestMethod.GET)
	public String documentProces(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@RequestParam(defaultValue = "default") String sort,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);

		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
		List<ExpedientDocumentDto> documentsProces = expedientDocumentService.findAmbInstanciaProces(
				expedientId,
				processInstanceId);
		// Ordena els documents per codi de document i si són annexos i no en tenen llavors van darrera ordenats per nom
		Collections.sort(documentsProces, new ExpedientDocumentDtoComparador(sort));
		Map<InstanciaProcesDto, List<ExpedientDocumentDto>> documents = new LinkedHashMap<InstanciaProcesDto, List<ExpedientDocumentDto>>();
		List<PortasignaturesDto> portasignaturesPendent = expedientDocumentService.portasignaturesFindPendents(
				expedientId,
				processInstanceId);
		documents.put(instanciaProces, documentsProces);
		model.addAttribute("expedient", expedient);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("documents",documents);
		model.addAttribute("portasignaturesPendent", portasignaturesPendent);
		return "v3/procesDocuments";
	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/new", method = RequestMethod.GET)
	public String nouGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			Model model) {
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setExpedientId(expedientId);
		command.setData(new Date());
		command.setValidarArxius(true);
		model.addAttribute("documentsNoUtilitzats", getDocumentsNoUtilitzats(expedientId, processInstanceId));
		model.addAttribute("processInstanceId", processInstanceId);
		model.addAttribute("documentExpedientCommand", command);
		emplenarModelNti(expedientId, model);
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
		return "v3/expedientDocumentForm";
	}
	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@Validated(Create.class) @ModelAttribute DocumentExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		command.setNtiActiu(expedient.isNtiActiu());
		if (!bindingResult.hasErrors()) {
			String documentCodi = null;
			if (!DocumentExpedientCommand.ADJUNTAR_ARXIU_CODI.equalsIgnoreCase(command.getDocumentCodi())) {
				documentCodi = command.getDocumentCodi();
			}
			String arxiuNom = "";
			byte[] arxiuContingut = null;
			String arxiuContentType = "";
			byte[] firmaContingut = null;
			if (command.isGenerarPlantilla()) {
				try {
					ArxiuDto generat = expedientDocumentService.generarAmbPlantilla(
							expedientId,
							processInstanceId,
							documentCodi);
					arxiuNom = generat.getNom();
					arxiuContingut = generat.getContingut();
					arxiuContentType = generat.getTipusMime();
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.generar.document.info", new Object[] {ex.getMessage()}));
					logger.error("Error generant el document: " + documentCodi, ex);
				} 
			} else {
				arxiuContingut = command.getArxiu().getBytes();
				arxiuContentType = command.getArxiu().getContentType();
				if (command.getFirma() != null && command.getFirma().getSize() > 0) {
					firmaContingut = command.getFirma().getBytes();
				}
				arxiuNom = command.getArxiu().getOriginalFilename();
			}
			if (arxiuContingut != null) {
				try {
					expedientDocumentService.create(
							expedientId,
							processInstanceId,
							documentCodi, // null en el cas dels adjunts
							command.getData(),
							command.getNom(), // Títol en el cas dels adjunts
							arxiuNom,
							arxiuContingut,
							arxiuContentType,
							command.isAmbFirma(),
							DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma()),
							firmaContingut,
							command.getNtiOrigen(),
							command.getNtiEstadoElaboracion(),
							command.getNtiTipoDocumental(),
							command.getNtiIdOrigen());
					
					MissatgesHelper.success(request, getMessage(request, "info.document.guardat") );
					return modalUrlTancar(false);
				} catch(Exception e) {
					String errMsg = getMessage(request, "info.document.guardat.error", new Object[] {e.getMessage()});
					logger.error(errMsg, e);
					MissatgesHelper.error(request, errMsg);
				}
			}
		}
    	model.addAttribute("documentsNoUtilitzats", getDocumentsNoUtilitzats(expedientId, processInstanceId));
		model.addAttribute("processInstanceId", processInstanceId);
		model.addAttribute("documentExpedientCommand", command);
		emplenarModelNti(expedientId, model);
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
    	return "v3/expedientDocumentForm";
	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/update", method = RequestMethod.GET)
	public String updateGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setExpedientId(expedientId);
		command.setDocId(document.getDocumentId());
		command.setValidarArxius(false);
		if (document.isAdjunt()) {
			command.setNom(document.getAdjuntTitol());
		} else {
			command.setNom(document.getDocumentNom());
		}
		command.setCodi(document.getDocumentCodi());
		command.setData(document.getDataDocument());
		model.addAttribute("document", document);	
		model.addAttribute("expedientId", expedientId);
		ExpedientDto expedient = emplenarModelNti(expedientId, model);
		if (expedient.isNtiActiu()) {
			command.setNtiOrigen(document.getNtiOrigen());
			command.setNtiEstadoElaboracion(document.getNtiEstadoElaboracion());
			command.setNtiTipoDocumental(document.getNtiTipoDocumental());
			command.setNtiIdOrigen(document.getNtiIdOrigen());
		}
		model.addAttribute("documentExpedientCommand", command);
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
		return "v3/expedientDocumentForm";
	}

	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@Validated(Update.class) @ModelAttribute DocumentExpedientCommand command,
			BindingResult result,
			Model model) throws IOException {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		command.setNtiActiu(expedient.isNtiActiu());
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
    			expedientId,
    			processInstanceId,
    			documentStoreId);
		if (!result.hasErrors()) {
			String arxiuNom = "";
			byte[] arxiuContingut = null;
			String arxiuContentType = "";
			byte[] firmaContingut = null;
			if (command.isGenerarPlantilla()) {
				try {
					ArxiuDto generat = expedientDocumentService.generarAmbPlantilla(
							expedientId,
							processInstanceId,
							document.getDocumentCodi());
					arxiuNom = generat.getNom();
					arxiuContingut = generat.getContingut();
					arxiuContentType = generat.getTipusMime();
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.generar.document.info", new Object[] {ex.getMessage()}));
					logger.error("Error generant el document: " + document.getDocumentCodi(), ex);
				} 				
			} else {
					arxiuContingut = command.getArxiu().getBytes();
					arxiuNom = command.getArxiu().getOriginalFilename();
					arxiuContentType = command.getArxiu().getContentType();
					if(arxiuContingut == null || arxiuContingut.length == 0) {
						ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocument(expedientId, processInstanceId, documentStoreId);
						arxiuContingut = arxiu.getContingut();
						arxiuNom = arxiu.getNom();
					}
					firmaContingut = null;
					if (command.getFirma() != null && command.getFirma().getSize() > 0) {
						firmaContingut = command.getFirma().getBytes();
					}
				}

			if (arxiuContingut != null) {
				try {
					expedientDocumentService.update(
							expedientId,
							processInstanceId,
							documentStoreId,
							command.getData(),
							document.isAdjunt() ? // Títol en el cas dels adjunts 
									command.getNom() 
									: null, 
							arxiuNom,
							(arxiuContingut.length != 0) ? arxiuContingut : null,
							arxiuContentType,
							command.isAmbFirma(),
							DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma()),
							firmaContingut,
							command.getNtiOrigen(),
							command.getNtiEstadoElaboracion(),
							command.getNtiTipoDocumental(),
							command.getNtiIdOrigen());
					
					MissatgesHelper.success(request, getMessage(request, "info.document.guardat"));
					return modalUrlTancar(false);				
				} catch(Exception e) {
					String errMsg = getMessage(request, "info.document.guardat.error", new Object[] {e.getMessage()});
					logger.error(errMsg, e);
					MissatgesHelper.error(request, errMsg);				
				}
			}
		}
		// Retorna al formulari per mostrar errors
		model.addAttribute("processInstanceId", processInstanceId);
		model.addAttribute("document", document);
		emplenarModelNti(expedientId, model);
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
    	return "v3/expedientDocumentForm";
	}

	
	
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/notificar", method = RequestMethod.GET)
	public String notificarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {

		DocumentNotificacioCommand command = new DocumentNotificacioCommand();
		Calendar caducitat = new GregorianCalendar();
		caducitat.setTime(new Date());
		caducitat.add(Calendar.DATE, 1);
		command.setCaducitat(caducitat.getTime());
		command.setEnviamentTipus(EnviamentTipusEnumDto.NOTIFICACIO);
		command.setIdioma(IdiomaEnumDto.CA);
		model.addAttribute("documentNotificacioCommand", command);
		
		ExpedientDocumentDto document = this.emplenarModelNotificacioDocument(expedientId, processInstanceId, documentStoreId, model);
		//Validar que tingui els permissos de gestió documental o administrar
		
		ExpedientDto expedientDto = expedientService.findAmbIdAmbPermis(expedientId);
		if (!expedientDto.isPermisDocManagement() && !expedientDto.isPermisAdministration()) {
				MissatgesHelper.error(request, 
					getMessage(request, 
							"expedient.document.notificar.validacio.no.permisos"));
				return modalUrlTancar(false);
			}

		// Validar que l'arxiu sigui convertible a pdf o zip
		if (!PdfUtils.isArxiuConvertiblePdf(document.getArxiuNom())
				&& !"zip".equals((document.getArxiuExtensio() != null ? document.getArxiuExtensio().toLowerCase() :  ""))) {
			MissatgesHelper.error(request, 
					getMessage(request, 
							"expedient.document.notificar.validacio.no.notificable",
							new Object[] {PdfUtils.getExtensionsConvertiblesPdf()}));
			return modalUrlTancar(false);
		}
		return "v3/expedientDocumentNotificar";
	}
	
	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/notificar", method = RequestMethod.POST)
	public String notificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@Validated DocumentNotificacioCommand documentNotificacioCommand,
			BindingResult result,
			Model model) throws IOException {
		if (result.hasErrors()) {
			this.emplenarModelNotificacioDocument(expedientId, processInstanceId, documentStoreId, model);
	    	return "v3/expedientDocumentNotificar";
		}
		try {
			DadesNotificacioDto dadesNotificacioDto = ConversioTipusHelper.convertir(
					documentNotificacioCommand, 
					DadesNotificacioDto.class);		
			
			// Dona d'alta una notificació per interessat
			DadesNotificacioDto dadesNotificacio;
			InteressatDto interessat;
			for (Long interessatId : documentNotificacioCommand.getInteressatsIds()) {
				interessat = expedientInteressatService.findOne(interessatId);
				// Alta de la notificació
				dadesNotificacio = expedientDocumentService.notificarDocument(
						expedientId,
						documentStoreId,
						dadesNotificacioDto,
						interessatId,
						documentNotificacioCommand.getRepresentantId());
				// Missatge del resultat
				if (! dadesNotificacio.getError())
					MissatgesHelper.success(request, getMessage(request, "info.document.notificat", new Object[] {interessat.getFullInfo()}));
				else
					MissatgesHelper.warning(request, getMessage(request, "info.document.notificar.avis", new Object[] {interessat.getFullInfo(), dadesNotificacio.getErrorDescripcio()}));
			}
		} catch(Exception e) {
			String errMsg = getMessage(request, "info.document.notificar.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
			this.emplenarModelNotificacioDocument(expedientId, processInstanceId, documentStoreId, model);
			return "v3/expedientDocumentNotificar";
		}
		return modalUrlTancar(false);
	}
	
	private ExpedientDocumentDto emplenarModelNotificacioDocument(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			Model model) {
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		List<InteressatDto> interessats = expedientInteressatService.findByExpedient(
				expedientId);
		model.addAttribute("interessats", interessats);
		model.addAttribute("document", document);	
		model.addAttribute("expedientId", expedientId);
		
		// entregaPostalViaTipusEstats
		List<ParellaCodiValorDto> opcions = new ArrayList<ParellaCodiValorDto>();
		for(EntregaPostalViaTipus entregaPostalViaTipus : EntregaPostalViaTipus.values())
			opcions.add(new ParellaCodiValorDto(
					entregaPostalViaTipus.name(),
					MessageHelper.getInstance().getMessage("notifica.entregaPostal.via.tipus.enum." + entregaPostalViaTipus.name())));		

		model.addAttribute("entregaPostalViaTipusEstats", opcions);

		// entregaPostalTipusEstats
		opcions = new ArrayList<ParellaCodiValorDto>();
		for(EntregaPostalTipus entregaPostalTipus : EntregaPostalTipus.values())
			opcions.add(new ParellaCodiValorDto(
					entregaPostalTipus.name(),
					MessageHelper.getInstance().getMessage("notifica.entregaPostal.enum." + entregaPostalTipus.name())));		
		model.addAttribute("entregaPostalTipusEstats", opcions);

		// serveiTipusEstats
		opcions = new ArrayList<ParellaCodiValorDto>();
		for(ServeiTipusEnumDto serveiTipus : ServeiTipusEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					serveiTipus.name(),
					MessageHelper.getInstance().getMessage("notifica.servei.tipus.enum." + serveiTipus.name())));		
		model.addAttribute("serveiTipusEstats", opcions);
		
		// enviamentTipusEstats
		opcions = new ArrayList<ParellaCodiValorDto>();
		for(EnviamentTipusEnumDto enviamentTipus : EnviamentTipusEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					enviamentTipus.name(),
					MessageHelper.getInstance().getMessage("notifica.enviament.tipus.enum." + enviamentTipus.name())));		
		model.addAttribute("enviamentTipusEstats", opcions);

		// idiomes
		opcions = new ArrayList<ParellaCodiValorDto>();
		for(IdiomaEnumDto idioma : IdiomaEnumDto.values())
			opcions.add(new ParellaCodiValorDto(
					idioma.name(),
					MessageHelper.getInstance().getMessage("enum.idioma." + idioma.name())));		
		model.addAttribute("idiomes", opcions);
		
		return document;
	}

	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/descarregar")
	public String descarregar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		try {
			ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocument(
					expedientId,
					processInstanceId,
					documentStoreId);
			if (arxiu != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
			}
		} catch (SistemaExternException e) {
			logger.error("Error descarregant fitxer", e);
			MissatgesHelper.error(request, e.getPublicMessage());
			model.addAttribute("pipellaActiva", "documents");
			return "redirect:/v3/expedient/" + expedientId;
		}
		return "arxiuView";
	}
	
	@RequestMapping(value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/descarregar/versio/{versioId}/{expedientTancat}")
	public String descarregarVersio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@PathVariable String versioId,
			@PathVariable boolean expedientTancat,
			Model model) {
		try {
			ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocumentVersio(
					expedientId,
					processInstanceId,
					documentStoreId,
					expedientTancat == true ? null : versioId);
			if (arxiu != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
			}
		} catch (SistemaExternException e) {
			logger.error("Error descarregant fitxer", e);
			MissatgesHelper.error(request, e.getPublicMessage());
			model.addAttribute("pipellaActiva", "documents");
			return "redirect:/v3/expedient/" + expedientId;
		}
		return "arxiuView";
	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/metadadesNti", method = RequestMethod.GET)
	public String metadadesNti(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			ExpedientDocumentDto expedientDocument = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentStoreId);
			model.addAttribute("expedientDocument", expedientDocument);
			model.addAttribute("expedientId", expedientId);
			if (expedient.isArxiuActiu()) {
				if (!StringUtils.isEmpty(expedientDocument.getArxiuUuid())) {
					model.addAttribute(
							"arxiuDetall",
							expedientDocumentService.getArxiuDetall(
									expedientId,
									processInstanceId,
									documentStoreId));
				} else {
					model.addAttribute("errorArxiuNoUuid", Boolean.TRUE);
				}
				if (expedient.getNtiOrgano() == null) {
					// La migració d'expedients no NTI provocava errors
					model.addAttribute("expedient", expedient);
					model.addAttribute("errorMetadadesNti", Boolean.TRUE);
				}
			}
		} catch(Exception e) {
			String errMsg = "Error consultant les dades de l'Arxiu del document: " + e.getMessage(); 
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		return "v3/expedientDocumentMetadadesNti";
	}

	/** Mètode per incoporar el document a l'Arxiu en el cas que l'expedient estigui integrat però el document no. Acció des
	 * de la modal de metadades NTI del document.
	 * @return Retorna cap a la pàgina de metadades nti del document.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/incoporarArxiu", method = RequestMethod.POST)
	public String incoporarArxiu(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		try {
			expedientDocumentService.migrarArxiu(expedientId, documentStoreId);
			MissatgesHelper.success(request, getMessage(request, "expedient.document.arxiu.migrar.success"));
		} catch(Exception e) {
			String errMsg = "Error incorporant el document a l'Arxiu: " + e.getMessage(); 
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		return "redirect:" + request.getHeader("Referer");
	}
	
	/** Mètode per descarregar una firma dettached des de la modal de dades de l'arxiu d'un document. */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/firma/{firmaIndex}/descarregar", method = RequestMethod.GET)
	public String descarregarFirma(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@PathVariable int firmaIndex,
			Model model) {
		try {
			ArxiuFirmaDto arxiuFirma = expedientDocumentService.getArxiuFirma(
					expedientId,
					documentStoreId,
					firmaIndex);
			if (arxiuFirma != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiuFirma.getFitxerNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiuFirma.getContingut());
			}
		} catch (SistemaExternException e) {
			logger.error("Error descarregant l'arxiu de firma", e);
			MissatgesHelper.error(request, e.getPublicMessage());
			model.addAttribute("pipellaActiva", "documents");
			return "redirect:/v3/expedient/" + expedientId;
		}
		return "arxiuView";

	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/esborrar")
	@ResponseBody
	public boolean esborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,	
			ModelMap model) throws ServletException {
		boolean response = false; 
		try {
			expedientDocumentService.delete(
					expedientId,
					processInstanceId,
					documentStoreId);
			MissatgesHelper.success(request, getMessage(request, "info.doc.proces.esborrat"));
			response = true; 
		} catch (Exception ex) {
			logger.error(getMessage(request, "error.esborrar.doc.proces"), ex);
			MissatgesHelper.error(request, getMessage(request, "error.esborrar.doc.proces") + ": " + ex.getLocalizedMessage());
		}
		return response;
	}

	/** Aquest mètode genera el document a partir de la plantilla i el descarrega.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentCodi}/generar", method = RequestMethod.GET)
	public String generar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable String documentCodi,
			Model model) {
		try {
			ArxiuDto generat = expedientDocumentService.generarAmbPlantilla(
					expedientId,
					processInstanceId,
					documentCodi);
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, generat.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, generat.getContingut());
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.generar.document.info", new Object[] {ex.getMessage()}));
			logger.error("Error generant el document: " + documentCodi, ex);
			ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentCodi);
			if (document != null) {
				return "redirect:/modal/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/" + document.getId() + "/update";
			} else {
				return "redirect:/modal/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/new";
			}
		} 
		return "arxiuView";
	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/signatura/verificar", method = RequestMethod.GET)
	public String verificarSignatura(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@RequestParam(value = "urlVerificacioCustodia", required = false) final String urlVerificacioCustodia,
			ModelMap model) throws ServletException {
		try {
			if (urlVerificacioCustodia != null && !urlVerificacioCustodia.isEmpty()) {
				model.addAttribute("tittle", getMessage(request, "expedient.document.verif_signatures"));
				model.addAttribute("url", urlVerificacioCustodia);
				return "v3/utils/modalIframe";
			}			
			// Recupera la informació del document i la afegeix al model
			ExpedientDocumentDto ed = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentStoreId);
			if (ed != null) {
				model.addAttribute(
						"signatura",
						expedientDocumentService.findDocumentAmbId(documentStoreId));
			}			
			model.addAttribute("signatures", expedientService.verificarSignatura(documentStoreId));
			return "v3/expedientTascaTramitacioSignarVerificar";
		} catch(Exception ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new ServletException(ex);
	    }
	}
	
	/** Mètode per redireccionar cal a la pàgina de verificació del CSV. Pot ser que el document no el tingui
	 * ben informat i s'hagi de consultar a l'Arxiu. Si no es pot consultar s'ha de retornar un error.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/signatura/verificarCsv", method = RequestMethod.GET)
	public String verificarCsv(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@RequestParam(value = "csv", required = false) final String csv,
			ModelMap model) throws ServletException {
		String ret = "redirect:/v3/expedient/";
		try {
			// La pròpia consulta actualtiza el CSV al documnet
			expedientDocumentService.getArxiuDetall(
					expedientId,
					processInstanceId,
					documentStoreId);
			ExpedientDocumentDto expedientDocument = expedientDocumentService.findOneAmbInstanciaProces(
						expedientId,
						processInstanceId,
						documentStoreId);
			 ret = "redirect:" + expedientDocument.getSignaturaUrlVerificacio();
		} catch(Exception ex) {
			String errMsg = getMessage(request, "expedient.document.verificar.csv.error", new Object[] {ex.getClass(), ex.getMessage()});
			logger.error(errMsg, ex);
			MissatgesHelper.error(request, errMsg);
	    }
		return ret;
	}


	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/signatura/esborrar", method = RequestMethod.GET)
	@ResponseBody
	public boolean signaturaEsborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,	
			ModelMap model) throws ServletException {
		boolean response = false; 
		try {
			expedientService.deleteSignatura(expedientId, documentStoreId);
			MissatgesHelper.success(request, getMessage(request, "info.signatura.esborrat"));
			response = true; 
		} catch (Exception ex) {
			logger.error(getMessage(request, "error.esborrar.signatura"), ex);
			MissatgesHelper.error(request, getMessage(request, "error.esborrar.signatura") + ": " + ex.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/registre/verificar", method = RequestMethod.GET)
	public String verificarRegistre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			ModelMap model) throws ServletException {
		try {
			model.addAttribute(
					"document",
					expedientDocumentService.findOneAmbInstanciaProces(
							expedientId,
							processInstanceId,
							documentStoreId));
			return "v3/expedientDocumentRegistreVerificar";
		} catch(Exception ex) {
			logger.error("Error al verificar la signatura", ex);
			throw new ServletException(ex);
	    }
	}

	/*
	 * retorna un JSON de amb la info del document
	 */
	@RequestMapping(
			value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/psignainfo", 
			method = RequestMethod.GET)
	@ResponseBody
	public Object psignaInfo(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId) {
		Object psignaInfo = expedientDocumentService.findPortasignaturesInfo(expedientId, processInstanceId, documentStoreId);
		
		return psignaInfo;
	}

	/** Mètode per reintentar el processament del document que s'envia al porta signatures. */
	@RequestMapping(
			value="/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/psignaReintentar", 
			method = RequestMethod.GET)
	public String documentPsignaReintentar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@RequestParam(value = "psignaId", required = true) Integer psignaId,
			Model model) {
		// TODO: eliminar la referencia al core 2.6 i passar el mètode processarDocumentPendentPortasignatures al pluginHelper
		if (pluginService.processarDocumentPendentPortasignatures(psignaId))
			MissatgesHelper.success(request, getMessage(request, "expedient.psigna.reintentar.ok"));
		else
			MissatgesHelper.error(request, getMessage(request, "expedient.psigna.reintentar.error"));

		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/document/arxiuMostrar")
	public String arxiuMostrar(
		HttpServletRequest request,
		@RequestParam(value = "token", required = true) String token,
		ModelMap model) {
		ArxiuDto arxiu = null;
		if (token != null) {
			arxiu = expedientDocumentService.findArxiuAmbTokenPerMostrar(token);
		}
		if (arxiu != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
		}
		return "arxiuView";
	}

	@RequestMapping(value = "/document/arxiuPerSignar")
	public String arxiuPerSignar(
		HttpServletRequest request,
		@RequestParam(value = "token", required = true) String token,
		ModelMap model) throws Exception {
		ArxiuDto arxiu = null;
		if (token != null)
			try {
				arxiu = expedientDocumentService.findArxiuAmbTokenPerSignar(token);
			} catch (SistemaExternException ex) {
				logger.error("Error al obtenir el document a partir del token '" + token + "'", ex);
				MissatgesHelper.error(request,ex.getPublicMessage());
			} catch (Exception ex) {
				logger.error("Error al obtenir el document a partir del token '" + token + "'", ex);
				MissatgesHelper.error(request,ex.getMessage());
			}	
		if (arxiu != null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
		}
		return "arxiuView";
	}
	
	/** Recupera el contingut de tots els documents i crea un comprimit per a la descàrrega.
	 * 
	 */
	@RequestMapping(value = "/{expedientId}/document/descarregarZip", method = RequestMethod.GET)
	public String descarregarZipDocumentacio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model)  {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, expedient.getIdentificador() + ".zip");
			model.addAttribute(
					ArxiuView.MODEL_ATTRIBUTE_DATA,
					expedientService.getZipDocumentacio(expedientId));
		} catch(Exception e) {
			MissatgesHelper.error(request, getMessage(request, "expedient.document.descarregar.zip.error", new Object[]{ e.getMessage() } ));
		}
		return "arxiuView";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	private List<DocumentDto> getDocumentsNoUtilitzats(Long expedientId, String procesId) {
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		List<DocumentDto> documents = dissenyService.findDocumentsOrdenatsPerCodi(
				expedient.getTipus().getId(),
				instanciaProces.getDefinicioProces().getId(),
				true);	// amb herència
		List<ExpedientDocumentDto> documentsInstancia = expedientDocumentService.findAmbInstanciaProces(expedientId, procesId);
		if (documentsInstancia != null && documentsInstancia.size() > 0) {
			List<DocumentDto> documentsNoUtilitzats = new ArrayList<DocumentDto>();
			// Posa els codis dels documents utilitzats en un Set
			Set<String> codisDocumentsExistents = new HashSet<String>();
			for (ExpedientDocumentDto documentExpedient : documentsInstancia)
				codisDocumentsExistents.add(documentExpedient.getDocumentCodi());
			// Mira quins documents no s'han utilitzat i els retorna
			for(DocumentDto document: documents) 
				if (!codisDocumentsExistents.contains(document.getCodi()))
					documentsNoUtilitzats.add(document);
			return documentsNoUtilitzats;
		} else {
			return documents;
		}
	}

	private ExpedientDto emplenarModelNti(
			Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		model.addAttribute("expedient", expedient);
		if (expedient.isNtiActiu()) {
			ntiHelper.omplirOrigen(model);
			ntiHelper.omplirEstadoElaboracion(model);
			ntiHelper.omplirTipoDocumental(model);
			ntiHelper.omplirTipoFirma(model);
		}
		return expedient;
	}

	/** Obre el formulari per iniciar la firma en passsarel·la web
	 * 
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/firmaPassarela", method = RequestMethod.GET)
	public String firmaPassarelaGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {		
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		boolean potFirmar = true;
		if (document.getArxiuUuid() == null && document.getCustodiaCodi() == null) {
			MissatgesHelper.error(request, getMessage(request, "expedient.document.firmaPassarela.validacio.custodia.codi"));
			potFirmar = false;
		} 
		DocumentExpedientFirmaPassarelaCommand command = new DocumentExpedientFirmaPassarelaCommand();
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		command.setMotiu(getMessage(request, "expedient.document.firmaPassarela.camp.motiu.default", new Object[] {expedient.getNumero()}));
		model.addAttribute("document", document);	
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("documentExpedientFirmaPassarelaCommand", command);
		model.addAttribute("potFirmar", potFirmar);
		return "v3/expedientDocumentFirmaPassarelaForm";
	}
	/** 
	 * Inicia el procés de firma del document i si tot va bé redirigeix cap a la URL retornada pel
	 * portasignatures.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/firmaPassarela", method = RequestMethod.POST)
	public String firmaPassarelaPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@Validated(FirmaPassarela.class) DocumentExpedientFirmaPassarelaCommand command,
			BindingResult bindingResult,
			Model model) {
		
		if (bindingResult.hasErrors()) {
			ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentStoreId);
			model.addAttribute("document", document);	
			model.addAttribute("expedientId", expedientId);
			model.addAttribute("documentExpedientFirmaPassarelaCommand", command);
			model.addAttribute("potFirmar", true);
			return "v3/expedientDocumentFirmaPassarelaForm";
		}
		try {
			ArxiuDto arxiuPerFirmar = expedientDocumentService.arxiuFindAmbDocument(
					expedientId,
					processInstanceId,
					documentStoreId);

			PersonaDto usuariActual = aplicacioService.findPersonaActual();
			String modalStr = (ModalHelper.isModal(request)) ? "/modal" : "";
			String procesFirmaUrl = passarelaFirmaHelper.iniciarProcesDeFirma(
					request,
					arxiuPerFirmar,
					documentStoreId.toString(),
					usuariActual.getDni(),
					command.getMotiu(),
					command.getLloc() != null ? command.getLloc() : "Illes Balears (HELIUM)",
					usuariActual.getEmail(),
					LocaleContextHolder.getLocale().getLanguage(),
					modalStr + "/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/" + documentStoreId + "/firmaPassarelaFinal",
					false);
			return "redirect:" + procesFirmaUrl;
		} catch(Exception e) {
			
		}
		return getModalControllerReturnValueSuccess(
				request, 
				"redirect:/v3/helium/expedient/" + expedientId + "?pipellaActiva=documents",
				null);
	}

	/** Mètode que es crida a la tornada de la firma per passarel·la en el Portasignatures. Si tot va bé es tanca la modal
	 * amb un missatge que ha anat bé.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/firmaPassarelaFinal", method = RequestMethod.GET)
	public String firmaPassarelaFinal(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@RequestParam("signaturesSetId") String signaturesSetId,
			Model model) {
				
		String ret = "redirect:/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/" + documentStoreId + "/firmaPassarela";
		try {
			PassarelaFirmaConfig signaturesSet = passarelaFirmaHelper.finalitzarProcesDeFirma(
					request,
					signaturesSetId);
			StatusSignaturesSet status = signaturesSet.getStatusSignaturesSet();
			FileInfoSignature firmaInfo = new FileInfoSignature();
			StatusSignature firmaStatus = new StatusSignature();
			if (signaturesSet!=null) {
				firmaInfo = signaturesSet.getFileInfoSignatureArray()[0];
				firmaStatus = firmaInfo.getStatusSignature();
				if(firmaStatus.getStatus()==-1) {
					status.setErrorMsg(signaturesSet.getFirmaSimpleStatus().getErrorMessage());
				}
			}
			switch (status.getStatus()) {
			case StatusSignaturesSet.STATUS_FINAL_OK:
				if (firmaStatus.getStatus() == StatusSignature.STATUS_FINAL_OK) {
					if (signaturesSet.getSignedData()==null) {
						firmaStatus.setStatus(StatusSignature.STATUS_FINAL_ERROR);
						String msg = "L'estat indica que ha finalitzat correctament però el fitxer firmat o no s'ha definit o no existeix";
						firmaStatus.setErrorMsg(msg);
						MissatgesHelper.error(
								request,
								getMessage(
										request, 
										"document.controller.firma.passarela.final.ok.nofile"));
					} else {
						try {
							expedientDocumentService.processarFirmaClient(
									expedientId,
									processInstanceId,
									documentStoreId,
									firmaInfo.getName(), 
									signaturesSet.getSignedData());
							
							MissatgesHelper.success(
									request,
									getMessage(
											request, 
											"document.controller.firma.passarela.final.ok"));
							
							ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
									expedientId,
									processInstanceId,
									documentStoreId);
							model.addAttribute("document", document);	
							model.addAttribute("potFirmar", false);
							
							ret = "v3/expedientDocumentFirmaPassarelaForm";
							
						} catch (Exception e) {
							String errMsg = getMessage(
									request, 
									"document.controller.firma.passarela.final.error.validacio",
									new Object[] {ExceptionUtils.getRootCauseMessage(e)});
							logger.error("Error en la signatura del document. " + errMsg, e);
							MissatgesHelper.error(
									request,
									errMsg);
						}
					}
				} else {
					MissatgesHelper.error(
							request,
							getMessage(
									request, 
									"document.controller.firma.passarela.final.ok.statuserr",
									new Object[] {firmaStatus.getStatus(), firmaStatus.getErrorMsg()}));
				}
				break;
			case StatusSignaturesSet.STATUS_FINAL_ERROR:
				MissatgesHelper.error(
						request,
						getMessage(
								request, 
								"document.controller.firma.passarela.final.error",
								new Object[] {
										status.getErrorMsg() != null? status.getErrorMsg() : "",
										status.getErrorException() != null? status.getErrorException().getMessage() : ""
								}));
				break;
			case StatusSignaturesSet.STATUS_CANCELLED:
				String msg = "La firma del document ha estat cancel.lada";
				firmaStatus.setErrorMsg(msg);
				MissatgesHelper.warning(
						request,
						getMessage(
								request, 
								"document.controller.firma.passarela.final.cancel"));
				break;
			default:
				MissatgesHelper.warning(
						request,
						getMessage(
								request, 
								"document.controller.firma.passarela.final.desconegut"));
			}
			passarelaFirmaHelper.closeSignaturesSet(
					request,
					signaturesSet);
		} catch(Exception e) {
			String errMsg = "Error no controlat en el procés de firma en passarel·la web: " + e.getMessage();
			MissatgesHelper.error(request, errMsg);
		}
		return ret;
	}
	
	@RequestMapping(value = "/{expedientId}/document/notificarZip", method = RequestMethod.GET)
	public String notificarZip(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		DocumentExpedientCommand command = new DocumentExpedientCommand();
		command.setExpedientId(expedientId);
		command.setData(new Date());
		command.setValidarArxius(true);
//		model.addAttribute("documentsNoUtilitzats", getDocumentsNoUtilitzats(expedientId, processInstanceId));
		model.addAttribute("documentExpedientCommand", command);
		emplenarModelNti(expedientId, model);
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"enum.document.tipus.firma."));
		return "v3/expedientDocumentNotificarZip";
	}
	
	/** Obre el formulari de l'enviament al portafirmes
	 * 
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/enviarPortasignatures", method = RequestMethod.GET)
	public String enviarPortasignaturesGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {		
		ExpedientDocumentDto expedientDocumentDto = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		boolean potFirmar = true;
		if (expedientDocumentDto.getArxiuUuid() == null && expedientDocumentDto.getCustodiaCodi() == null) {
			MissatgesHelper.error(request, getMessage(request, "expedient.document.firmaPassarela.validacio.custodia.codi"));
			potFirmar = false;
		}
		
		DocumentExpedientEnviarPortasignaturesCommand command = new DocumentExpedientEnviarPortasignaturesCommand();
		
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		if(expedientDocumentDto.getDocumentCodi()!=null) {	
			DocumentDto documentDto = documentService.findAmbCodi(expedient.getTipus().getId(), null, expedientDocumentDto.getDocumentCodi(), true);	
			command.setPortafirmesPrioritatTipus(PortafirmesPrioritatEnumDto.NORMAL);
			command.setNom(documentDto.getDocumentNom());
			command.setId(documentDto.getDocumentId());
			command.setPortafirmesActiu(documentDto.isPortafirmesActiu());
			if(documentDto.isPortafirmesActiu()) {
				command.setId(documentStoreId);
				command.setPortafirmesFluxTipus(documentDto.getPortafirmesFluxTipus());
				if(documentDto.getPortafirmesFluxTipus().equals(PortafirmesTipusEnumDto.FLUX)) {
					expedientDocumentDto.setPortafirmesFluxId(documentDto.getPortafirmesFluxId());
					command.setPortafirmesFluxId(documentDto.getPortafirmesFluxId());
					model.addAttribute("portafirmesFluxSeleccionat", documentDto.getPortafirmesFluxId());
					model.addAttribute("portafirmesFluxId", documentDto.getPortafirmesFluxId());
					String urlMostrarPlantilla = portafirmesFluxService.recuperarUrlMostrarPlantilla(documentDto.getPortafirmesFluxId());
					model.addAttribute("urlFluxFirmes", urlMostrarPlantilla);
				}
				else if(documentDto.getPortafirmesFluxTipus().equals(PortafirmesTipusEnumDto.SIMPLE)) {
					command.setPortafirmesResponsables(documentDto.getPortafirmesResponsables());
					command.setPortafirmesSequenciaTipus(documentDto.getPortafirmesSequenciaTipus());
				}
			}
			
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
			model.addAttribute(
					"portafirmesPrioritatEnumOptions",
					EnumHelper.getOptionsForEnum(
							PortafirmesPrioritatEnumDto.class
							,"enum.document.tipus.portafirmes.prioritat."));
		}
		// Possibles annexos
		List<ExpedientDocumentDto> annexos = expedientDocumentService.findAmbInstanciaProces(expedientId, processInstanceId);
		// Treu adjunts i el propi document
		Iterator<ExpedientDocumentDto> docI = annexos.iterator();
		while(docI.hasNext()) {
			ExpedientDocumentDto doc = docI.next();
			if (doc.getId() == documentStoreId
					|| doc.isAdjunt()) {
				docI.remove();
			}
		}
		model.addAttribute("annexos", annexos);
		command.setMotiu(getMessage(request, "expedient.document.firmaPassarela.camp.motiu.default", new Object[] {expedient.getNumero()}));
		model.addAttribute("document", expedientDocumentDto);	
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("documentExpedientEnviarPortasignaturesCommand", command);
		model.addAttribute("potFirmar", potFirmar);
		return "v3/expedientDocumentEnviarPortasignaturesForm";
	}
	
	
	
	/** 
	 * Visualitza la modal amb la informació de l'enviament al portasignatures, donant la opció de cancelar-lo. 
	 * portasignatures.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/pendentSignatura", method = RequestMethod.GET)
	public String pendentSignatura(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) throws UnsupportedEncodingException {
		
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		
			model.addAttribute("document", document);	
			model.addAttribute("expedientId", expedientId);
			model.addAttribute("potFirmar", true);
			
		PortasignaturesDto psignaPendentActual = expedientDocumentService.getPortasignaturesByDocumentStoreId(documentStoreId);
		model.addAttribute("psignaPendentActual", psignaPendentActual);
		if(psignaPendentActual.getDocumentId() != null) {
			String urlMostrarPlantilla = portafirmesFluxService.recuperarUrlViewEstatFluxDeFirmes(psignaPendentActual.getDocumentId());
			model.addAttribute("urlFluxFirmes", urlMostrarPlantilla);
		}
		
		return "v3/expedientDocumentPendentPortasignaturesForm";
	}
	/** 
	 * Inicia el procés de firma del document i si tot va bé redirigeix cap a la URL retornada pel
	 * portasignatures.
	 */
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/enviarPortasignatures", method = RequestMethod.POST)
	public String enviarPortasignaturesPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@Validated(EnviarPortasignatures.class) DocumentExpedientEnviarPortasignaturesCommand command,
			BindingResult bindingResult,
			Model model) {
		
		if (bindingResult.hasErrors()) {
			ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentStoreId);
			model.addAttribute("document", document);	
			model.addAttribute("expedientId", expedientId);
			model.addAttribute("documentExpedientEnviarPortasignaturesCommand", command);
		}
		try {
			this.enviarPortasignatures(command, documentStoreId, expedientId,  processInstanceId);
			MissatgesHelper.success(
					request, 
					getMessage(
							request,"expedient.document.enviar.portasignatures.ok"));
			return modalUrlTancar(false);
		} catch(Exception e) {
			String errMsg = getMessage(request, "expedient.document.enviar.portasignatures.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
	
		}
		return "v3/expedientDocumentEnviarPortasignaturesForm";
		
	}
	private void enviarPortasignatures(DocumentExpedientEnviarPortasignaturesCommand command, Long documentStoreId, Long expedientId, String processInstanceId) {
		PersonaDto usuariActual = aplicacioService.findPersonaActual();
		
		DocumentStore documentStore = documentHelper.findById(documentStoreId);
		DocumentDto documentDto = expedientDocumentService.findDocumentAmbId(documentStoreId);
		ExpedientDto expedientDto = expedientService.findAmbIdAmbPermis(expedientId);
		// Valida que no sigui ja un document firmat
		if (documentStore.isSignat()) 
			throw new ValidacioException("No es pot enviar a firmar al Portasignatures un document que ja està signat");

		List<Long> annexosId = command.getAnnexos();
		List<DocumentDto> annexos = null;
		if (annexosId != null) {
			annexos = new ArrayList<DocumentDto>();
			for (Long docId: annexosId) {
				DocumentDto docDto = documentHelper.toDocumentDto(
						docId,
						false,
						false,
						true,
						false,
						false, // Per notificar
						false);
				if (docDto != null){
					annexos.add(docDto);
				}
			}
		}

		//Com a RIPEA
		List<PortafirmesFluxBlocDto> blocList = new ArrayList<PortafirmesFluxBlocDto>();//MARTA
		PortafirmesFluxBlocDto bloc = null;
		if (command.getPortafirmesResponsables()!=null && !command.getPortafirmesResponsables().isEmpty()) {
			String[] responsablesCodis = command.getPortafirmesResponsables().split(",");
			if (command.getPortafirmesSequenciaTipus().equals(PortafirmesSimpleTipusEnumDto.SERIE)) {				
				for (int i = 0; i < responsablesCodis.length; i++) {
					List<PersonaDto> personesPas = new ArrayList<PersonaDto>();
					bloc = new PortafirmesFluxBlocDto();
					PersonaDto persona = aplicacioService.findPersonaAmbCodi(responsablesCodis[i]);	
					personesPas.add(persona);
					bloc.setDestinataris(personesPas);
					bloc.setMinSignataris(1);
					bloc.setObligatorietats(new boolean[] {true});
					blocList.add(bloc);
				}
			} else if (command.getPortafirmesSequenciaTipus().equals(PortafirmesSimpleTipusEnumDto.PARALEL)) {
				bloc = new PortafirmesFluxBlocDto();
				List<PersonaDto> personesPas = new ArrayList<PersonaDto>();
				bloc.setMinSignataris(command.getPortafirmesResponsables().length());
				for (int i = 0; i < responsablesCodis.length; i++) {
					PersonaDto persona = aplicacioService.findPersonaAmbCodi(responsablesCodis[i]);	
					personesPas.add(persona);
				}
				bloc.setDestinataris(personesPas);
				boolean[] obligatorietats = new boolean[responsablesCodis.length];
				Arrays.fill(obligatorietats, true);
				bloc.setObligatorietats(obligatorietats);
				blocList.add(bloc);
			}
		}
		expedientDocumentService.enviarPortasignatures(
				documentDto,
				annexos,
				usuariActual, //persona,			
				blocList,
				expedientDto,//expedientRepository.findOne(expedientId),
				command.getPortafirmesPrioritatTipus() != null ? command.getPortafirmesPrioritatTipus().toString() : PortafirmesPrioritatEnumDto.NORMAL.toString(),//importancia
				null, //dataLimit,
				null,//tokenId
				Long.valueOf(processInstanceId),
				null, //transicioOK,
				null, //transicioKO,
				command.getPortafirmesResponsables() != null ? command.getPortafirmesResponsables().split(",") : null,
				command.getPortafirmesSequenciaTipus(),
				command.getPortafirmesFluxId());
	}
	
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/portasignaturesCancelarEnviament/{documentId}", method = RequestMethod.POST)
	public String portasignaturesCancelarEnviament(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			@PathVariable Integer documentId,//${psignaPendentActual.documentId}
			DocumentExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("document", document);	
			model.addAttribute("expedientId", expedientId);
			model.addAttribute("documentExpedientCommand", command);
			model.addAttribute("potFirmar", true);
			return "v3/expedientDocumentForm";
		}
		
		try {
			PortasignaturesDto psignaPendentActual = expedientDocumentService.getPortasignaturesByDocumentId(documentId);
			expedientDocumentService.portafirmesCancelar(psignaPendentActual.getDocumentId(), psignaPendentActual.getId());
			model.addAttribute("documentExpedientCommand", command);
			MissatgesHelper.success(
					request, 
					getMessage(
							request,"expedient.document.portasignatures.enviament.cancelat"));
			return modalUrlTancar(false);
		} catch(Exception e) {
			String errMsg = getMessage(request, "expedient.document.portasignatures.enviament.cancelat.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		model.addAttribute("pipellaActiva", "documents");
		return "v3/expedientDocumentForm";
	}
	
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/reintentarEnviamentPortasignatures", method = RequestMethod.POST)
	public String reintentarEnviamentPortasignatures(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			DocumentExpedientEnviarPortasignaturesCommand command,
			BindingResult bindingResult,
			Model model) {
		
		ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("document", document);	
			model.addAttribute("expedientId", expedientId);
			model.addAttribute("documentExpedientCommand", command);
			model.addAttribute("potFirmar", true);
		}
		
		try {
			/*@TODO: l'únic reprocessament que hem de fer és quan han acceptat la firma recuperar, 
			 * guardar el document firmat i continuar quan hi ha hagut error, no quan está pendent*/
			this.enviarPortasignatures(command, documentStoreId, expedientId,  processInstanceId);
			MissatgesHelper.success(request, getMessage(request, "expedient.document.portasignatures.enviament.reprocessar.ok"));				
		} catch(Exception e) {
			String errMsg = getMessage(request, "expedient.document.portasignatures.enviament.reprocessar.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);				
		}
		
    	return "v3/expedientDocumentForm";
	}

	
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/enviarPortasignatures/iniciarTransaccio", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto iniciarTransaccio(
			HttpServletRequest request,
			@RequestParam(value = "nom", required = false) String nom,
			@RequestParam(value = "plantillaId", required = false) String plantillaId,
			@PathVariable Long expedientId, 
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) throws UnsupportedEncodingException {
		String urlReturn;
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			urlReturn =  request.getContextPath() + "/v3/expedient/" +expedient.getId();
			if (plantillaId != null && !plantillaId.isEmpty()) {
				transaccioResponse = new PortafirmesIniciFluxRespostaDto();
				String urlEdicio = portafirmesFluxService.recuperarUrlEdicioPlantilla(plantillaId, urlReturn);
				transaccioResponse.setUrlRedireccio(urlEdicio);
				model.addAttribute("urlEdicio", urlEdicio);
			} else {
				transaccioResponse = portafirmesFluxService.iniciarFluxFirma(urlReturn, true);
			}
		} catch (Exception ex) {
			logger.error("Error al iniciar transacio", ex);
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}
		return transaccioResponse;
	}
	

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/tancarTransaccio/{idTransaccio}", method = RequestMethod.GET)
	@ResponseBody
	public void tancarTransaccio(
			HttpServletRequest request, 
			@PathVariable String idTransaccio,
			@PathVariable Long expedientId, 
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		portafirmesFluxService.tancarTransaccio(idTransaccio);
	}
	

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/flux/plantilles", method = RequestMethod.GET)
	@ResponseBody
	public List<PortafirmesFluxRespostaDto> getPlantillesDisponibles(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {		
		List<PortafirmesFluxRespostaDto> resposta = portafirmesFluxService.recuperarPlantillesDisponibles(false);
		return resposta;
	}
	

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/flux/returnurl/{transactionId}", method = RequestMethod.GET)
	public String transaccioEstat(
			HttpServletRequest request, 
			@PathVariable String transactionId, 
			@PathVariable Long expedientId, 
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
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
		return "portafirmesModalTancar";
	}


	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/flux/returnurl/", method = RequestMethod.GET)
	public String transaccioEstat(HttpServletRequest request, Model model) {
		model.addAttribute(
				"FluxCreat",
				getMessage(request, "metadocument.form.camp.portafirmes.flux.edicio.enum.FINAL_OK"));
		return "portafirmesModalTancar";
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientDocumentController.class);

}
