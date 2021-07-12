/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.DocumentHelper;
import es.caib.helium.logic.helper.ExceptionHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.IndexHelper;
import es.caib.helium.logic.helper.MessageServiceHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.logic.helper.PortasignaturesHelper;
import es.caib.helium.logic.helper.ProcesCallbackHelper;
import es.caib.helium.logic.intf.WProcessInstance;
import es.caib.helium.logic.intf.WToken;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi.ExpedientRetroaccioTipus;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.PortasignaturesDto;
import es.caib.helium.logic.intf.service.PortasignaturesService;
import es.caib.helium.persist.entity.Alerta;
import es.caib.helium.persist.entity.DocumentStore;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.Portasignatures;
import es.caib.helium.persist.entity.Portasignatures.TipusEstat;
import es.caib.helium.persist.entity.Portasignatures.Transicio;
import es.caib.helium.persist.repository.AlertaRepository;
import es.caib.helium.persist.repository.DocumentStoreRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.PortasignaturesRepository;
import es.caib.helium.persist.util.ThreadLocalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * Implementació dels mètodes del servei PortasignaturesService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PortasignaturesServiceImpl implements PortasignaturesService {

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PortasignaturesHelper portasignaturesHelper;
	@Autowired
	PortasignaturesRepository portasignaturesRepository;
	@Autowired
	DocumentStoreRepository documentStoreRepository;
	@Autowired
	ExpedientRepository expedientRepository;
	@Autowired
	AlertaRepository alertaRepository;
	
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Resource
	private ProcesCallbackHelper procesCallbackHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ExceptionHelper exceptionHelper;
	@Resource
	private DocumentHelper documentHelper;
	@Resource
	private MessageServiceHelper messageHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public boolean processarDocumentCallback(
			Integer documentId, 
			boolean rebujat, 
			String motiuRebuig) {
		
		logger.debug(
				"Processar callback portasignatures (" +
				"documentId=" + documentId + ", " +
				"rebujat=" + rebujat + ", " +
				"motiuRebuig=" + motiuRebuig + ")");
		
		boolean success = false;
		try {
			Portasignatures portasignatures = portasignaturesRepository.findByDocumentId(documentId);
			if (portasignatures != null) {
				if (TipusEstat.PENDENT.equals(portasignatures.getEstat())) {
					portasignatures.setDataSignatRebutjat(new Date());
					if (!rebujat) {
						portasignatures.setEstat(TipusEstat.SIGNAT);
						portasignatures.setTransition(Transicio.SIGNAT);
					} else {
						portasignatures.setEstat(TipusEstat.REBUTJAT);
						portasignatures.setTransition(Transicio.REBUTJAT);
						portasignatures.setMotiuRebuig(motiuRebuig);
					}
					portasignaturesRepository.save(portasignatures);
					
					if (!procesCallbackHelper.isDocumentEnProces(portasignatures.getDocumentId())) {
						procesCallbackHelper.afegirDocument(portasignatures.getDocumentId());
						try {
							processarDocumentPendentPortasignatures(documentId, portasignatures);
						} finally {
							if (procesCallbackHelper.isDocumentEnProces(portasignatures.getDocumentId()))
								procesCallbackHelper.eliminarDocument(portasignatures.getDocumentId());
						}
					}
					
					return true;
				} else if (TipusEstat.ESBORRAT.equals(portasignatures.getEstat())) {
					return true;
				} else if (TipusEstat.PROCESSAT.equals(portasignatures.getEstat())) {
					return true;
				} else {
					logger.error("El document rebut al callback (documentId=" + documentId + ") no està pendent del callback, el seu estat és " + portasignatures.getEstat());
				}
			} else {
				logger.error("El document rebut al callback (documentId=" + documentId + ") no s'ha trobat entre els documents enviats al portasignatures");
			}
		} catch (Exception ex) {
			logger.error("El document rebut al callback (documentId=" + documentId + ") ha produit una excepció al ser processat: " + ex.getMessage());
			logger.debug("El document rebut al callback (documentId=" + documentId + ") ha produit una excepció al ser processat", ex);
		}
		return success;
	}
	
	/** Processa la petició del portafirmes guardant el document en cas d'estar firmat i actualitzant el resultat del processament.
	 * 
	 * @param documentId
	 * @param portasignatures
	 * @return
	 */
	private boolean processarDocumentPendentPortasignatures(
			Integer documentId,
			Portasignatures portasignatures) {
		boolean resposta = false;
		if (portasignatures != null) {
			if (portasignatures.getDataProcessamentPrimer() == null)
				portasignatures.setDataProcessamentPrimer(new Date());
			portasignatures.setDataProcessamentDarrer(new Date());
			Long tokenId = portasignatures.getTokenId();
			WToken token = workflowEngineApi.getTokenById(tokenId.toString());
			DocumentStore documentStore = documentStoreRepository.getById(portasignatures.getDocumentStoreId());
			if (documentStore != null) {
				if (TipusEstat.SIGNAT.equals(portasignatures.getEstat()) ||
					(TipusEstat.ERROR.equals(portasignatures.getEstat()) && Transicio.SIGNAT.equals(portasignatures.getTransition()))) {
					// Processa els documents signats
					try {
						ThreadLocalInfo.clearProcessInstanceFinalitzatIds();
						workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
								token.getProcessInstanceId(),
								ExpedientRetroaccioTipus.PROCES_DOCUMENT_SIGNAR,
								new Boolean(true).toString());
						
						if (portasignatures.getDataSignalIntent() == null)
							portasignatures.setDataSignalIntent(new Date());
						portasignatures.setDataSignalOk(new Date());
						portasignatures.setEstat(TipusEstat.PROCESSAT);
						
						// Guarda el document
						if (documentStore.getReferenciaCustodia() == null) {
							if (portasignatures.getDataCustodiaIntent() == null)
								portasignatures.setDataCustodiaIntent(new Date());
							afegirDocumentCustodia(
									portasignatures.getDocumentId(),
									documentStore);
							portasignatures.setDataCustodiaOk(new Date());
						}						
						// Avança el flux
						workflowEngineApi.signalToken(
								tokenId.longValue(),
								portasignatures.getTransicioOK());

						//Actualitzem l'estat de l'expedient, ja que si tot el procés de firma i de custòdia
						// ha anat bé, es possible que s'avanci cap al node "fi"
						WProcessInstance rootProcessInstance = workflowEngineApi.getRootProcessInstance(
								token.getProcessInstanceId());
						Expedient expedient = expedientRepository.findByProcessInstanceId(rootProcessInstance.getId());
						expedientHelper.verificarFinalitzacioExpedient(
								expedient);
						
						// Reindexa els possibles canvis
						indexHelper.expedientIndexLuceneUpdate(
								token.getProcessInstanceId());

						resposta = true;

//					} catch (PluginException pex) {
//						errorProcesPsigna(
//								portasignatures,
//								exceptionHelper.getMissageFinalCadenaExcepcions(pex));
//						logger.error("Error al processar el document firmat pel callback (documentId=" + portasignatures.getDocumentId() + "): " + exceptionHelper.getMissageFinalCadenaExcepcions(pex), pex);
					} catch (Exception ex) {
						errorProcesPsigna(
								portasignatures,
								exceptionHelper.getMissageFinalCadenaExcepcions(ex));
						logger.error("Error al processar el document firmat pel callback (documentId=" + portasignatures.getDocumentId() + ")", ex);
					}
					portasignaturesRepository.save(portasignatures);
				} else if (TipusEstat.REBUTJAT.equals(portasignatures.getEstat()) ||
						(TipusEstat.ERROR.equals(portasignatures.getEstat()) && Transicio.REBUTJAT.equals(portasignatures.getTransition()))) {
					// Processa els documents rebujats
					try {
						workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
								token.getProcessInstanceId(),
								ExpedientRetroaccioTipus.PROCES_DOCUMENT_SIGNAR,
								new Boolean(false).toString());						
						workflowEngineApi.signalToken(
								tokenId.longValue(),
								portasignatures.getTransicioKO());
						portasignatures.setEstat(TipusEstat.PROCESSAT);
						indexHelper.expedientIndexLuceneUpdate(token.getProcessInstanceId());
						resposta = true;
					} catch (Exception ex) {
						errorProcesPsigna(
								portasignatures,
								exceptionHelper.getMissageFinalCadenaExcepcions(ex));
						logger.error("Error al processar el document rebutjat pel callback (id=" + portasignatures.getDocumentId() + ")", ex);
					}
					portasignaturesRepository.save(portasignatures);
				} else {
					String error = "El document de portasignatures (id=" + portasignatures.getDocumentId() + ") no està pendent de processar, està en estat " + portasignatures.getEstat().toString();
					errorProcesPsigna(
							portasignatures,
							error);
					logger.error(error);
				}
			} else {
				String error = "El document rebut al callback (id=" + portasignatures.getDocumentId() + ") fa referència a un documentStore inexistent (id=" + portasignatures.getDocumentStoreId() + ")";
				errorProcesPsigna(
						portasignatures,
						error);
				logger.error(error);
			}
			List<Portasignatures> ambErrors = portasignaturesRepository.findAmbExpedientIdIEstat(
					portasignatures.getExpedient().getId(), 
					TipusEstat.ERROR);
			
			if (ambErrors.size() > 0)
				portasignatures.getExpedient().setErrorsIntegracions(true);
			else
				portasignatures.getExpedient().setErrorsIntegracions(false);
		} else {
			logger.error("El document de portasignatures (id=" + documentId + ") no s'ha trobat");
		}
		return resposta;
	}

	private void afegirDocumentCustodia(
			Integer psignaId,
			DocumentStore documentStore) throws Exception {
		Long documentStoreId = documentStore.getId();
		DocumentDto document = documentHelper.getDocumentSenseContingut(documentStoreId);
		if (document != null) {
			List<byte[]> signatures = pluginHelper.obtenirSignaturesDocument(psignaId);
			if (signatures != null && signatures.size() == 1) {				
				documentHelper.guardarDocumentFirmat(
						documentStore,
						signatures.get(0));
			} else {
				if (signatures == null) {
					throw new Exception(messageHelper.getMessage("error.pluginService.capSignatura"));
				} else {
					throw new Exception(
							"El nombre de signatures del document és de " + signatures.size() + " en comptes de 1");
				}
			}
		} else {
			throw new IllegalStateException(messageHelper.getMessage("error.pluginService.noDisponible"));
		}
	}

	private void errorProcesPsigna(
			Portasignatures portasignatures,
			String errorCallback) {
		portasignatures.setErrorCallbackProcessant(errorCallback);
		portasignatures.setEstat(TipusEstat.ERROR);
		String expedientResponsable = portasignatures.getExpedient().getResponsableCodi();
		if (expedientResponsable != null) {
			Alerta alerta = new Alerta(
					new Date(),
					expedientResponsable,
					"",
					portasignatures.getExpedient().getEntorn());
			alerta.setExpedient(portasignatures.getExpedient());
			DocumentDto document = documentHelper.getDocumentSenseContingut(portasignatures.getDocumentStoreId());
			String causa = null;
			if (document != null)
				causa = "Error al processar resposta del portasignatures per al document \"" + document.getDocumentNom() + "\": " + errorCallback;
			else
				causa = "Error al processar resposta del portasignatures amb id " + portasignatures.getDocumentId();
			if (causa.length() > 255)
				alerta.setCausa(causa.substring(0, 248) + "[...]");
			else
				alerta.setCausa(causa);
			alertaRepository.save(alerta);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly= true)
	public PortasignaturesDto getByDocumentId(Integer documentId) {
		return conversioTipusServiceHelper.convertir(
				portasignaturesHelper.getByDocumentId(documentId),
				PortasignaturesDto.class);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(PortasignaturesServiceImpl.class);
}
