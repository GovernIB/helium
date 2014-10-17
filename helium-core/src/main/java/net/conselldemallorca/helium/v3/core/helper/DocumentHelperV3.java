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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.util.DocumentTokenUtils;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentDescarregarException;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentTascaRepository;

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
	private DocumentRepository documentRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientHelper expedientHelper;
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

	private PdfUtils pdfUtils;
	private DocumentTokenUtils documentTokenUtils;



	public ArxiuDto getArxiuPerDocumentStoreId(
			Long documentStoreId,
			boolean ambContingutOriginal,
			boolean perSignar,
			boolean ambSegellSignatura) {
		ArxiuDto resposta = new ArxiuDto();
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		// Obtenim el contingut de l'arxiu
		byte[] arxiuOrigenContingut = null;
		if (documentStore.isSignat() && isSignaturaFileAttached()) {
			try {
			arxiuOrigenContingut = pluginHelper.custodiaObtenirSignaturesAmbArxiu(
					documentStore.getReferenciaCustodia());
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
		for (Document document: documents)
			documentsIndexatsPerCodi.put(document.getCodi(), document);
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3 convertDocumentDto", "expedient", tipusExp, null, "0");
		
		List<ExpedientDocumentDto> resposta = new ArrayList<ExpedientDocumentDto>();
		Map<String, Object> varsInstanciaProces = jbpmHelper.getProcessInstanceVariables(
				processInstanceId);
		if (varsInstanciaProces != null) {
			mesuresTemporalsHelper.mesuraIniciar("Expedient DOCUMENTS v3 convertDocumentDto", "expedient", tipusExp, null, "1");
			filtrarVariablesAmbDocuments(varsInstanciaProces);
			for (String var: varsInstanciaProces.keySet()) {
				Long documentStoreId = (Long)varsInstanciaProces.get(var);
				if (documentStoreId != null) {
					String documentCodi = getDocumentCodiDeVariableJbpm(var);
					ExpedientDocumentDto dto = toExpedientDocumentDto(
							processInstanceId,
							documentStoreId,
							var.startsWith(VariableHelper.PREFIX_VAR_DOCUMENT),
							documentCodi,
							var.startsWith(VariableHelper.PREFIX_VAR_ADJUNT),
							getAdjuntIdDeVariableJbpm(var),
							documentsIndexatsPerCodi.get(documentCodi));
					resposta.add(dto);
				}
			}
			mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3 convertDocumentDto", "expedient", tipusExp, null, "1");
		}
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3 convertDocumentDto", "expedient",tipusExp);
		
		return resposta;
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
					task.getProcessInstanceId(),
					documentTasca));
		}
		return resposta;
	}



	private ExpedientDocumentDto toExpedientDocumentDto(
			String processInstanceId,
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
			try {
				dto.setSignaturaUrlVerificacio(getUrlComprovacioSignatura(documentStoreId));
			} catch (Exception ex) {
				dto.setError("No s'ha pogut obtenir la URL de comprovació de signatura (id=" + documentStoreId + ",documentCodi=" + documentCodi + ", processInstanceId=" + processInstanceId + ")");
				logger.error("No s'ha pogut obtenir la URL de comprovació de signatura (id=" + documentStoreId + ",documentCodi=" + documentCodi + ", processInstanceId=" + processInstanceId + ")", ex);
			}
			dto.setRegistrat(documentStore.isRegistrat());
			dto.setRegistreEntrada(documentStore.isRegistreEntrada());
			dto.setRegistreNumero(documentStore.getRegistreNumero());
			dto.setRegistreData(documentStore.getRegistreData());
			dto.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
			dto.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
			if (esDocument) {
				dto.setDocumentCodi(documentCodi);
				if (document != null) {
					dto.setDocumentId(document.getId());
					dto.setDocumentNom(document.getNom());
					dto.setDocumentContentType(document.getContentType());
					dto.setDocumentCustodiaCodi(document.getCustodiaCodi());
					dto.setDocumentTipusDocPortasignatures(document.getTipusDocPortasignatures());
				} else {
					dto.setError("No s'ha trobat el document (id=" + documentStoreId + ",documentCodi=" + documentCodi + ", processInstanceId=" + processInstanceId + ")");
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
				dto.setError("No s'ha trobat el documentStore del document (id=" + documentStoreId + ", documentCodi=" + documentCodi + ", processInstanceId=" + processInstanceId + ")");
			} else if (esAdjunt) {
				dto.setAdjuntId(adjuntId);
				dto.setAdjunt(true);
				dto.setError("No s'ha trobat el documentStore del adjunt (id=" + documentStoreId + ", adjuntId=" + adjuntId + ", processInstanceId=" + processInstanceId + ")");
			} else {
				dto.setError("No s'ha trobat el documentStore i no es ni document ni adjunt (id=" + documentStoreId + ", processInstanceId=" + processInstanceId + ")"); 
			}
		}
		return dto;
	}

	private TascaDocumentDto toTascaDocumentDto(
			String processInstanceId,
			DocumentTasca documentTasca) {
		TascaDocumentDto dto = new TascaDocumentDto();
		Document document = documentTasca.getDocument();
		String varCodi = getVarPerDocumentCodi(
				document.getCodi(),
				false);
		dto.setVarCodi(varCodi);
		dto.setDocumentCodi(document.getCodi());
		dto.setDocumentNom(document.getNom());
		dto.setRequired(documentTasca.isRequired());
		dto.setReadOnly(documentTasca.isReadOnly());
		Long documentStoreId = (Long)jbpmHelper.getProcessInstanceVariable(
				processInstanceId,
				varCodi);
		if (documentStoreId != null) {
			dto.setDocumentPendent(false);
			DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
			if (documentStore != null) {
				dto.setId(documentStore.getId());
				dto.setDataCreacio(documentStore.getDataCreacio());
				dto.setDataModificacio(documentStore.getDataModificacio());
				dto.setDataDocument(documentStore.getDataDocument());
				dto.setSignat(documentStore.isSignat());
				if (documentStore.isRegistrat()) {
					dto.setRegistrat(true);
					dto.setRegistreData(documentStore.getRegistreData());
					dto.setRegistreNumero(documentStore.getRegistreNumero());
					dto.setRegistreOficinaCodi(documentStore.getRegistreOficinaCodi());
					dto.setRegistreOficinaNom(documentStore.getRegistreOficinaNom());
					dto.setRegistreEntrada(documentStore.isRegistreEntrada());
				}
				dto.setArxiuNom(documentStore.getArxiuNom());
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
				} else {
					codiDocument = document.getJbpmVariable().substring(PREFIX_VAR_DOCUMENT.length());
					JbpmProcessDefinition jpd = jbpmHelper.findProcessDefinitionWithProcessInstanceId(document.getProcessInstanceId());
					DefinicioProces definicioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
							jpd.getKey(),
							jpd.getVersion());
					Document doc = documentStoreRepository.findAmbDefinicioProcesICodi(definicioProces.getId(), codiDocument);
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
			DocumentStore ds = new DocumentStore(
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? DocumentFont.ALFRESCO : DocumentFont.INTERNA,
					processInstanceId,
					getVarPerDocumentCodi(documentCodi, isAdjunt),
					new Date(),
					documentData,
					arxiuNom);
			ds.setAdjunt(isAdjunt);
			if (isAdjunt)
				ds.setAdjuntTitol(documentNom);
			if (pluginGestioDocumentalDao.isGestioDocumentalActiu())
				ds.setArxiuContingut(arxiuContingut);
			documentStoreRepository.save(ds);
			documentStoreId = ds.getId();
		} else {
			// Si el document està creat l'actualitza
			DocumentStore ds = documentStoreRepository.findOne(documentStoreId);
			ds.setDataDocument(documentData);
			ds.setDataModificacio(new Date());
			if (ds.isAdjunt())
				ds.setAdjuntTitol(documentNom);
			ds.setArxiuNom(arxiuNom);
			if (pluginGestioDocumentalDao.isGestioDocumentalActiu())
				ds.setArxiuContingut(arxiuContingut);
			if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu())
				pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
		}
		// Crea el document a dins la gestió documental
		if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu()) {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			String docNom = documentNom;
			if (docNom == null) {
				Document document = getDocumentDisseny(taskInstanceId, processInstanceId, documentCodi);
				docNom = document.getNom();
			}
			String referenciaFont = pluginGestioDocumentalDao.createDocument(
					expedient,
					documentStoreId.toString(),
					docNom,
					documentData,
					arxiuNom,
					arxiuContingut);
			DocumentStore ds = documentStoreRepository.findOne(documentStoreId);
			ds.setReferenciaFont(referenciaFont);
		}
		// Guarda la referència al nou document a dins el jBPM
		if (taskInstanceId != null)
			jbpmHelper.setTaskInstanceVariable(
					taskInstanceId,
					getVarPerDocumentCodi(documentCodi, isAdjunt),
					documentStoreId);
		else
			jbpmHelper.setProcessInstanceVariable(
					processInstanceId,
					getVarPerDocumentCodi(documentCodi, isAdjunt),
					documentStoreId);
		return documentStoreId;
	}

	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId, documentCodi);
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
						getVarPerDocumentCodi(documentCodi, false));
				jbpmHelper.deleteTaskInstanceVariable(
						taskInstanceId,
						PREFIX_SIGNATURA + documentCodi);
			}
			if (processInstanceId != null) {
				jbpmHelper.deleteProcessInstanceVariable(
						processInstanceId,
						getVarPerDocumentCodi(documentCodi, false));
			}
		}
	}

	public Document findAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		List<Document> documents = documentRepository.findAmbDefinicioProcesICodi(definicioProcesId, codi);
		if (!documents.isEmpty())
			return documents.get(0);
		return null;
	}

	private Document getDocumentDisseny(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		if (taskInstanceId != null) {
			JbpmTask taskInstance = jbpmHelper.getTaskById(taskInstanceId);
			DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(taskInstance.getProcessDefinitionId());
			return findAmbDefinicioProcesICodi(
					definicioProces.getId(),
					documentCodi);
		} else {
			JbpmProcessInstance processInstance = jbpmHelper.getProcessInstance(processInstanceId);
			DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(processInstance.getProcessDefinitionId());
			return findAmbDefinicioProcesICodi(
					definicioProces.getId(),
					documentCodi);
		}
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

	private static final Log logger = LogFactory.getLog(DocumentHelperV3.class);

}
