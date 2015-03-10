/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.core.model.dao.PluginSignaturaDao;
import net.conselldemallorca.helium.core.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.util.DocumentTokenUtils;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.OpenOfficeUtils;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentConvertirException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentDescarregarException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentGenerarException;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.FirmaTascaRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar els documents dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentHelperV3 {

	public static final String PREFIX_VAR_DOCUMENT = "H3l1um#document.";
	public static final String PREFIX_ADJUNT = "H3l1um#adjunt.";
	public static final String PREFIX_SIGNATURA = "H3l1um#signatura.";
	
	@Resource
	private PlantillaHelper plantillaHelper;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private PluginCustodiaDao pluginCustodiaDao;
	@Resource
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	@Resource
	private PluginSignaturaDao pluginSignaturaDao;
	@Resource
	private FirmaTascaRepository firmaTascaRepository;
	@Resource
	private OpenOfficeUtils openOfficeUtils;

	private PdfUtils pdfUtils;
	private DocumentTokenUtils documentTokenUtils;
	
	public ArxiuDto getArxiuPerDocumentStoreId(
			Long documentStoreId,
			boolean perSignar,
			boolean ambSegellSignatura) {
		ArxiuDto resposta = new ArxiuDto();
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		// Obtenim el contingut de l'arxiu
		byte[] arxiuOrigenContingut = null;
		if (documentStore.isSignat() && isSignaturaFileAttached()) {
			try {
				arxiuOrigenContingut = pluginHelper.custodiaObtenirSignaturesAmbArxiu(documentStore.getReferenciaCustodia());
			} catch (Exception ex) {
				throw new DocumentDescarregarException("Error al obtenir l'arxiu de la custòdia (id=" + documentStoreId + ", processInstanceId=" + documentStore.getProcessInstanceId() + ")", ex);
			}
		} else {
			if (documentStore.getFont().equals(DocumentFont.INTERNA)) {
				arxiuOrigenContingut = documentStore.getArxiuContingut();
			} else {
				try {
					arxiuOrigenContingut = pluginHelper.gestioDocumentalObtenirDocument(
							documentStore.getReferenciaFont());
				} catch (Exception ex) {
					throw new DocumentDescarregarException("Error al obtenir l'arxiu de la gestió documental (id=" + documentStoreId + ", processInstanceId=" + documentStore.getProcessInstanceId() + ")", ex);
				}
			}
		}
		// Calculam el nom de l'arxiu
		String arxiuNomOriginal = calcularArxiuNomOriginal(documentStore);
		String extensioDesti = calcularArxiuExtensioDesti(
				arxiuNomOriginal,
				documentStore,
				perSignar);
		// Només podem convertir a extensió de destí PDF
		if ("pdf".equalsIgnoreCase(extensioDesti)) {
			resposta.setNom(
					getNomArxiuAmbExtensio(
							documentStore.getArxiuNom(),
							extensioDesti));
			// Si és un PDF podem estampar
			try {
				ByteArrayOutputStream vistaContingut = new ByteArrayOutputStream();
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				String dataRegistre = null;
				if (documentStore.getRegistreData() != null)
					dataRegistre = df.format(documentStore.getRegistreData());
				String numeroRegistre = documentStore.getRegistreNumero();
				String urlComprovacioSignatura = null;
				if (ambSegellSignatura)
					urlComprovacioSignatura = getUrlComprovacioSignatura(documentStoreId);
				try {
					getPdfUtils().estampar(
							arxiuNomOriginal,
							arxiuOrigenContingut,
							(ambSegellSignatura) ? !documentStore.isSignat() : false,
							urlComprovacioSignatura,
							documentStore.isRegistrat(),
							numeroRegistre,
							dataRegistre,
							documentStore.getRegistreOficinaNom(),
							documentStore.isRegistreEntrada(),
							vistaContingut,
							extensioDesti);
				} catch (Exception ex) {
					throw new Exception(ex);
				}
				resposta.setContingut(vistaContingut.toByteArray());
			} catch (Exception ex) {
				throw new DocumentDescarregarException("No s'ha pogut generar la vista pel document (id=" + documentStoreId + ", processInstanceId=" + documentStore.getProcessInstanceId() + ")", ex);
			}
		} else {
			// Si no és un pdf retornam la vista directament
			resposta.setNom(arxiuNomOriginal);
			resposta.setContingut(arxiuOrigenContingut);
		}
		return resposta;
	}

	public List<ExpedientDocumentDto> findDocumentsPerInstanciaProces(
			String processInstanceId) {
		String tipusExp = null;
		if (MesuresTemporalsHelper.isActiu()) {
			Expedient exp = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			tipusExp = (exp != null ? exp.getTipus().getNom() : null);
			mesuresTemporalsHelper.mesuraIniciar("Expedient DOCUMENTS v3", "expedient", tipusExp);
			mesuresTemporalsHelper.mesuraIniciar("Expedient DOCUMENTS v3", "expedient", tipusExp, null, "0");
		}
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3", "expedient", tipusExp, null, "0");
		mesuresTemporalsHelper.mesuraIniciar("Expedient DOCUMENTS v3", "expedient", tipusExp, null, "1");
		List<Document> documents = documentRepository.findByDefinicioProces(definicioProces);
		List<ExpedientDocumentDto> resposta = convertDocumentDto(documents, processInstanceId, tipusExp);
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3", "expedient", tipusExp, null, "1");
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3", "expedient",tipusExp);
		return resposta;
	}
	
	public List<ExpedientDocumentDto> convertDocumentDto(List<Document> documents, String processInstanceId, String tipusExp) {
		mesuresTemporalsHelper.mesuraIniciar("Expedient DOCUMENTS v3 convertDocumentDto", "expedient", tipusExp);
		
		mesuresTemporalsHelper.mesuraIniciar("Expedient DOCUMENTS v3 convertDocumentDto", "expedient", tipusExp, null, "0");
		Map<String, Document> documentsIndexatsPerCodi = new HashMap<String, Document>();
		Map<String, Object> varsInstanciaProces = jbpmHelper.getProcessInstanceVariables(processInstanceId);
		for (Document document: documents)
			documentsIndexatsPerCodi.put(document.getCodi(), document);
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3 convertDocumentDto", "expedient", tipusExp, null, "0");
		
		List<ExpedientDocumentDto> resposta = new ArrayList<ExpedientDocumentDto>();		
				
		if (varsInstanciaProces != null) {
			mesuresTemporalsHelper.mesuraIniciar("Expedient DOCUMENTS v3 convertDocumentDto", "expedient", tipusExp, null, "1");
			filtrarVariablesAmbDocuments(varsInstanciaProces);
			for (String var: varsInstanciaProces.keySet()) {
				Long documentStoreId = (Long)varsInstanciaProces.get(var);
				if (documentStoreId != null) {
					String documentCodi = getDocumentCodiDeVariableJbpm(var);
					if (documentsIndexatsPerCodi.containsKey(documentCodi)) {
						ExpedientDocumentDto dto = toExpedientDocumentDto(
								documentStoreId,
								var.startsWith(VariableHelper.PREFIX_VAR_DOCUMENT),
								documentCodi,
								var.startsWith(VariableHelper.PREFIX_VAR_ADJUNT),
								getAdjuntIdDeVariableJbpm(var),
								documentsIndexatsPerCodi.get(documentCodi));
						resposta.add(dto);
					} else if (var.startsWith(DocumentHelper.PREFIX_ADJUNT)) {
						// Afegeix els adjunts
						resposta.add(toExpedientDocument(
								documentStoreId,
								documentCodi,
								getAdjuntIdDeVariableJbpm(var),
								getDocumentSenseContingut(documentStoreId)));
					}
				}
			}
			mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3 convertDocumentDto", "expedient", tipusExp, null, "1");
		}
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3 convertDocumentDto", "expedient",tipusExp);
		
		return resposta;
	}

	public ExpedientDocumentDto findDocumentPerExpedientDocumentStoreId(Expedient expedient, Long documentStoreId, String docCodi) {
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENT v3", "expedient", expedient.getTipus().getCodi(), null, "0");
		mesuresTemporalsHelper.mesuraIniciar("Expedient DOCUMENT v3", "expedient", expedient.getTipus().getCodi(), null, "1");
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(expedient.getProcessInstanceId());
		DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
		ExpedientDocumentDto resposta = null;
		if (documentStore.isAdjunt()) {
			resposta = toExpedientDocument(
					documentStoreId,
					docCodi,
					getAdjuntIdDeVariableJbpm(documentStore.getJbpmVariable()),
					getDocumentSenseContingut(documentStoreId));
		} else {
			resposta = toExpedientDocumentDto(
					documentStoreId,
					documentStore.getJbpmVariable().startsWith(VariableHelper.PREFIX_VAR_DOCUMENT),
					docCodi,
					documentStore.getJbpmVariable().startsWith(VariableHelper.PREFIX_VAR_ADJUNT),
					getAdjuntIdDeVariableJbpm(documentStore.getJbpmVariable()),
					documentRepository.findByDefinicioProcesAndCodi(definicioProces, docCodi));
		}
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENT v3", "expedient", expedient.getTipus().getCodi(), null, "1");
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENT v3", "expedient",expedient.getTipus().getCodi());
		return resposta;
	}
		
	public TascaDocumentDto findDocumentPerId(String tascaId, Long docId) {
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				true,
				true);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		DocumentTasca documentTasca = documentTascaRepository.findAmbDefinicioProcesITascaJbpmNameDocumentId(
				docId,
				definicioProces.getId(),
				task.getName());
		
		return toTascaDocumentDto(
					task,
					documentTasca.getDocument(), documentTasca.isRequired(), documentTasca.isReadOnly());
	}

	public List<TascaDocumentDto> findDocumentsPerInstanciaTasca(JbpmTask task) {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		List<DocumentTasca> documentsTasca = documentTascaRepository.findAmbDefinicioProcesITascaJbpmNameOrdenats(
				definicioProces.getId(),
				task.getName());
		List<TascaDocumentDto> resposta = new ArrayList<TascaDocumentDto>();
		for (DocumentTasca documentTasca: documentsTasca) {
			resposta.add(toTascaDocumentDto(
					task,
					documentTasca.getDocument(), documentTasca.isRequired(), documentTasca.isReadOnly()));
		}
		return resposta;
	}

	public boolean hasDocumentsPerInstanciaTasca(JbpmTask task) {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		return documentTascaRepository.countAmbDefinicioProcesITascaJbpmName(
				definicioProces.getId(),
				task.getName()) > 0;
	}

	public List<TascaDocumentDto> findDocumentsPerInstanciaTascaSignar(JbpmTask task) {
		List<TascaDocumentDto> resposta = new ArrayList<TascaDocumentDto>();
		for (FirmaTasca firmaTasca: firmaTascaRepository.findAmbTascaOrdenats(task.getName(), task.getProcessDefinitionId())) {
			resposta.add(toTascaDocumentDto(
					task,
					firmaTasca.getDocument(), firmaTasca.isRequired(), false));
		}
		return resposta;
	}

	public boolean hasDocumentsPerInstanciaTascaSignar(JbpmTask task) {
		return firmaTascaRepository.countAmbTasca(task.getName(), task.getProcessDefinitionId()) > 0;
	}

	private ExpedientDocumentDto toExpedientDocument(
			Long documentStoreId,
			String documentCodi,
			String adjuntId,
			DocumentDto document) {
		ExpedientDocumentDto dto = new ExpedientDocumentDto();
		dto.setId(documentStoreId);
		dto.setDocumentCodi(documentCodi);
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore != null) {
			dto.setDataCreacio(documentStore.getDataCreacio());
			dto.setDataModificacio(documentStore.getDataModificacio());
			dto.setDataDocument(documentStore.getDataDocument());
			dto.setSignat(documentStore.isSignat());
			// TODO
			//dto.setPortasignaturesId(portasignaturesId);
			if (documentStore.isSignat()) {
				dto.setDocumentPendentSignar(documentStore.isSignat());
				dto.setUrlVerificacioCustodia(
						pluginCustodiaDao.getUrlComprovacioSignatura(
								documentStoreId.toString()));
			}
			try {
				dto.setTokenSignatura(getDocumentTokenUtils().xifrarToken(documentStoreId.toString()));
			} catch (Exception ex) {
				logger.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
			}
			try {
				dto.setSignaturaUrlVerificacio(getUrlComprovacioSignatura(documentStoreId));
			} catch (Exception ex) {
				dto.setError("No s'ha pogut obtenir la URL de comprovació de signatura (id=" + documentStoreId + ",documentCodi=" + documentCodi + ")");
				logger.error("No s'ha pogut obtenir la URL de comprovació de signatura (id=" + documentStoreId + ",documentCodi=" + documentCodi + ")", ex);
			}
			dto.setRegistrat(documentStore.isRegistrat());
			dto.setRegistreEntrada(documentStore.isRegistreEntrada());
			dto.setRegistreNumero(documentStore.getRegistreNumero());
			dto.setRegistreData(documentStore.getRegistreData());
			dto.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
			dto.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
			dto.setProcessInstanceId(documentStore.getProcessInstanceId());
			dto.setDocumentId(document.getId());
			dto.setDocumentNom(document.getAdjuntTitol());
			dto.setDocumentContentType(document.getContentType());
			dto.setDocumentCustodiaCodi(document.getCustodiaCodi());
			dto.setDocumentTipusDocPortasignatures(document.getTipusDocPortasignatures());
			dto.setAdjunt(true);
			dto.setAdjuntId(adjuntId);
			dto.setAdjuntTitol(documentStore.getAdjuntTitol());
			dto.setArxiuNom(calcularArxiuNom(documentStore, false));
		} else {
			dto.setAdjuntId(adjuntId);
			dto.setAdjunt(true);
			dto.setError("No s'ha trobat el documentStore del adjunt (id=" + documentStoreId + ", adjuntId=" + adjuntId + ")");
		}
		return dto;
	}

	private ExpedientDocumentDto toExpedientDocumentDto(
			Long documentStoreId,
			boolean esDocument,
			String documentCodi,
			boolean esAdjunt,
			String adjuntId,
			Document document) {
		ExpedientDocumentDto dto = new ExpedientDocumentDto();
		dto.setId(documentStoreId);
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore != null) {
			dto.setDataCreacio(documentStore.getDataCreacio());
			dto.setDataModificacio(documentStore.getDataModificacio());
			dto.setDataDocument(documentStore.getDataDocument());
			dto.setSignat(documentStore.isSignat());
			// TODO
			//dto.setPortasignaturesId(portasignaturesId);
			if (documentStore.isSignat()) {
				dto.setDocumentPendentSignar(documentStore.isSignat());
				dto.setUrlVerificacioCustodia(
						pluginCustodiaDao.getUrlComprovacioSignatura(
								documentStoreId.toString()));
			}
			try {
				dto.setTokenSignatura(getDocumentTokenUtils().xifrarToken(documentStoreId.toString()));
			} catch (Exception ex) {
				logger.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
			}
			try {
				dto.setSignaturaUrlVerificacio(getUrlComprovacioSignatura(documentStoreId));
			} catch (Exception ex) {
				dto.setError("No s'ha pogut obtenir la URL de comprovació de signatura (id=" + documentStoreId + ",documentCodi=" + documentCodi + ")");
				logger.error("No s'ha pogut obtenir la URL de comprovació de signatura (id=" + documentStoreId + ",documentCodi=" + documentCodi + ")", ex);
			}
			dto.setRegistrat(documentStore.isRegistrat());
			dto.setRegistreEntrada(documentStore.isRegistreEntrada());
			dto.setRegistreNumero(documentStore.getRegistreNumero());
			dto.setRegistreData(documentStore.getRegistreData());
			dto.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
			dto.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
			dto.setProcessInstanceId(documentStore.getProcessInstanceId());
			if (esDocument) {
				dto.setDocumentCodi(documentCodi);
				if (document != null) {
					dto.setPlantilla(document.isPlantilla());
					dto.setDocumentId(document.getId());
					dto.setDocumentNom(document.getNom());
					dto.setDocumentContentType(document.getContentType());
					dto.setDocumentCustodiaCodi(document.getCustodiaCodi());
					dto.setDocumentTipusDocPortasignatures(document.getTipusDocPortasignatures());
				} else {
					dto.setError("No s'ha trobat el document (id=" + documentStoreId + ",documentCodi=" + documentCodi + ")");
				}
			} else if (esAdjunt) {
				dto.setAdjunt(true);
				dto.setAdjuntId(adjuntId);
				dto.setAdjuntTitol(documentStore.getAdjuntTitol());
			}
			dto.setArxiuNom(calcularArxiuNom(documentStore, false));
		} else {
			if (esDocument) {
				dto.setDocumentCodi(documentCodi);
				dto.setError("No s'ha trobat el documentStore del document (id=" + documentStoreId + ", documentCodi=" + documentCodi + ")");
			} else if (esAdjunt) {
				dto.setAdjuntId(adjuntId);
				dto.setAdjunt(true);
				dto.setError("No s'ha trobat el documentStore del adjunt (id=" + documentStoreId + ", adjuntId=" + adjuntId + ")");
			} else {
				dto.setError("No s'ha trobat el documentStore i no es ni document ni adjunt (id=" + documentStoreId + ")"); 
			}
		}
		if (document != null && !document.getFirmes().isEmpty()) {
			dto.setDocumentPendentSignar(true);
			Iterator<FirmaTasca> firmas = document.getFirmes().iterator();
			while (firmas.hasNext()) {						
				if (firmas.next().isRequired()) {
					dto.setSignatRequired(true);
					break;
				}
			}
		}
		return dto;
	}

	public DocumentStore getDocumentStore(
			JbpmTask task,
			String documentCodi) {
		DocumentStore documentStore = null;
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(String.valueOf(task.getTask().getId()), task.getProcessInstanceId(), documentCodi);
		if (documentStoreId != null) {
			documentStore = documentStoreRepository.findById(documentStoreId);
		}
		return documentStore;
	}
	
	public List<RespostaValidacioSignaturaDto> verificarSignatura(String tascaId, Long docId) throws Exception {
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				tascaId,
				false,
				false);
		
		DocumentStore documentStore = getDocumentStore(task, documentRepository.findOne(docId).getCodi());
		
		return getRespostasValidacioSignatura(documentStore);
	}
	
	public List<RespostaValidacioSignaturaDto> getRespostasValidacioSignatura(DocumentStore documentStore) {
		DocumentDto document = getDocumentOriginal(documentStore.getId(), true);
		if (pluginCustodiaDao.potObtenirInfoSignatures()) {
			return conversioTipusHelper.convertirList(pluginCustodiaDao.dadesValidacioSignatura(
					documentStore.getReferenciaCustodia()), RespostaValidacioSignaturaDto.class);
		} else if (isSignaturaFileAttached()) {
			List<byte[]> signatures = pluginCustodiaDao.obtenirSignatures(documentStore.getReferenciaCustodia());
			List<RespostaValidacioSignatura> resposta = new ArrayList<RespostaValidacioSignatura>();
			if (!signatures.isEmpty()) {
				RespostaValidacioSignatura res = pluginSignaturaDao.verificarSignatura(
						null,
						signatures.get(0),
						true);
				resposta.add(res);
			}
			return conversioTipusHelper.convertirList(resposta, RespostaValidacioSignaturaDto.class);
		} else {
			List<RespostaValidacioSignatura> resposta = new ArrayList<RespostaValidacioSignatura>();
			List<byte[]> signatures = pluginCustodiaDao.obtenirSignatures(
					documentStore.getReferenciaCustodia());
			for (byte[] signatura: signatures) {
				RespostaValidacioSignatura res = pluginSignaturaDao.verificarSignatura(
						document.getArxiuContingut(),
						signatura,
						true);
				resposta.add(res);
			}
			return conversioTipusHelper.convertirList(resposta, RespostaValidacioSignaturaDto.class);
		}
	}
	
	private TascaDocumentDto toTascaDocumentDto(
			JbpmTask task,
			Document document, 
			boolean required, 
			boolean readonly) {
		TascaDocumentDto dto = new TascaDocumentDto();
		String varCodi = getVarPerDocumentCodi(
				document.getCodi(),
				document.isAdjuntarAuto());
		dto.setId(document.getId());
		dto.setVarCodi(varCodi);
		dto.setDocumentCodi(document.getCodi());
		dto.setDocumentNom(document.getNom());
		dto.setRequired(required);
		dto.setReadOnly(readonly);
		dto.setPlantilla(document.isPlantilla());
		dto.setExtensionsPermeses(document.getExtensionsPermeses());
		dto.setAdjuntarAuto(document.isAdjuntarAuto());
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(String.valueOf(task.getTask().getId()), task.getProcessInstanceId(), document.getCodi());
		if (documentStoreId != null) {
			DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
			if (documentStore != null) {
				dto.setDocumentStoreId(documentStoreId);
				dto.setArxiuNom(documentStore.getArxiuNom());
				dto.setDataCreacio(documentStore.getDataCreacio());
				dto.setDataModificacio(documentStore.getDataModificacio());
				dto.setDataDocument(documentStore.getDataDocument());
				dto.setSignat(documentStore.isSignat());
				dto.setRegistrat(documentStore.isRegistrat());
				if (documentStore.isSignat()) {
					dto.setUrlVerificacioCustodia(
							pluginCustodiaDao.getUrlComprovacioSignatura(
									documentStoreId.toString()));
				}
				try {
					dto.setTokenSignatura(getDocumentTokenUtils().xifrarToken(documentStoreId.toString()));
				} catch (Exception ex) {
					logger.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
				}
				if (documentStore.isRegistrat()) {
					dto.setRegistreData(documentStore.getRegistreData());
					dto.setRegistreNumero(documentStore.getRegistreNumero());
					dto.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
					dto.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
					dto.setRegistreEntrada(documentStore.isRegistreEntrada());
				}
			}
		}	
		return dto;
	}

	private String calcularArxiuNom(
			DocumentStore documentStore,
			boolean perSignar) {
		String nomOriginal = calcularArxiuNomOriginal(documentStore);
		String extensioDesti = calcularArxiuExtensioDesti(
				nomOriginal,
				documentStore,
				perSignar);
		return getNomArxiuAmbExtensio(
				documentStore.getArxiuNom(),
				extensioDesti);
	}
	private String calcularArxiuNomOriginal(
			DocumentStore documentStore) {
		String nomOriginal;
		if (documentStore.isSignat() && isSignaturaFileAttached()) {
			nomOriginal = getNomArxiuAmbExtensio(
					documentStore.getArxiuNom(),
					getExtensioArxiuSignat());
		} else {
			nomOriginal = documentStore.getArxiuNom();
		}
		return nomOriginal;
	}
	private String calcularArxiuExtensioDesti(
			String nomOriginal,
			DocumentStore documentStore,
			boolean perSignar) {
		String extensioActual = null;
		int indexPunt = nomOriginal.lastIndexOf(".");
		if (indexPunt != -1)
			extensioActual = nomOriginal.substring(indexPunt + 1);
		String extensioDesti = extensioActual;
		if (perSignar && isActiuConversioSignatura()) {
			extensioDesti = getExtensioArxiuSignat();
		} else if (documentStore.isRegistrat()) {
			extensioDesti = getExtensioArxiuRegistrat();
		}
		return extensioDesti;
	}

	private String getDocumentCodiDeVariableJbpm(String varName) {
		return varName.substring(VariableHelper.PREFIX_VAR_DOCUMENT.length());
	}
	private String getAdjuntIdDeVariableJbpm(String varName) {
		return varName.substring(VariableHelper.PREFIX_VAR_ADJUNT.length());
	}

	private void filtrarVariablesAmbDocuments(Map<String, Object> variables) {
		if (variables != null) {
			variables.remove(TascaService.VAR_TASCA_VALIDADA);
			variables.remove(TascaService.VAR_TASCA_DELEGACIO);
			List<String> codisEsborrar = new ArrayList<String>();
			for (String codi: variables.keySet()) {
				if (!codi.startsWith(VariableHelper.PREFIX_VAR_DOCUMENT) && !codi.startsWith(VariableHelper.PREFIX_VAR_ADJUNT)) {
					codisEsborrar.add(codi);
				}
			}
			for (String codi: codisEsborrar)
				variables.remove(codi);
		}
	}

	private String getNomArxiuAmbExtensio(
			String arxiuNomOriginal,
			String extensio) {
		if (!isActiuConversioSignatura())
			return arxiuNomOriginal;
		if (extensio == null)
			extensio = "";
		int indexPunt = arxiuNomOriginal.lastIndexOf(".");
		if (indexPunt != -1) {
			return arxiuNomOriginal.substring(0, indexPunt) + "." + extensio;
		} else {
			return arxiuNomOriginal + "." + extensio;
		}
	}

	private String getUrlComprovacioSignatura(Long documentStoreId) throws Exception {
		String urlCustodia = pluginHelper.custodiaObtenirUrlComprovacioSignatura(documentStoreId.toString());
		if (urlCustodia != null) {
			return urlCustodia;
		} else {
			String baseUrl = (String)GlobalProperties.getInstance().get("app.base.verificacio.url");
			if (baseUrl == null)
				baseUrl = (String)GlobalProperties.getInstance().get("app.base.url");
			String token = getDocumentTokenUtils().xifrarToken(documentStoreId.toString());
			return baseUrl + "/signatura/verificarExtern.html?token=" + token;
		}
	}

	private String getExtensioArxiuSignat() {
		return (String)GlobalProperties.getInstance().get("app.conversio.signatura.extension");
	}
	private String getExtensioArxiuRegistrat() {
		return (String)GlobalProperties.getInstance().get("app.conversio.registre.extension");
	}
	private boolean isSignaturaFileAttached() {
		return "true".equalsIgnoreCase((String)GlobalProperties.getInstance().get("app.signatura.plugin.file.attached"));
	}
	private boolean isActiuConversioSignatura() {
		String actiuConversio = (String)GlobalProperties.getInstance().get("app.conversio.actiu");
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioSignatura = (String)GlobalProperties.getInstance().get("app.conversio.signatura.actiu");
		return "true".equalsIgnoreCase(actiuConversioSignatura);
	}

	private PdfUtils getPdfUtils() {
		if (pdfUtils == null)
			pdfUtils = new PdfUtils();
		return pdfUtils;
	}
	private DocumentTokenUtils getDocumentTokenUtils() {
		if (documentTokenUtils == null)
			documentTokenUtils = new DocumentTokenUtils(
					(String)GlobalProperties.getInstance().get("app.encriptacio.clau"));
		return documentTokenUtils;
	}

	public DocumentDto signarDocumentTascaAmbToken(Long docId, String tascaId, String token, byte[] signatura) throws Exception {
		DocumentDto dto = null;
		Long documentStoreId = getDocumentStoreIdPerToken(token);		
		if (documentStoreId != null) {
			DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
			dto = getDocumentSenseContingut(documentStoreId);			
			boolean custodiat = false;
			if (pluginCustodiaDao.isCustodiaActiu()) {
				String nomArxiu = getNomArxiuAmbExtensio(
						dto.getArxiuNom(),
						getExtensioArxiuSignat());		
				String referenciaCustodia = null;
				if (pluginCustodiaDao.isValidacioImplicita()) {
					logger.info("signarDocumentTascaAmbToken : documentId: " + documentStore.getId() + ". gesdocId: " +documentStore.getReferenciaFont() + ". nomArxiuSignat: " + nomArxiu + ". codiTipusCustodia: " + dto.getCustodiaCodi()); 
					referenciaCustodia = pluginCustodiaDao.afegirSignatura(
							documentStore.getId(),
							documentStore.getReferenciaFont(),
							nomArxiu,
							dto.getCustodiaCodi(),
							signatura);
					custodiat = true;
				} else {
					RespostaValidacioSignatura resposta = pluginSignaturaDao.verificarSignatura(
							dto.getVistaContingut(),
							signatura,
							false);
					if (resposta.isEstatOk()) {
						referenciaCustodia = pluginCustodiaDao.afegirSignatura(
								documentStore.getId(),
								documentStore.getReferenciaFont(),
								nomArxiu,
								dto.getCustodiaCodi(),
								signatura);
						custodiat = true;
					}
				}
				documentStore.setReferenciaCustodia(referenciaCustodia);
			}
			if (custodiat) {
				documentStore.setSignat(true);
				JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
						tascaId,
						true,
						true);
				String.valueOf(task.getTask().getId());
				jbpmHelper.setTaskInstanceVariable(
						tascaId,
						DocumentHelper.PREFIX_SIGNATURA + dto.getDocumentCodi(),
						documentStore.getId());
			}
		}
		return dto;
	}

	private DocumentDto toDocumentDto(
			Long documentStoreId,
			boolean ambContingutOriginal,
			boolean ambContingutSignat,
			boolean ambContingutVista,
			boolean perSignar,
			boolean ambSegellSignatura) {
		if (documentStoreId != null) {
			DocumentStore document = documentStoreRepository.findOne(documentStoreId);
			if (document != null) {
				DocumentDto dto = new DocumentDto();
				dto.setId(document.getId());
				dto.setDataCreacio(document.getDataCreacio());
				dto.setDataDocument(document.getDataDocument());
				dto.setArxiuNom(document.getArxiuNom());
				dto.setProcessInstanceId(document.getProcessInstanceId());
				dto.setSignat(document.isSignat());
				dto.setAdjunt(document.isAdjunt());
				dto.setAdjuntTitol(document.getAdjuntTitol());
				try {
					dto.setTokenSignatura(getDocumentTokenUtils().xifrarToken(documentStoreId.toString()));
				} catch (Exception ex) {
					logger.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
				}
				if (document.isSignat()) {
					dto.setUrlVerificacioCustodia(
							pluginCustodiaDao.getUrlComprovacioSignatura(
									documentStoreId.toString()));
				}
				String codiDocument;
				if (document.isAdjunt()) {
					dto.setAdjuntId(document.getJbpmVariable().substring(PREFIX_ADJUNT.length()));
					dto.setCodi(dto.getAdjuntId());
					dto.setDocumentCodi(dto.getAdjuntId());
					dto.setDocumentNom(document.getAdjuntTitol());
					dto.setArxiuContingut(document.getArxiuContingut());
				} else {
					codiDocument = document.getJbpmVariable().substring(PREFIX_VAR_DOCUMENT.length());
					JbpmProcessDefinition jpd = jbpmHelper.findProcessDefinitionWithProcessInstanceId(document.getProcessInstanceId());
					DefinicioProces definicioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
							jpd.getKey(),
							jpd.getVersion());
					Document doc = documentRepository.findAmbDefinicioProcesICodi(definicioProces.getId(), codiDocument);
					if (doc != null) {
						dto.setContentType(doc.getContentType());
						dto.setCustodiaCodi(doc.getCustodiaCodi());
						dto.setDocumentId(doc.getId());
						dto.setDocumentCodi(doc.getCodi());
						dto.setDocumentNom(doc.getNom());
						dto.setTipusDocPortasignatures(doc.getTipusDocPortasignatures());
						dto.setAdjuntarAuto(doc.isAdjuntarAuto());
					}
				}
				if (ambContingutOriginal) {
					dto.setArxiuContingut(
							getContingutDocumentAmbFont(document));
				}
				if (ambContingutSignat && document.isSignat() && isSignaturaFileAttached()) {
					dto.setSignatNom(
							getNomArxiuAmbExtensio(
									document.getArxiuNom(),
									getExtensioArxiuSignat()));
					byte[] signatura = pluginCustodiaDao.obtenirSignaturesAmbArxiu(document.getReferenciaCustodia());
					dto.setSignatContingut(signatura);
				}
				if (ambContingutVista) {
					String arxiuOrigenNom;
					byte[] arxiuOrigenContingut;
					// Obtenim l'origen per a generar la vista o bé del document original
					// o bé del document signat
					if (document.isSignat() && isSignaturaFileAttached()) {
						if (ambContingutSignat) {
							arxiuOrigenNom = dto.getSignatNom();
							arxiuOrigenContingut = dto.getSignatContingut();
						} else {
							arxiuOrigenNom = getNomArxiuAmbExtensio(
									document.getArxiuNom(),
									getExtensioArxiuSignat());
							arxiuOrigenContingut = pluginCustodiaDao.obtenirSignaturesAmbArxiu(document.getReferenciaCustodia());
						}
					} else {
						arxiuOrigenNom = dto.getArxiuNom();
						if (ambContingutOriginal) {
							arxiuOrigenContingut = dto.getArxiuContingut();
						} else {
							if (document.getFont().equals(DocumentFont.INTERNA)) {
								arxiuOrigenContingut = document.getArxiuContingut();
							} else {
								arxiuOrigenContingut = pluginGestioDocumentalDao.retrieveDocument(
												document.getReferenciaFont());
							}
						}
					}
					// Calculam l'extensió del document final de la vista
					String extensioActual = null;
					int indexPunt = arxiuOrigenNom.indexOf(".");
					if (indexPunt != -1)
						extensioActual = arxiuOrigenNom.substring(0, indexPunt);
					String extensioDesti = extensioActual;
					if (perSignar && isActiuConversioSignatura()) {
						extensioDesti = getExtensioArxiuSignat();
					} else if (document.isRegistrat()) {
						extensioDesti = getExtensioArxiuRegistrat();
					}
					dto.setVistaNom(dto.getArxiuNomSenseExtensio() + "." + extensioDesti);
					if ("pdf".equalsIgnoreCase(extensioDesti)) {
						// Si és un PDF podem estampar
						try {
							ByteArrayOutputStream vistaContingut = new ByteArrayOutputStream();
							DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
							String dataRegistre = null;
							if (document.getRegistreData() != null)
								dataRegistre = df.format(document.getRegistreData());
							String numeroRegistre = document.getRegistreNumero();
							try {
								getPdfUtils().estampar(
										arxiuOrigenNom,
										arxiuOrigenContingut,
										(ambSegellSignatura) ? !document.isSignat() : false,
										(ambSegellSignatura) ? getUrlComprovacioSignatura(documentStoreId, dto.getTokenSignatura()): null,
										document.isRegistrat(),
										numeroRegistre,
										dataRegistre,
										document.getRegistreOficinaNom(),
										document.isRegistreEntrada(),
										vistaContingut,
										extensioDesti);
							} catch (Exception ex) {
								throw new Exception(ex);
							}
							dto.setVistaContingut(vistaContingut.toByteArray());
						} catch (Exception ex) {
							logger.error("No s'ha pogut generar la vista pel document '" + document.getCodiDocument() + "'", ex);
						}
					} else {
						// Si no és un pdf retornam la vista directament
						dto.setVistaNom(arxiuOrigenNom);
						dto.setVistaContingut(arxiuOrigenContingut);
					}
				}
				if (document.isRegistrat()) {
					dto.setRegistreData(document.getRegistreData());
					dto.setRegistreNumero(document.getRegistreNumero());
					dto.setRegistreOficinaCodi(document.getRegistreOficinaCodi());
					dto.setRegistreOficinaNom(document.getRegistreOficinaNom());
					dto.setRegistreEntrada(document.isRegistreEntrada());
					dto.setRegistrat(true);
				}
				return dto;
			}
		}
		return null;
	}

	public Long getDocumentStoreIdPerToken(String token) {
		try {
			String[] tokenDesxifrat = getDocumentTokenUtils().desxifrarTokenMultiple(token);
			if (tokenDesxifrat.length == 1)
				return Long.parseLong(tokenDesxifrat[0]);
			else
				return Long.parseLong(tokenDesxifrat[1]);
		} catch (Exception ex) {
			throw new IllegalArgumentsException("Format de token incorrecte", ex);
		}
	}
	
	public DocumentDto getDocumentVista(
			Long documentStoreId,
			boolean perSignar,
			boolean ambSegellSignatura) {
		if (documentStoreId != null) {
			return toDocumentDto(
					documentStoreId,
					false,
					false,
					true,
					perSignar,
					ambSegellSignatura);
		} else {
			return null;
		}
	}
	
	public DocumentDto getDocumentOriginal(
			Long documentStoreId,
			boolean ambContingut) {
		if (documentStoreId != null) {
			return toDocumentDto(
					documentStoreId,
					ambContingut,
					false,
					false,
					false,
					false);
		} else {
			return null;
		}
	}

	private String getUrlComprovacioSignatura(Long documentStoreId, String token) {
		String urlCustodia = pluginCustodiaDao.getUrlComprovacioSignatura(documentStoreId.toString());
		if (urlCustodia != null) {
			return urlCustodia;
		} else {
			String baseUrl = (String)GlobalProperties.getInstance().get("app.base.verificacio.url");
			if (baseUrl == null)
				baseUrl = (String)GlobalProperties.getInstance().get("app.base.url");
			return baseUrl + "/signatura/verificarExtern.html?token=" + token;
		}
	}

	private byte[] getContingutDocumentAmbFont(DocumentStore document) {
		if (document.getFont().equals(DocumentFont.INTERNA))
			return document.getArxiuContingut();
		else
			return pluginGestioDocumentalDao.retrieveDocument(
							document.getReferenciaFont());
	}

	public DocumentDto getDocumentSenseContingut(
			Long documentStoreId) {
		if (documentStoreId != null) {
			DocumentDto dto = toDocumentDto(
					documentStoreId,
					false,
					false,
					false,
					false,
					false);
			return dto;
		} else {
			return null;
		}
	}

	public DocumentDto getDocumentSenseContingut(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			boolean perSignarEnTasca,
			boolean ambInfoPsigna) {
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId, documentCodi);
		if (documentStoreId != null) {
			DocumentDto dto = toDocumentDto(
					documentStoreId,
					false,
					false,
					false,
					false,
					false);
			if (perSignarEnTasca) {
				try {
					dto.setTokenSignaturaMultiple(getDocumentTokenUtils().xifrarTokenMultiple(
							new String[] {
									taskInstanceId,
									documentStoreId.toString()}));
				} catch (Exception ex) {
					logger.error("No s'ha pogut generar el token pel document " + documentStoreId, ex);
				}
				if (dto.isSignat()) {
					Object signatEnTasca = jbpmHelper.getTaskInstanceVariable(taskInstanceId, DocumentHelperV3.PREFIX_SIGNATURA + dto.getDocumentCodi());
					dto.setSignatEnTasca(signatEnTasca != null);
				} else {
					dto.setSignatEnTasca(false);
				}
			}
			return dto;
		} else {
			return null;
		}
	}
	
	public Long actualitzarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			String documentNom,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut,
			boolean isAdjunt) {
		DocumentStore documentStore = null;
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId, documentCodi);
		if (documentStoreId != null)
			documentStore = documentStoreRepository.findById(documentStoreId);
		if (documentStore == null) {
			// Si el document no existeix el crea
			documentStore = new DocumentStore(
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? DocumentFont.ALFRESCO : DocumentFont.INTERNA,
					processInstanceId,
					getVarPerDocumentCodi(documentCodi, isAdjunt),
					new Date(),
					documentData,
					arxiuNom);
			documentStore.setAdjunt(isAdjunt);
			if (isAdjunt)
				documentStore.setAdjuntTitol(documentNom);
			documentStore.setArxiuNom(arxiuNom);
			if (!pluginGestioDocumentalDao.isGestioDocumentalActiu())
				documentStore.setArxiuContingut(arxiuContingut);
			documentStore = documentStoreRepository.save(documentStore);
			documentStoreRepository.flush();
		} else {
			// Si el document està creat l'actualitza
			documentStore = documentStoreRepository.findOne(documentStoreId);
			documentStore.setDataDocument(documentData);
			documentStore.setDataModificacio(new Date());
			if (documentStore.isAdjunt())
				documentStore.setAdjuntTitol(documentNom);
			documentStore.setArxiuNom(arxiuNom);
			if (!pluginGestioDocumentalDao.isGestioDocumentalActiu())
				documentStore.setArxiuContingut(arxiuContingut);
			if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu())
				pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
		}
		// Crea el document a dins la gestió documental
		if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu()) {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			if (documentNom == null) {
				Document document = getDocumentDisseny(taskInstanceId, processInstanceId, documentCodi);
				documentNom = document.getNom();
			}
			String referenciaFont = pluginGestioDocumentalDao.createDocument(
					expedient,
					documentStore.getId().toString(),
					documentNom,
					documentData,
					arxiuNom,
					arxiuContingut);
			documentStore.setReferenciaFont(referenciaFont);
		}
		// Guarda la referència al nou document a dins el jBPM
		if (taskInstanceId != null)
			jbpmHelper.setTaskInstanceVariable(
					taskInstanceId,
					documentStore.getJbpmVariable(),
					documentStore.getId());
		else
			jbpmHelper.setProcessInstanceVariable(
					processInstanceId,
					documentStore.getJbpmVariable(),
					documentStore.getId());
		return documentStore.getId();
	}
	
	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		esborrarDocument(
				taskInstanceId,
				processInstanceId,
				documentCodi,
				null);
	}
	
	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Long documentStoreId) {
		if (documentStoreId == null)
			documentStoreId = getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId, documentCodi);
		if (documentStoreId != null){
			DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
			if (documentStore != null) {
				if (documentStore.isSignat()) {
					if (pluginCustodiaDao.isCustodiaActiu()) {
						try {
							pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
						} catch (PluginException ignored) {}
					}
				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
				if (processInstanceId != null) {
					for (Portasignatures psigna: pluginHelper.findPendentsPortasignaturesPerProcessInstanceId(processInstanceId)) {
						if (psigna.getDocumentStoreId().longValue() == documentStore.getId().longValue()) {
							psigna.setEstat(TipusEstat.ESBORRAT);
							pluginHelper.saveOrUpdatePortasignatures(psigna);
						}
					}
				}
				documentStoreRepository.delete(documentStoreId);
			}
			if (taskInstanceId != null) {
				jbpmHelper.deleteTaskInstanceVariable(
						taskInstanceId,
						documentStore.getJbpmVariable());
				jbpmHelper.deleteTaskInstanceVariable(
						taskInstanceId,
						PREFIX_SIGNATURA + documentCodi);
			}
			if (processInstanceId != null) {
				jbpmHelper.deleteProcessInstanceVariable(
						processInstanceId,
						documentStore.getJbpmVariable());
			}
		}
	}

	private Document getDocumentDisseny(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		DefinicioProces definicioProces = null;
		if (taskInstanceId != null) {
			JbpmTask taskInstance = jbpmHelper.getTaskById(taskInstanceId);
			definicioProces = definicioProcesRepository.findByJbpmId(taskInstance.getProcessDefinitionId());
		} else {
			JbpmProcessInstance processInstance = jbpmHelper.getProcessInstance(processInstanceId);
			definicioProces = definicioProcesRepository.findByJbpmId(processInstance.getProcessDefinitionId());
		}
		return documentRepository.findAmbDefinicioProcesICodi(definicioProces.getId(), documentCodi);
	}
	
	private Long getDocumentStoreIdDeVariableJbpm(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		Object value = null;
		if (taskInstanceId != null) {
			value = jbpmHelper.getTaskInstanceVariable(
					taskInstanceId,
					getVarPerDocumentCodi(documentCodi, false));
		}
		if (value == null && processInstanceId != null) {
			value = jbpmHelper.getProcessInstanceVariable(
					processInstanceId,
					getVarPerDocumentCodi(documentCodi, false));
		}
		return (Long)value;
	}
	
	private String getVarPerDocumentCodi(String documentCodi, boolean isAdjunt) {
		if (isAdjunt)
			return PREFIX_ADJUNT + documentCodi;
		else
			return PREFIX_VAR_DOCUMENT + documentCodi;
	}
	public static String getDocumentCodiPerVariableJbpm(String var) {
		if (var.startsWith(PREFIX_VAR_DOCUMENT)) {
			return var.substring(PREFIX_VAR_DOCUMENT.length());
		} else if (var.startsWith(PREFIX_ADJUNT)) {
			return var.substring(PREFIX_ADJUNT.length());
		} else if (var.startsWith(PREFIX_SIGNATURA)) {
			return var.substring(PREFIX_SIGNATURA.length());
		} else {
			return var;
		}
	}

	private String nomArxiuAmbExtensio(String fileName, String extensio) {
		if (extensio == null || extensio.length() == 0)
			return fileName;
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String nom = fileName.substring(0, indexPunt);
			return nom + "." + extensio;
		} else {
			return fileName + "." + extensio;
		}
	}

	private boolean isActiuConversioVista() {
		String actiuConversio = (String)GlobalProperties.getInstance().get("app.conversio.actiu");
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.vista.actiu");
		if (actiuConversioVista == null)
			actiuConversioVista = (String)GlobalProperties.getInstance().get("app.conversio.gentasca.actiu");
		return "true".equalsIgnoreCase(actiuConversioVista);
	}
	
	private String getExtensioVista(Document document) {
		String extensioVista = null;
		if (isActiuConversioVista()) {
			if (document.getConvertirExtensio() != null && document.getConvertirExtensio().length() > 0) {
				extensioVista = document.getConvertirExtensio();
			} else {
				extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.vista.extension");
				if (extensioVista == null)
					extensioVista = (String)GlobalProperties.getInstance().get("app.conversio.gentasca.extension");
			}
		}
		return extensioVista;
	}

	public DocumentDto generarDocumentAmbPlantilla(
			String taskInstanceId,
			String processInstanceId,
			Long documentId) throws DocumentGenerarException, DocumentConvertirException {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Document document = documentRepository.findOne(documentId);
		DocumentDto resposta = new DocumentDto();
		resposta.setCodi(document.getCodi());
		resposta.setDataCreacio(new Date());
		resposta.setDataDocument(new Date());
		resposta.setArxiuNom(document.getNom() + ".odt");
		resposta.setAdjuntarAuto(document.isAdjuntarAuto());
		if (document.isPlantilla()) {
			ArxiuDto resultat = plantillaHelper.generarDocumentPlantilla(
					expedient.getEntorn().getId(),
					documentId,
					taskInstanceId,
					processInstanceId,
					new Date());
			if (isActiuConversioVista()) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					openOfficeUtils.convertir(
							resposta.getArxiuNom(),
							resultat.getContingut(),
							getExtensioVista(document),
							baos);
					resposta.setArxiuNom(
							nomArxiuAmbExtensio(
									resposta.getArxiuNom(),
									getExtensioVista(document)));
					resposta.setArxiuContingut(baos.toByteArray());
				} catch (Exception ex) {
					throw new DocumentConvertirException(
							"Error en la conversió del document",
							ex);
				}
			} else {
				resposta.setArxiuContingut(resultat.getContingut());
			}
		} else {
			resposta.setArxiuContingut(document.getArxiuContingut());
		}
		return resposta;
	}

	private static final Log logger = LogFactory.getLog(DocumentHelperV3.class);
}
