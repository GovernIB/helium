/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.util.DocumentTokenUtils;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentDescarregarException;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;

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

	@Resource
	DocumentRepository documentRepository;
	@Resource
	DocumentStoreRepository documentStoreRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;

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
		Map<String, Document> documentsIndexatsPerCodi = new HashMap<String, Document>();
		for (Document document: documents)
			documentsIndexatsPerCodi.put(document.getCodi(), document);
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3", "expedient", tipusExp, null, "1");
		mesuresTemporalsHelper.mesuraIniciar("Expedient DOCUMENTS v3", "expedient", tipusExp, null, "2");
		List<ExpedientDocumentDto> resposta = new ArrayList<ExpedientDocumentDto>();
		Map<String, Object> varsInstanciaProces = jbpmHelper.getProcessInstanceVariables(
				processInstanceId);
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3", "expedient", tipusExp, null, "2");
		if (varsInstanciaProces != null) {
			mesuresTemporalsHelper.mesuraIniciar("Expedient DOCUMENTS v3", "expedient", tipusExp, null, "3");
			filtrarVariablesAmbDocuments(varsInstanciaProces);
			for (String var: varsInstanciaProces.keySet()) {
				Long documentStoreId = (Long)varsInstanciaProces.get(var);
				if (documentStoreId != null) {
					String documentCodi = getDocumentCodiDeVariableJbpm(var);
					ExpedientDocumentDto dto = toDocumentDto(
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
			mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3", "expedient", tipusExp, null, "3");
		}
		mesuresTemporalsHelper.mesuraCalcular("Expedient DOCUMENTS v3", "expedient",tipusExp);
		return resposta;
	}


	private ExpedientDocumentDto toDocumentDto(
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

	private static final Log logger = LogFactory.getLog(DocumentHelperV3.class);

	/*
	private JbpmHelper jbpmDao;
	private DefinicioProcesDao definicioProcesDao;
	private ExpedientDao expedientDao;
	private DocumentDao documentDao;
	private PluginPortasignaturesDao pluginPortasignaturesDao;
	private DocumentStoreDao documentStoreDao;
	private PluginSignaturaDao pluginSignaturaDao;
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	private PluginCustodiaDao pluginCustodiaDao;

	private DocumentTokenUtils documentTokenUtils;
	private PdfUtils pdfUtils;
	private OpenOfficeUtils openOfficeUtils;

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
			documentStore = documentStoreDao.getById(documentStoreId, false);
		if (documentStore == null) {
			// Si el document no existeix el crea
			documentStoreId = documentStoreDao.create(
					processInstanceId,
					getVarPerDocumentCodi(documentCodi, isAdjunt),
					documentData,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? DocumentFont.ALFRESCO : DocumentFont.INTERNA,
					arxiuNom,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? null : arxiuContingut,
					isAdjunt,
					documentNom);
		} else {
			// Si el document està creat l'actualitza
			documentStoreDao.update(
					documentStoreId,
					documentData,
					arxiuNom,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? null : arxiuContingut,
					documentNom);
			if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu())
				pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
		}
		// Crea el document a dins la gestió documental
		if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu()) {
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
			Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
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
			documentStoreDao.updateReferenciaFont(
					documentStoreId,
					referenciaFont);
		}
		// Guarda la referència al nou document a dins el jBPM
		if (taskInstanceId != null)
			jbpmDao.setTaskInstanceVariable(
					taskInstanceId,
					getVarPerDocumentCodi(documentCodi, isAdjunt),
					documentStoreId);
		else
			jbpmDao.setProcessInstanceVariable(
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
			DocumentStore documentStore = documentStoreDao.getById(documentStoreId, false);
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
					for (Portasignatures psigna: pluginPortasignaturesDao.findPendentsPerProcessInstanceId(processInstanceId)) {
						if (psigna.getDocumentStoreId().longValue() == documentStore.getId().longValue()) {
							psigna.setEstat(TipusEstat.ESBORRAT);
							pluginPortasignaturesDao.saveOrUpdate(psigna);
						}
					}
				}
				documentStoreDao.delete(documentStoreId);
			}
			if (taskInstanceId != null) {
				jbpmDao.deleteTaskInstanceVariable(
						taskInstanceId,
						getVarPerDocumentCodi(documentCodi, false));
				jbpmDao.deleteTaskInstanceVariable(
						taskInstanceId,
						PREFIX_SIGNATURA + documentCodi);
			}
			if (processInstanceId != null) {
				jbpmDao.deleteProcessInstanceVariable(
						processInstanceId,
						getVarPerDocumentCodi(documentCodi, false));
			}
		}
	}

	public boolean signarDocumentTascaAmbToken(
			String token,
			byte[] signatura) {
		String taskInstanceId = getTaskInstanceIdPerToken(token);
		Long documentStoreId = getDocumentStoreIdPerToken(token);
		DocumentDto dto = toDocumentDto(
				documentStoreId,
				false,
				false,
				true,
				true,
				true);
		if (dto != null) {
			DocumentStore documentStore = documentStoreDao.getById(dto.getId(), false);
			boolean custodiat = false;
			if (pluginCustodiaDao.isCustodiaActiu()) {
				String nomArxiu = getNomArxiuAmbExtensio(
						dto.getArxiuNom(),
						getExtensioArxiuSignat());
				String referenciaCustodia = null;
				if (pluginCustodiaDao.isValidacioImplicita()) {
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
				jbpmDao.setTaskInstanceVariable(
						taskInstanceId,
						DocumentHelperV3.PREFIX_SIGNATURA + dto.getDocumentCodi(),
						documentStore.getId());
			}
			return custodiat;
		}
		return false;
	}

	public DocumentStore getDocumentStore(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId, documentCodi);
		if (documentStoreId != null)
			return documentStoreDao.getById(documentStoreId, false);
		else
			return null;
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
					Object signatEnTasca = jbpmDao.getTaskInstanceVariable(taskInstanceId, DocumentHelperV3.PREFIX_SIGNATURA + dto.getDocumentCodi());
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
	public DocumentDto getDocumentOriginal(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			boolean ambContingut) {
		return getDocumentOriginal(
				getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId, documentCodi),
				ambContingut);
	}
	public DocumentDto getDocumentOriginalPerToken(
			String token,
			boolean ambContingut) {
		return getDocumentOriginal(
				getDocumentStoreIdPerToken(token),
				ambContingut);
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

	public DocumentDto getDocumentVista(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			boolean perSignar,
			boolean ambSegellSignatura) {
		return getDocumentVista(
				getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId, documentCodi),
				perSignar,
				ambSegellSignatura);
	}
	public DocumentDto getDocumentVistaPerToken(
			String token,
			boolean perSignar,
			boolean ambSegellSignatura) {
		return getDocumentVista(
				getDocumentStoreIdPerToken(token),
				perSignar,
				ambSegellSignatura);
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
	public String getDocumentCodiPerVariableJbpm(String var) {
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

	public void convertirArxiuPerVista(
			Document document,
			ArxiuDto arxiu) throws Exception {
		if (isActiuConversioVista()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String extensioVista = getExtensioVista(document);
			getOpenOfficeUtils().convertir(
					arxiu.getNom(),
					arxiu.getContingut(),
					extensioVista,
					baos);
			arxiu.setNom(
					nomArxiuAmbExtensio(
							arxiu.getNom(),
							extensioVista));
			arxiu.setContingut(baos.toByteArray());
		}
	}

	public JbpmTask getTaskPerToken(String token) {
		return jbpmDao.getTaskById(getTaskInstanceIdPerToken(token));
	}

	@Autowired
	public void setJbpmDao(JbpmHelper jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
	}
	@Autowired
	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}
	@Autowired
	public void setPluginPortasignaturesDao(
			PluginPortasignaturesDao pluginPortasignaturesDao) {
		this.pluginPortasignaturesDao = pluginPortasignaturesDao;
	}
	@Autowired
	public void setDocumentStoreDao(DocumentStoreDao documentStoreDao) {
		this.documentStoreDao = documentStoreDao;
	}
	@Autowired
	public void setPluginGestioDocumentalDao(PluginGestioDocumentalDao pluginGestioDocumentalDao) {
		this.pluginGestioDocumentalDao = pluginGestioDocumentalDao;
	}
	@Autowired
	public void setPluginSignaturaDao(PluginSignaturaDao pluginSignaturaDao) {
		this.pluginSignaturaDao = pluginSignaturaDao;
	}
	@Autowired
	public void setPluginCustodiaDao(PluginCustodiaDao pluginCustodiaDao) {
		this.pluginCustodiaDao = pluginCustodiaDao;
	}



	private Long getDocumentStoreIdDeVariableJbpm(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		Object value = null;
		if (taskInstanceId != null) {
			value = jbpmDao.getTaskInstanceVariable(
					taskInstanceId,
					getVarPerDocumentCodi(documentCodi, false));
		}
		if (value == null && processInstanceId != null) {
			value = jbpmDao.getProcessInstanceVariable(
					processInstanceId,
					getVarPerDocumentCodi(documentCodi, false));
		}
		return (Long)value;
	}
	
	private Document getDocumentDisseny(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		if (taskInstanceId != null) {
			JbpmTask taskInstance = jbpmDao.getTaskById(taskInstanceId);
			DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(taskInstance.getProcessDefinitionId());
			return documentDao.findAmbDefinicioProcesICodi(
					definicioProces.getId(),
					documentCodi);
		} else {
			JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(processInstanceId);
			DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(processInstance.getProcessDefinitionId());
			return documentDao.findAmbDefinicioProcesICodi(
					definicioProces.getId(),
					documentCodi);
		}
	}

	private DocumentDto toDocumentDto(
			Long documentStoreId,
			boolean ambContingutOriginal,
			boolean ambContingutSignat,
			boolean ambContingutVista,
			boolean perSignar,
			boolean ambSegellSignatura) {
		if (documentStoreId != null) {
			DocumentStore document = documentStoreDao.getById(documentStoreId, false);
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
					JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(document.getProcessInstanceId());
					DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmKeyIVersio(jpd.getKey(), jpd.getVersion());
					Document doc = documentDao.findAmbDefinicioProcesICodi(definicioProces.getId(), codiDocument);
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
	private String getExtensioArxiuSignat() {
		return (String)GlobalProperties.getInstance().get("app.conversio.signatura.extension");
	}
	private String getExtensioArxiuRegistrat() {
		return (String)GlobalProperties.getInstance().get("app.conversio.registre.extension");
	}

	private byte[] getContingutDocumentAmbFont(DocumentStore document) {
		if (document.getFont().equals(DocumentFont.INTERNA))
			return document.getArxiuContingut();
		else
			return pluginGestioDocumentalDao.retrieveDocument(
							document.getReferenciaFont());
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

	private Long getDocumentStoreIdPerToken(String token) {
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

	private String getTaskInstanceIdPerToken(String token) {
		try {
			String[] tokenDesxifrat = getDocumentTokenUtils().desxifrarTokenMultiple(token);
			if (tokenDesxifrat.length == 2)
				return tokenDesxifrat[0];
			else
				throw new IllegalArgumentsException("Format de token incorrecte");
		} catch (Exception ex) {
			throw new IllegalArgumentsException("Format de token incorrecte", ex);
		}
	}

	private String getVarPerDocumentCodi(String documentCodi, boolean isAdjunt) {
		if (isAdjunt)
			return PREFIX_ADJUNT + documentCodi;
		else
			return PREFIX_VAR_DOCUMENT + documentCodi;
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

	private DocumentTokenUtils getDocumentTokenUtils() {
		if (documentTokenUtils == null)
			documentTokenUtils = new DocumentTokenUtils(
					(String)GlobalProperties.getInstance().get("app.encriptacio.clau"));
		return documentTokenUtils;
	}
	private PdfUtils getPdfUtils() {
		if (pdfUtils == null)
			pdfUtils = new PdfUtils();
		return pdfUtils;
	}
	private OpenOfficeUtils getOpenOfficeUtils() {
		if (openOfficeUtils == null)
			openOfficeUtils = new OpenOfficeUtils();
		return openOfficeUtils;
	}

	

	public void esborrarVariableInstance(
			String processInstanceId,
			String adjuntId) {

			jbpmDao.deleteProcessInstanceVariable(
						processInstanceId,
						getVarPerDocumentCodi(adjuntId, true));
	}*/

}
