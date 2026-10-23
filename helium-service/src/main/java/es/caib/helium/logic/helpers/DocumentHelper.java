/**
 *
 */
package es.caib.helium.logic.helpers;

import es.caib.helium.commons.config.PropertyConfig;
import es.caib.helium.commons.dto.DocumentDto;
import es.caib.helium.commons.dto.PortafirmesEstatEnum;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.commons.utils.PdfUtils;
import es.caib.helium.logic.helper.*;
import es.caib.helium.logic.utils.DocumentTokenUtils;
import es.caib.helium.persistence.entity.*;
import es.caib.helium.persistence.entity.DocumentStore.DocumentFont;
import es.caib.helium.persistence.repository.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Helper per a gestionar els documents dels expedients
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentHelper {

	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private PortasignaturesRepository portasignaturesRepository;

	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ExpedientHelper expedientHelper;

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
		DocumentStore documentStore = null;
		Long documentStoreId = getDocumentStoreIdDeVariableBpm(taskInstanceId, processInstanceId, documentCodi, isAdjunt);
		if (documentStoreId != null)
			documentStore = documentStoreRepository.findById(documentStoreId).orElse(null);
		if (documentStore == null) {
			documentStore = new DocumentStore(
				     (pluginHelper.gestioDocumentalIsPluginActiu()) ? DocumentFont.ALFRESCO : DocumentFont.INTERNA,
				     processInstanceId,
				     documentCodi,
				     new Date(),
				     documentData,
				     arxiuNom);
		    documentStore.setAdjunt(isAdjunt);
		    if (isAdjunt)
		    documentStore.setAdjuntTitol(documentNom);
		    if (arxiuContingut != null)
		    documentStore.setArxiuContingut(arxiuContingut);
		    documentStore = documentStoreRepository.save(documentStore);
		    documentStoreId = documentStore.getId();
		} else {
			documentStore.setDataDocument(documentData);
			documentStore.setArxiuNom(arxiuNom);
			if (pluginHelper.gestioDocumentalIsPluginActiu()) {
				documentStore.setArxiuContingut(
						(pluginHelper.gestioDocumentalIsPluginActiu()) ? null : arxiuContingut);
			}
			if (documentStore.isAdjunt())
				documentStore.setAdjuntTitol(documentNom);
			if (arxiuContingut != null && pluginHelper.gestioDocumentalIsPluginActiu())
				pluginHelper.gestioDocumentalDeleteDocument(
						documentStore.getReferenciaFont(),
						expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
		}
		return documentStoreId;
	}

	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		Long documentStoreId = getDocumentStoreIdDeVariableBpm(
				taskInstanceId,
				processInstanceId,
				documentCodi);
		if (documentStoreId != null){
			DocumentStore documentStore = documentStoreRepository.findById(documentStoreId).orElse(null);
			if (documentStore != null) {
//				if (documentStore.isSignat()) {
//					if (pluginHelper.custodiaIsPluginActiu()) {
//						pluginHelper.custodiaEsborrarSignatures(
//								documentStore.getReferenciaCustodia(),
//								expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
//					}
//				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginHelper.gestioDocumentalDeleteDocument(
							documentStore.getReferenciaFont(),
							expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
				if (processInstanceId != null) {
					List<Portasignatures> peticions = portasignaturesRepository.findByProcessInstanceIdAndDocumentStoreId(
							processInstanceId,
							documentStore.getId());
					if (!peticions.isEmpty()) {
						for (Portasignatures peticio : peticions) {
							peticio.setEstat(PortafirmesEstatEnum.ESBORRAT);
							portasignaturesRepository.save(peticio);
						}
					}
				}
				documentStoreRepository.deleteById(documentStoreId);
			}
		}
	}

	public DocumentDto getDocumentSenseContingut(
			Long documentStoreId) {
		if (documentStoreId != null) {
			DocumentDto dto;
			try {
				dto = toDocumentDto(
						documentStoreId,
						false,
						false,
						false,
						false,
						false);
				return dto;
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return null;
	}

	private Long getDocumentStoreIdDeVariableBpm(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		return getDocumentStoreIdDeVariableBpm(taskInstanceId, processInstanceId,documentCodi, false);
	}

	private Long getDocumentStoreIdDeVariableBpm(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			boolean isAdjunt) {
		Object value = null;
		if (taskInstanceId != null) {
//			value = jbpmDao.getTaskInstanceVariable(
//					taskInstanceId,
//					getVarPerDocumentCodi(documentCodi, isAdjunt));
		}
		if (value == null && processInstanceId != null) {
//			value = jbpmDao.getProcessInstanceVariable(
//					processInstanceId,
//					getVarPerDocumentCodi(documentCodi, isAdjunt));
		}
		return (Long)value;
	}

	private DocumentDto toDocumentDto(
			Long documentStoreId,
			boolean ambContingutOriginal,
			boolean ambContingutSignat,
			boolean ambContingutVista,
			boolean perSignar,
			boolean ambSegellSignatura) {
		if (documentStoreId != null) {
			DocumentStore document = documentStoreRepository.findById(documentStoreId).orElse(null);
			if (document != null) {
				DocumentDto dto = new DocumentDto();
				dto.setId(document.getId());
				dto.setDataCreacio(document.getDataCreacio());
				dto.setDataDocument(document.getDataDocument());
				dto.setArxiuNom(document.getArxiuNom());
				dto.setArxiuContingut(document.getArxiuContingut());
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
//					dto.setUrlVerificacioCustodia(
//							pluginHelper.custodiaObtenirUrlComprovacioSignatura(
//									document.getReferenciaCustodia()));
//					if (document.getArxiuUuid() == null) {
//						dto.setUrlVerificacioCustodia(
//								pluginHelper.custodiaObtenirUrlComprovacioSignatura(
//										document.getReferenciaCustodia()));
//						dto.set.setSignaturaUrlVerificacio(
//								dto.getUrlVerificacioCustodia());
//					} else {
//						dto.setSignaturaUrlVerificacio(
//								getPropertyArxiuVerificacioBaseUrl() + document.getNtiCsv());
//					}

				}
				String codiDocument;
				if (document.isAdjunt()) {
					dto.setAdjuntId(document.getCodi());
					dto.setDocumentId(document.getId());
				} else {
					codiDocument = document.getCodi();
//					JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(document.getProcessInstanceId());
//					DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(document.getProcessInstanceId());
					ExpedientTipus expedientTipus = expedient.getTipus();
					Document doc = null;
					if (expedientTipus.isAmbInfoPropia())
						doc = documentRepository.findByExpedientTipusAndCodi(
								expedientTipus.getId(),
								codiDocument,
								expedientTipus.getExpedientTipusPare() != null);
//					else
//						doc = documentRepository.findByDefinicioProcesAndCodi(
//								definicioProces,
//								codiDocument);

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
					byte[] signatura = document.getArxiuContingut();
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
							arxiuOrigenContingut = document.getArxiuContingut();
						}
					} else {
						arxiuOrigenNom = dto.getArxiuNom();
						if (ambContingutOriginal) {
							arxiuOrigenContingut = dto.getArxiuContingut();
						} else {
							if (document.getFont().equals(DocumentFont.INTERNA)) {
								arxiuOrigenContingut = document.getArxiuContingut();
							} else {
								arxiuOrigenContingut = pluginHelper.gestioDocumentalObtenirDocument(
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
									(ambSegellSignatura) ? getUrlComprovacioSignatura(document.getReferenciaCustodia(), dto.getTokenSignatura()): null,
									document.isRegistrat(),
									numeroRegistre,
									dataRegistre,
									document.getRegistreOficinaNom(),
									document.isRegistreEntrada(),
									vistaContingut,
									extensioDesti);
							dto.setVistaContingut(vistaContingut.toByteArray());
						} catch (Exception ex) {
							Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(document.getProcessInstanceId());
							String errorDescripcio = "No s'ha pogut generar la vista pel document '" + document.getCodiDocument() + "'";
							logger.error(errorDescripcio, ex);
							throw SistemaExternException.tractarSistemaExternException(
									expedient.getEntorn().getId(),
									expedient.getEntorn().getCodi(),
									expedient.getEntorn().getNom(),
									expedient.getId(),
									expedient.getTitol(),
									expedient.getNumero(),
									expedient.getTipus().getId(),
									expedient.getTipus().getCodi(),
									expedient.getTipus().getNom(),
									MonitorIntegracioHelper.INTCODI_PFIRMA,
									"(PORTASIGNATURES. Enviar: " + errorDescripcio + ")",
									ex);
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
		return "true".equalsIgnoreCase((String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_SIGNATURA_PLUGIN_FILE_ATTACHED));
	}

	private boolean isActiuConversioSignatura() {
		String actiuConversio = (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_ACTIU);
		if (!"true".equalsIgnoreCase(actiuConversio))
			return false;
		String actiuConversioSignatura = (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_SIGNATURA_ACTIU);
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
		return (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_SIGNATURA_EXTENSION);
	}
	private String getExtensioArxiuRegistrat() {
		return (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_CONVERSIO_REGISTRE_EXTENSION);
	}

	private byte[] getContingutDocumentAmbFont(DocumentStore document) {
		if (document.getFont().equals(DocumentFont.INTERNA))
			return document.getArxiuContingut();
		else
			return pluginHelper.gestioDocumentalObtenirDocument(
							document.getReferenciaFont());
	}

	private String getUrlComprovacioSignatura(String referenciaCustodia, String token) {
		String baseUrl = (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_BASE_VERIFICACIO_URL);
		if (baseUrl == null)
			baseUrl = (String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_BASE_URL);
		return baseUrl + "/signatura/verificarExtern.html?token=" + token;
	}

	private DocumentTokenUtils getDocumentTokenUtils() {
		if (documentTokenUtils == null)
			documentTokenUtils = new DocumentTokenUtils(
					(String)GlobalProperties.getInstance().getProperty(PropertyConfig.PROP_ENCRIPTACIO_CLAU));
		return documentTokenUtils;
	}
	private PdfUtils getPdfUtils() {
		if (pdfUtils == null)
			pdfUtils = new PdfUtils();
		return pdfUtils;
	}

	private static final Log logger = LogFactory.getLog(DocumentHelper.class);

}
