/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.DocumentDao;
import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.core.model.dao.PluginSignaturaDao;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.DocumentTokenUtils;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar els documents dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentHelper {

	public static final String PREFIX_VAR_DOCUMENT = "H3l1um#document.";
	public static final String PREFIX_ADJUNT = "H3l1um#adjunt.";
	public static final String PREFIX_SIGNATURA = "H3l1um#signatura.";

	private JbpmDao jbpmDao;
	private DefinicioProcesDao definicioProcesDao;
	private ExpedientDao expedientDao;
	private DocumentDao documentDao;
	private DocumentStoreDao documentStoreDao;
	private PluginSignaturaDao pluginSignaturaDao;
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	private PluginCustodiaDao pluginCustodiaDao;

	private DocumentTokenUtils documentTokenUtils;
	private PdfUtils pdfUtils;



	public Long actualitzarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			String documentNom,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut,
			boolean isAdjunt) {
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId, documentCodi);
		if (documentStoreId == null) {
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
			DocumentStore documentStore = documentStoreDao.getById(documentStoreId, false);
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
		if (documentStoreId != null) {
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
				documentStoreDao.delete(documentStoreId);
			}
			if (taskInstanceId != null) {
				jbpmDao.deleteTaskInstanceVariable(
						taskInstanceId,
						getVarPerDocumentCodi(documentCodi, false));
				jbpmDao.deleteTaskInstanceVariable(
						taskInstanceId,
						PREFIX_SIGNATURA + documentCodi);
			} else {
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
						DocumentHelper.PREFIX_SIGNATURA + dto.getDocumentCodi(),
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
			boolean perSignarEnTasca) {
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
					Object signatEnTasca = jbpmDao.getTaskInstanceVariable(taskInstanceId, DocumentHelper.PREFIX_SIGNATURA + dto.getDocumentCodi());
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

	public JbpmTask getTaskPerToken(String token) {
		return jbpmDao.getTaskById(getTaskInstanceIdPerToken(token));
	}

	@Autowired
	public void setJbpmDao(JbpmDao jbpmDao) {
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

	private static final Log logger = LogFactory.getLog(DocumentHelper.class);

}
