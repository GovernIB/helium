package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Collections;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;

@Component
public class PortafirmesCallbackHelper {

	@Autowired
	private PortasignaturesRepository portasignaturesRepository;

	private List<Integer> idsDocumentsProcessant = new ArrayList<Integer>();

	@Transactional
	public boolean processarDocumentPendentPortasignatures(Integer id) {
		return processarDocumentPendentPortasignatures(
				id,
				portasignaturesRepository.findByDocumentId(id));
	}

	@Transactional
	public boolean processarDocumentCallbackPortasignatures(
			long id,
			boolean rebujat,
			String motiuRebuig) {
		/*try {
			Portasignatures portasignatures = pluginPortasignaturesDao.findByDocument(id);
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
					pluginPortasignaturesDao.saveOrUpdate(portasignatures);
					if (!isDocumentEnProces(portasignatures.getDocumentId())) {
						afegirDocument(portasignatures.getDocumentId());
						try {
							processarDocumentPendentPortasignatures(id, portasignatures);
						} finally {
							if (isDocumentEnProces(portasignatures.getDocumentId()))
								eliminarDocument(portasignatures.getDocumentId());
						}
					}
					return true;
				} else if (TipusEstat.ESBORRAT.equals(portasignatures.getEstat())) {
					return true;
				} else if (TipusEstat.PROCESSAT.equals(portasignatures.getEstat())) {
					return true;
				} else {
					logger.error("El document rebut al callback (id=" + id + ") no està pendent del callback, el seu estat és " + portasignatures.getEstat());
				}
			} else {
				logger.error("El document rebut al callback (id=" + id + ") no s'ha trobat entre els documents enviats al portasignatures");
			}
		} catch (Exception ex) {
			logger.error("El document rebut al callback (id=" + id + ") ha produit una excepció al ser processat: " + ex.getMessage());
			logger.debug("El document rebut al callback (id=" + id + ") ha produit una excepció al ser processat", ex);
		}
		return false;*/
		return false;
	}

	public void afegirDocument(Integer idDocument) {
		this.idsDocumentsProcessant.add(idDocument);
	}

	public boolean eliminarDocument(Integer idDocument) {
		if (this.idsDocumentsProcessant.contains(idDocument))
			return this.idsDocumentsProcessant.removeAll(Collections.singleton(idDocument));
		return false;
	}

	public boolean isDocumentEnProces(Integer idDocucument) {
		return this.idsDocumentsProcessant.contains(idDocucument);
	}

