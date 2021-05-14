package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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

import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
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
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ServeiTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;
import net.conselldemallorca.helium.v3.core.api.service.PortasignaturesService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Create;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Update;
import net.conselldemallorca.helium.webapp.v3.command.DocumentNotificacioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NtiHelper;

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
	@Autowired
	private PortasignaturesService portasignaturesService;
	@Autowired
	private NtiHelper ntiHelper;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;

	/** comparador per ordenar documents per codi de document primer i per títol de l'adjunt si són adjunts després.
	 *
	 */
	protected class  ExpedientDocumentDtoComparador implements Comparator<ExpedientDocumentDto>{

		/** Compara primer per codi de document i si són annexs i no en tenen llavors per títol de l'annex. */
		@Override
		public int compare(ExpedientDocumentDto doc1, ExpedientDocumentDto doc2) {
			int ret;
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
			return ret;
		}
	}

	@RequestMapping(value = "/{expedientId}/document", method = RequestMethod.GET)
	public String document(
			HttpServletRequest request,
			@PathVariable Long expedientId,
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
				Collections.sort(documentsInstancia, new ExpedientDocumentDtoComparador());	
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
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);

		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
		List<ExpedientDocumentDto> documentsProces = expedientDocumentService.findAmbInstanciaProces(
				expedientId,
				processInstanceId);
		// Ordena els documents per codi de document i si són annexos i no en tenen llavors van darrera ordenats per nom
		Collections.sort(documentsProces, new ExpedientDocumentDtoComparador());
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
			byte[] arxiuContingut = command.getArxiu().getBytes();
			String arxiuContentType = command.getArxiu().getContentType();
			byte[] firmaContingut = null;
			if (command.getFirma() != null && command.getFirma().getSize() > 0) {
				firmaContingut = command.getFirma().getBytes();
			}
			String arxiuNom = command.getArxiu().getOriginalFilename();
			String documentCodi = null;
			if (!DocumentExpedientCommand.ADJUNTAR_ARXIU_CODI.equalsIgnoreCase(command.getDocumentCodi())) {
				documentCodi = command.getDocumentCodi();
			}
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
			try {
				byte[] arxiuContingut = command.getArxiu().getBytes();
				String arxiuNom = command.getArxiu().getOriginalFilename();
				String arxiuContentType = command.getArxiu().getContentType();
				if(arxiuContingut == null || arxiuContingut.length == 0) {
					ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocument(expedientId, processInstanceId, documentStoreId);
					arxiuContingut = arxiu.getContingut();
					arxiuNom = arxiu.getNom();
				}
				byte[] firmaContingut = null;
				if (command.getFirma() != null && command.getFirma().getSize() > 0) {
					firmaContingut = command.getFirma().getBytes();
				}
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
		model.addAttribute("documentNotificacioCommand", command);
		
		ExpedientDocumentDto document = this.emplenarModelNotificacioDocument(expedientId, processInstanceId, documentStoreId, model);
		
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
			DadesNotificacioDto dadesNotificacioDto = conversioTipusHelper.convertir(
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

		return document;
	}
	
	@ModelAttribute("entregaPostalViaTipusEstats")
	public List<ParellaCodiValorDto> populateEntregaPostalViaTipusEstat(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.ALAMEDA"), EntregaPostalViaTipus.ALAMEDA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.CALLE"), EntregaPostalViaTipus.CALLE));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.CAMINO"), EntregaPostalViaTipus.CAMINO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.CARRER"), EntregaPostalViaTipus.CARRER));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.CARRETERA"), EntregaPostalViaTipus.CARRETERA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.GLORIETA"), EntregaPostalViaTipus.GLORIETA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.KALEA"), EntregaPostalViaTipus.KALEA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.PASAJE"), EntregaPostalViaTipus.PASAJE));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.PASEO"), EntregaPostalViaTipus.PASEO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.PLAÇA"), EntregaPostalViaTipus.PLAÇA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.PLAZA"), EntregaPostalViaTipus.PLAZA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.RAMBLA"), EntregaPostalViaTipus.RAMBLA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.RONDA"), EntregaPostalViaTipus.RONDA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.RUA"), EntregaPostalViaTipus.RUA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.SECTOR"), EntregaPostalViaTipus.SECTOR));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.TRAVESIA"), EntregaPostalViaTipus.TRAVESIA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.URBANIZACION"), EntregaPostalViaTipus.URBANIZACION));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.AVENIDA"), EntregaPostalViaTipus.AVENIDA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.AVINGUDA"), EntregaPostalViaTipus.AVINGUDA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.BARRIO"), EntregaPostalViaTipus.BARRIO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.CALLEJA"), EntregaPostalViaTipus.CALLEJA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.CAMI"), EntregaPostalViaTipus.CAMI));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.CAMPO"), EntregaPostalViaTipus.CAMPO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.CARRERA"), EntregaPostalViaTipus.CARRERA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.CUESTA"), EntregaPostalViaTipus.CUESTA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.EDIFICIO"), EntregaPostalViaTipus.EDIFICIO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.ENPARANTZA"), EntregaPostalViaTipus.ENPARANTZA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.ESTRADA"), EntregaPostalViaTipus.ESTRADA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.JARDINES"), EntregaPostalViaTipus.JARDINES));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.JARDINS"), EntregaPostalViaTipus.JARDINS));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.PARQUE"), EntregaPostalViaTipus.PARQUE));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.PASSEIG"), EntregaPostalViaTipus.PASSEIG));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.PRAZA"), EntregaPostalViaTipus.PRAZA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.PLAZUELA"), EntregaPostalViaTipus.PLAZUELA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.PLACETA"), EntregaPostalViaTipus.PLACETA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.POBLADO"), EntregaPostalViaTipus.POBLADO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.VIA"), EntregaPostalViaTipus.VIA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.TRAVESSERA"), EntregaPostalViaTipus.TRAVESSERA));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.PASSATGE"), EntregaPostalViaTipus.PASSATGE));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.BULEVAR"), EntregaPostalViaTipus.BULEVAR));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.POLIGONO"), EntregaPostalViaTipus.POLIGONO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.via.tipus.enum.OTROS"), EntregaPostalViaTipus.OTROS));

		return resposta;
	}
	
	
	
	@ModelAttribute("entregaPostalTipusEstats")
	public List<ParellaCodiValorDto> populatePostaTipusEstat(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.tipus.enum.NACIONAL"), EntregaPostalTipus.NACIONAL));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.enum.ESTRANGER"), EntregaPostalTipus.ESTRANGER));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.enum.APARTAT_CORREUS"), EntregaPostalTipus.APARTAT_CORREUS));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.entregaPostal.enum.SENSE_NORMALITZAR"), EntregaPostalTipus.SENSE_NORMALITZAR));
		return resposta;
	}
	
	
	
	
	@ModelAttribute("serveiTipusEstats")
	public List<ParellaCodiValorDto> populateEstats(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.servei.tipus.enum.NORMAL"), ServeiTipusEnumDto.NORMAL));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.servei.tipus.enum.URGENT"), ServeiTipusEnumDto.URGENT));
		return resposta;
	}

	@ModelAttribute("enviamentTipusEstats")
	public List<ParellaCodiValorDto> populateEnviamentTipus(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.enviament.tipus.enum.COMUNICACIO"), EnviamentTipusEnumDto.COMUNICACIO));
		resposta.add(new ParellaCodiValorDto(getMessage(request, "notifica.enviament.tipus.enum.NOTIFICACIO"), EnviamentTipusEnumDto.NOTIFICACIO));
		return resposta;
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

	
	
	@RequestMapping(value = "/{expedientId}/expedientNotificacions", method = RequestMethod.GET)
	public String notificacions(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
//		expedientId=new Long("1100");
		List<DadesNotificacioDto> notificacions = expedientService.findNotificacionsNotibPerExpedientId(expedientId);
		model.addAttribute("expedient", expedient);
		model.addAttribute("notificacions", notificacions);

		return "v3/notificacioLlistat";
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

	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentCodi}/generar")
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
			MissatgesHelper.error(request, getMessage(request, "error.generar.document"));
			logger.error("Error generant el document: " + documentCodi, ex);
			ExpedientDocumentDto document = expedientDocumentService.findOneAmbInstanciaProces(
					expedientId,
					processInstanceId,
					documentCodi);
			return "redirect:/modal/v3/expedient/" + expedientId + "/proces/" + processInstanceId + "/document/" + document.getId() + "/modificar";
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
		if (portasignaturesService.processarDocumentCallback(psignaId, false, null))
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

	private static final Log logger = LogFactory.getLog(ExpedientDocumentController.class);

}
