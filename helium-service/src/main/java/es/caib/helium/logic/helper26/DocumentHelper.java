/**
 * 
 */
package es.caib.helium.logic.helper26;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import es.caib.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import es.caib.helium.logic.helper.DocumentHelperV3;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.logic.intf.WProcessDefinition;
import es.caib.helium.logic.intf.WProcessInstance;
import es.caib.helium.logic.intf.WTaskInstance;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.util.JbpmVars;
import es.caib.helium.logic.util.DocumentTokenUtils;
import es.caib.helium.logic.util.GlobalProperties;
import es.caib.helium.logic.util.PdfUtils;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Document;
import es.caib.helium.persist.entity.DocumentStore;
import es.caib.helium.persist.entity.DocumentStore.DocumentFont;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Portasignatures;
import es.caib.helium.persist.entity.Portasignatures.TipusEstat;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.DocumentRepository;
import es.caib.helium.persist.repository.DocumentStoreRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.PortasignaturesRepository;

/**
 * Helper per a gestionar els documents dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentHelper {

	/*public static final String PREFIX_VAR_DOCUMENT = "H3l1um#document.";
	public static final String PREFIX_ADJUNT = "H3l1um#adjunt.";
	public static final String PREFIX_SIGNATURA = "H3l1um#signatura.";*/

	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientRepository expedientRepository;
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
	@Resource(name = "documentHelperV3")
	private DocumentHelperV3 documentHelperV3;

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
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId, documentCodi, isAdjunt);
		if (documentStoreId != null)
			documentStore = documentStoreRepository.findById(documentStoreId).orElse(null);
		if (documentStore == null) {
			documentStore = new DocumentStore(
				     (pluginHelper.gestioDocumentalIsPluginActiu()) ? DocumentFont.ALFRESCO : DocumentFont.INTERNA,
				     processInstanceId,
				     getVarPerDocumentCodi(documentCodi, isAdjunt),
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
		// Crea el document a dins la gestió documental
		if (arxiuContingut != null && pluginHelper.gestioDocumentalIsPluginActiu()) {
			ExpedientDto expedient = workflowEngineApi.expedientFindByProcessInstanceId(processInstanceId);
			String docNom = documentNom;
			if (docNom == null) {
				Document document = getDocumentDisseny(
						taskInstanceId,
						processInstanceId,
						documentCodi);
				docNom = document.getNom();
			}
			String referenciaFont = pluginHelper.gestioDocumentalCreateDocument(
					expedientRepository.findById(expedient.getId()).get(),
					documentStoreId.toString(),
					docNom,
					documentData,
					arxiuNom,
					arxiuContingut);
			documentStore.setReferenciaFont(referenciaFont);
		}
		// Guarda la referència al nou document a dins el jBPM
		if (taskInstanceId != null)
			workflowEngineApi.setTaskInstanceVariable(
					taskInstanceId,
					getVarPerDocumentCodi(documentCodi, isAdjunt),
					documentStoreId);
		else
			workflowEngineApi.setProcessInstanceVariable(
					processInstanceId,
					getVarPerDocumentCodi(documentCodi, isAdjunt),
					documentStoreId);
		return documentStoreId;
	}

	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		Long documentStoreId = getDocumentStoreIdDeVariableJbpm(
				taskInstanceId,
				processInstanceId,
				documentCodi);
		if (documentStoreId != null){
			DocumentStore documentStore = documentStoreRepository.findById(documentStoreId).orElse(null);
			if (documentStore != null) {
				if (documentStore.isSignat()) {
					if (pluginHelper.custodiaIsPluginActiu()) {
						pluginHelper.custodiaEsborrarSignatures(
								documentStore.getReferenciaCustodia(),
								expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
					}
				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginHelper.gestioDocumentalDeleteDocument(
							documentStore.getReferenciaFont(),
							expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
				if (processInstanceId != null) {
					Portasignatures psigna = portasignaturesRepository.findByProcessInstanceIdAndDocumentStoreId(
							processInstanceId,
							documentStore.getId());
					if (psigna != null) {
						psigna.setEstat(TipusEstat.ESBORRAT);
						portasignaturesRepository.save(psigna);
					}
				}
				documentStoreRepository.deleteById(documentStoreId);
			}
			if (taskInstanceId != null) {
				workflowEngineApi.deleteTaskInstanceVariable(
						taskInstanceId,
						getVarPerDocumentCodi(documentCodi, false));
				workflowEngineApi.deleteTaskInstanceVariable(
						taskInstanceId,
						JbpmVars.PREFIX_SIGNATURA + documentCodi);
			}
			if (processInstanceId != null) {
				workflowEngineApi.deleteProcessInstanceVariable(
						processInstanceId,
						getVarPerDocumentCodi(documentCodi, false));
			}
		}
	}
	
	public void esborrarDocumentAdjunt(
			Long documentStoreId,
			String processInstanceId,
			String adjuntId) {
		
		if (documentStoreId != null){
			DocumentStore documentStore = documentStoreRepository.findById(documentStoreId).orElse(null);
			if (documentStore != null) {
				if (documentStore.isSignat()) {
					if (pluginHelper.custodiaIsPluginActiu()) {
						pluginHelper.custodiaEsborrarSignatures(
								documentStore.getReferenciaCustodia(),
								expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
					}
				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginHelper.gestioDocumentalDeleteDocument(
							documentStore.getReferenciaFont(),
							expedientHelper.findExpedientByProcessInstanceId(processInstanceId));
				if (processInstanceId != null) {
					Portasignatures psigna = portasignaturesRepository.findByProcessInstanceIdAndDocumentStoreId(
							processInstanceId,
							documentStore.getId());
					if (psigna != null) {
						psigna.setEstat(TipusEstat.ESBORRAT);
						portasignaturesRepository.save(psigna);
					}
				}
				documentStoreRepository.deleteById(documentStoreId);
			}
			if (processInstanceId != null) {
				workflowEngineApi.deleteProcessInstanceVariable(
						processInstanceId,
						getVarPerDocumentCodi(adjuntId, true));
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
			DocumentStore documentStore = documentStoreRepository.findById(dto.getId()).get();
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(
					documentStore.getProcessInstanceId());
			boolean custodiat = false;
			if (documentStore.getArxiuUuid() != null) {
				ArxiuDto pdfFirmat = new ArxiuDto();
				pdfFirmat.setNom("firma.pdf");
				pdfFirmat.setTipusMime("application/pdf");
				pdfFirmat.setContingut(signatura);
				pluginHelper.arxiuDocumentGuardarPdfFirmat(
						expedient,
						documentStore,
						dto.getDocumentNom(),
						pdfFirmat);
				es.caib.plugins.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentInfo(
						documentStore.getArxiuUuid(),
						null,
						false,
						true);
				documentHelperV3.actualitzarNtiFirma(documentStore, documentArxiu);
				custodiat = true;
			} else if (pluginHelper.custodiaIsPluginActiu()) {
				if (expedient.isNtiActiu()) {
					documentHelperV3.actualitzarNtiFirma(documentStore, null);
				}
				String nomArxiu = getNomArxiuAmbExtensio(
						dto.getArxiuNom(),
						getExtensioArxiuSignat());
				String referenciaCustodia = null;
				if (pluginHelper.custodiaIsValidacioImplicita()) {
					referenciaCustodia = pluginHelper.custodiaAfegirSignatura(
							documentStore.getId(),
							documentStore.getReferenciaFont(),
							nomArxiu,
							dto.getCustodiaCodi(),
							signatura);
					custodiat = true;
				} else {
					RespostaValidacioSignatura resposta = pluginHelper.signaturaVerificar(
							dto.getVistaContingut(),
							signatura,
							false);
					if (resposta.isEstatOk()) {
						referenciaCustodia = pluginHelper.custodiaAfegirSignatura(
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
				workflowEngineApi.setTaskInstanceVariable(
						taskInstanceId,
						JbpmVars.PREFIX_SIGNATURA + dto.getDocumentCodi(),
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
			return documentStoreRepository.findById(documentStoreId).orElse(null);
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
			try {
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
						Object signatEnTasca = workflowEngineApi.getTaskInstanceVariable(taskInstanceId, JbpmVars.PREFIX_SIGNATURA + dto.getDocumentCodi());
						dto.setSignatEnTasca(signatEnTasca != null);
					} else {
						dto.setSignatEnTasca(false);
					}
				}
				return dto;
			} catch (Exception e) {
				logger.error(e);
			}
		} 
		return null;
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
			try {
				return toDocumentDto(
						documentStoreId,
						ambContingut,
						false,
						false,
						false,
						false);
			} catch (Exception e) {
				logger.error(e);
			}
		} 
		return null;
	}

	public DocumentDto getDocumentVista(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			boolean perSignar,
			boolean ambSegellSignatura) throws Exception {
		return getDocumentVista(
				getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId, documentCodi),
				perSignar,
				ambSegellSignatura);
	}
	public DocumentDto getDocumentVistaPerToken(
			String token,
			boolean perSignar,
			boolean ambSegellSignatura) throws Exception {
		return getDocumentVista(
				getDocumentStoreIdPerToken(token),
				perSignar,
				ambSegellSignatura);
	}
	public DocumentDto getDocumentVista(
			Long documentStoreId,
			boolean perSignar,
			boolean ambSegellSignatura) throws Exception {
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
		if (var.startsWith(JbpmVars.PREFIX_DOCUMENT)) {
			return var.substring(JbpmVars.PREFIX_DOCUMENT.length());
		} else if (var.startsWith(JbpmVars.PREFIX_ADJUNT)) {
			return var.substring(JbpmVars.PREFIX_ADJUNT.length());
		} else if (var.startsWith(JbpmVars.PREFIX_SIGNATURA)) {
			return var.substring(JbpmVars.PREFIX_SIGNATURA.length());
		} else {
			return var;
		}
	}

	public WTaskInstance getTaskPerToken(String token) {
		return workflowEngineApi.getTaskById(getTaskInstanceIdPerToken(token));
	}

	public void actualitzarNtiFirma(
			DocumentStore documentStore,
			es.caib.plugins.arxiu.api.Document arxiuDocument) {
		documentHelperV3.actualitzarNtiFirma(documentStore, arxiuDocument);
	}

	public void guardarDocumentFirmat(
			DocumentStore documentStore,
			byte[] signatura) throws Exception {
		documentHelperV3.guardarDocumentFirmat(
				documentStore.getProcessInstanceId(),
				documentStore.getId(),
				signatura,
				true,
				true);
	}

	private Long getDocumentStoreIdDeVariableJbpm(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		return getDocumentStoreIdDeVariableJbpm(taskInstanceId, processInstanceId,documentCodi, false);
	}

	private Long getDocumentStoreIdDeVariableJbpm(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			boolean isAdjunt) {
		Object value = null;
		if (taskInstanceId != null) {
			value = workflowEngineApi.getTaskInstanceVariable(
					taskInstanceId,
					getVarPerDocumentCodi(documentCodi, isAdjunt));
		}
		if (value == null && processInstanceId != null) {
			value = workflowEngineApi.getProcessInstanceVariable(
					processInstanceId,
					getVarPerDocumentCodi(documentCodi, isAdjunt));
		}
		return (Long)value;
	}
	
	private Document getDocumentDisseny(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = expedient.getTipus();
		DefinicioProces definicioProces;
		if (taskInstanceId != null) {
			WTaskInstance taskInstance = workflowEngineApi.getTaskById(taskInstanceId);
			definicioProces = definicioProcesRepository.findByJbpmId(taskInstance.getProcessDefinitionId());
		} else {
			WProcessInstance processInstance = workflowEngineApi.getProcessInstance(processInstanceId);
			definicioProces = definicioProcesRepository.findByJbpmId(processInstance.getProcessDefinitionId());
		}
		
		if (expedientTipus.isAmbInfoPropia())
			return documentRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					documentCodi,
					expedientTipus.getExpedientTipusPare() != null);
		else
			return documentRepository.findByDefinicioProcesAndCodi(
					definicioProces, 
					documentCodi);
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
					dto.setUrlVerificacioCustodia(
							pluginHelper.custodiaObtenirUrlComprovacioSignatura(
									documentStoreId.toString()));
				}
				String codiDocument;
				if (document.isAdjunt()) {
					dto.setAdjuntId(document.getJbpmVariable().substring(JbpmVars.PREFIX_ADJUNT.length()));
					dto.setDocumentId(document.getId());
				} else {
					codiDocument = document.getJbpmVariable().substring(JbpmVars.PREFIX_DOCUMENT.length());
					WProcessDefinition jpd = workflowEngineApi.findProcessDefinitionWithProcessInstanceId(document.getProcessInstanceId());
					DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(document.getProcessInstanceId());
					ExpedientTipus expedientTipus = expedient.getTipus();
					Document doc;
					if (expedientTipus.isAmbInfoPropia())
						doc = documentRepository.findByExpedientTipusAndCodi(
								expedientTipus.getId(),
								codiDocument,
								expedientTipus.getExpedientTipusPare() != null);
					else
						doc = documentRepository.findByDefinicioProcesAndCodi(
								definicioProces, 
								codiDocument);
					
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
					byte[] signatura = pluginHelper.custodiaObtenirSignaturesAmbArxiu(document.getReferenciaCustodia());
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
							arxiuOrigenContingut = pluginHelper.custodiaObtenirSignaturesAmbArxiu(document.getReferenciaCustodia());
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
			return pluginHelper.gestioDocumentalObtenirDocument(
							document.getReferenciaFont());
	}

	private String getUrlComprovacioSignatura(Long documentStoreId, String token) {
		String urlCustodia = pluginHelper.custodiaObtenirUrlComprovacioSignatura(
				documentStoreId.toString());
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
			throw new RuntimeException("Format de token incorrecte", ex);
		}
	}

	private String getTaskInstanceIdPerToken(String token) {
		try {
			String[] tokenDesxifrat = getDocumentTokenUtils().desxifrarTokenMultiple(token);
			if (tokenDesxifrat.length == 2)
				return tokenDesxifrat[0];
			else
				throw new RuntimeException("Format de token incorrecte");
		} catch (Exception ex) {
			throw new RuntimeException("Format de token incorrecte", ex);
		}
	}

	public String getVarPerDocumentCodi(String documentCodi, boolean isAdjunt) {
		if (isAdjunt)
			return JbpmVars.PREFIX_ADJUNT + documentCodi;
		else
			return JbpmVars.PREFIX_DOCUMENT + documentCodi;
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

	public void esborrarVariableInstance(
			String processInstanceId,
			String adjuntId) {

		workflowEngineApi.deleteProcessInstanceVariable(
						processInstanceId,
						getVarPerDocumentCodi(adjuntId, true));
	}
}