	private boolean processarDocumentPendentPortasignatures(
			Integer id,
			Portasignatures portasignatures) {
		boolean resposta = false;
		/*if (portasignatures != null) {
			if (portasignatures.getDataProcessamentPrimer() == null)
				portasignatures.setDataProcessamentPrimer(new Date());
			portasignatures.setDataProcessamentDarrer(new Date());
			Long tokenId = portasignatures.getTokenId();
			JbpmToken token = jbpmDao.getTokenById(tokenId.toString());
			DocumentStore documentStore = documentStoreDao.getById(portasignatures.getDocumentStoreId(), false);
			if (documentStore != null) {
				if (TipusEstat.SIGNAT.equals(portasignatures.getEstat()) ||
					(TipusEstat.ERROR.equals(portasignatures.getEstat()) && Transicio.SIGNAT.equals(portasignatures.getTransition()))) {
					// Processa els documents signats
					try {
						ThreadLocalInfo.clearProcessInstanceFinalitzatIds();
						expedientLogHelper.afegirLogExpedientPerProces(
								token.getProcessInstanceId(),
								ExpedientLogAccioTipus.PROCES_DOCUMENT_SIGNAR,
								new Boolean(true).toString());
						if (documentStore.getReferenciaCustodia() == null) {
							if (portasignatures.getDataCustodiaIntent() == null)
								portasignatures.setDataCustodiaIntent(new Date());
							afegirDocumentCustodia(
									portasignatures.getDocumentId(),
									documentStore);
							portasignatures.setDataCustodiaOk(new Date());
						}
						if (portasignatures.getDataSignalIntent() == null)
							portasignatures.setDataSignalIntent(new Date());
						jbpmDao.signalToken(
								tokenId.longValue(),
								portasignatures.getTransicioOK());
						portasignatures.setDataSignalOk(new Date());
						portasignatures.setEstat(TipusEstat.PROCESSAT);
						getServiceUtils().expedientIndexLuceneUpdate(
								token.getProcessInstanceId());
						
						//Actualitzem l'estat de l'expedient, ja que si tot el procés de firma i de custòdia
						// ha anat bé, es possible que s'avanci cap al node "fi"
						JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(
								token.getProcessInstanceId());
						Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
						expedientHelper.verificarFinalitzacioExpedient(
								expedient);
						resposta = true;
					} catch (PluginException pex) {
						errorProcesPsigna(
								portasignatures,
								exceptionHelper.getMissageFinalCadenaExcepcions(pex));
						logger.error("Error al processar el document firmat pel callback (id=" + portasignatures.getDocumentId() + "): " + exceptionHelper.getMissageFinalCadenaExcepcions(pex), pex);
					} catch (Exception ex) {
						errorProcesPsigna(
								portasignatures,
								exceptionHelper.getMissageFinalCadenaExcepcions(ex));
						logger.error("Error al processar el document firmat pel callback (id=" + portasignatures.getDocumentId() + ")", ex);
					}
					pluginPortasignaturesDao.saveOrUpdate(portasignatures);
				} else if (TipusEstat.REBUTJAT.equals(portasignatures.getEstat()) ||
						(TipusEstat.ERROR.equals(portasignatures.getEstat()) && Transicio.REBUTJAT.equals(portasignatures.getTransition()))) {
					// Processa els documents rebujats
					try {
						expedientLogHelper.afegirLogExpedientPerProces(
								token.getProcessInstanceId(),
								ExpedientLogAccioTipus.PROCES_DOCUMENT_SIGNAR,
								new Boolean(false).toString());
						jbpmDao.signalToken(
								tokenId.longValue(),
								portasignatures.getTransicioKO());
						portasignatures.setEstat(TipusEstat.PROCESSAT);
						getServiceUtils().expedientIndexLuceneUpdate(
								token.getProcessInstanceId());
						resposta = true;
					} catch (Exception ex) {
						errorProcesPsigna(
								portasignatures,
								exceptionHelper.getMissageFinalCadenaExcepcions(ex));
						logger.error("Error al processar el document rebutjat pel callback (id=" + portasignatures.getDocumentId() + ")", ex);
					}
					pluginPortasignaturesDao.saveOrUpdate(portasignatures);
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
			List<Portasignatures> ambErrors = pluginPortasignaturesDao.findAmbErrorsPerExpedientId(portasignatures.getExpedient().getId());
			if (ambErrors.size() > 0)
				portasignatures.getExpedient().setErrorsIntegracions(true);
			else
				portasignatures.getExpedient().setErrorsIntegracions(false);
		} else {
			logger.error("El document de portasignatures (id=" + id + ") no s'ha trobat");
		}*/
		return resposta;
	}
	/*private void afegirDocumentCustodia(
			Integer psignaId,
			DocumentStore documentStore) throws Exception {
		Long documentStoreId = documentStore.getId();
		DocumentDto document = documentHelper.getDocumentSenseContingut(documentStoreId);
		if (document != null) {
			List<byte[]> signatures = pluginPortasignaturesDao.obtenirSignaturesDocument(
					psignaId);
			if (signatures != null && signatures.size() == 1) {
				documentHelper.guardarDocumentFirmat(
						documentStore,
						signatures.get(0));
			} else {
				if (signatures == null) {
					throw new Exception(getServiceUtils().getMessage("error.pluginService.capSignatura"));
				} else {
					throw new Exception(
							"El nombre de signatures del document és de " + signatures.size() + " en comptes de 1");
				}
			}
		} else {
			throw new IllegalStateException(getServiceUtils().getMessage("error.pluginService.noDisponible"));
		}
	}

	private static final Log logger = LogFactory.getLog(PortafirmesCallbackHelper.class);*/

}
