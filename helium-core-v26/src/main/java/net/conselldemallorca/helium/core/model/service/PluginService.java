/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.codahale.metrics.MetricRegistry;

import net.conselldemallorca.helium.core.common.ThreadLocalInfo;
import net.conselldemallorca.helium.core.helper.ExceptionHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ProcesCallbackHelper;
import net.conselldemallorca.helium.core.helperv26.DocumentHelper;
import net.conselldemallorca.helium.core.model.dao.AlertaDao;
import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaCampDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.dao.PluginPortasignaturesDao;
import net.conselldemallorca.helium.core.model.dao.PluginTramitacioDao;
import net.conselldemallorca.helium.core.model.dao.UsuariDao;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.exception.IllegalStateException;
import net.conselldemallorca.helium.core.model.exception.IncidentThrowsAdviceHelper;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.core.model.hibernate.Usuari;
import net.conselldemallorca.helium.core.security.AclServiceDao;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTramitRequest;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;


/**
 * Servei per gestionar les consultes als plugins
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PluginService {

	private PluginPersonaDao pluginPersonaDao;
	private PluginPortasignaturesDao pluginPortasignaturesDao;
	private PluginTramitacioDao pluginTramitacioDao;
	private UsuariDao usuariDao;
	private ExpedientDao expedientDao;
	private DocumentStoreDao documentStoreDao;
	private JbpmHelper jbpmDao;

	private DocumentHelper documentHelper;
	private ExpedientHelper expedientHelper;
	private ExpedientLogHelper expedientLogHelper;

	private DefinicioProcesDao definicioProcesDao;
	private CampDao campDao;
	private ConsultaCampDao consultaCampDao;
	private AlertaDao alertaDao;
	private LuceneDao luceneDao;
	private DtoConverter dtoConverter;
	private AclServiceDao aclServiceDao;

	private ServiceUtils serviceUtils;
	private MessageSource messageSource;
	private MetricRegistry metricRegistry;
	private ProcesCallbackHelper procesCallbackHelper;

	@Autowired
	private ExceptionHelper exceptionHelper;


	public List<PersonaDto> findPersonaLikeNomSencer(String text) {
		return pluginPersonaDao.findLikeNomSencerPlugin(text);
	}
	public PersonaDto findPersonaAmbCodi(String codi) {
		return pluginPersonaDao.findAmbCodiPlugin(codi);
	}
	@Scheduled(fixedRate = 86400000)
	public void personesSync() {
		if (isSyncPersonesActiu()) {
			List<PersonaDto> persones = pluginPersonaDao.findAllPlugin();
			int nsyn = 0;
			int ncre = 0;
			logger.info("Inici de la sincronització de persones (" + persones.size() + " registres)");
	    	for (PersonaDto persona: persones) {
	    		try {
		    		net.conselldemallorca.helium.core.model.hibernate.Persona p = pluginPersonaDao.findAmbCodi(persona.getCodi());
		    		if (p != null) {
		    			p.setNom(persona.getNom());
		    			p.setLlinatges(persona.getLlinatges());
		    			p.setDni(persona.getDni());
		    			p.setEmail(persona.getEmail());
		    		} else {
		    			p = new net.conselldemallorca.helium.core.model.hibernate.Persona();
		    			String codiPerCreacio = (isIgnoreCase()) ? persona.getCodi().toLowerCase() : persona.getCodi();
	    				p.setCodi(codiPerCreacio);
		    			p.setNom(persona.getNom());
		    			p.setLlinatge1((persona.getLlinatge1() != null) ? persona.getLlinatge1(): " ");
		    			p.setLlinatge2(persona.getLlinatge2());
		    			p.setDni(persona.getDni());
		    			p.setEmail(persona.getEmail());
		    			pluginPersonaDao.saveOrUpdate(p);
		    			pluginPersonaDao.flush();
		    			Usuari usuari = usuariDao.getById(codiPerCreacio, false);
		    			if (usuari == null) {
		    				usuari = new Usuari();
		    				usuari.setCodi(codiPerCreacio);
		    				usuari.setContrasenya(codiPerCreacio);
		    				usuariDao.saveOrUpdate(usuari);
		    				usuariDao.flush();
		    			}
		    			ncre++;
		    		}
	    		} catch (Exception ex) {
	    			logger.error("Error en la importació de la persona amb codi " + persona.getCodi(), ex);
	    		}
	    		nsyn++;
	    	}
	    	logger.info("Fi de la sincronització de persones (processades: " + nsyn + ", creades: " + ncre + ")");
		}
	}

	public void enviarPortasignatures(
			Long documentId,
			List<Long> annexosId,
			PersonaDto persona,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3,
			Expedient expedient,
			String importancia,
			Date dataLimit,
			Long tokenId,
			Long processInstanceId,
			String transicioOK,
			String transicioKO) throws PluginException {
		try {
			DocumentDto document = documentHelper.getDocumentVista(
					documentId,
					true,
					true);
			List<DocumentDto> annexos = null;
			if (annexosId != null) {
				annexos = new ArrayList<DocumentDto>();
				for (Long docId: annexosId) {
					DocumentDto docDto = documentHelper.getDocumentVista(
							docId,
							false,
							false);
					if (docDto != null){
						annexos.add(docDto);
					}
				}
			}	
			Integer doc = pluginPortasignaturesDao.uploadDocument(
					document,
					annexos,
					persona,
					personesPas1,
					minSignatarisPas1,
					personesPas2,
					minSignatarisPas2,
					personesPas3,
					minSignatarisPas3,
					expedient,
					importancia,
					dataLimit);
			IncidentThrowsAdviceHelper.addDadesAdvicePortasignatures(doc);
			Calendar cal = Calendar.getInstance();
			Portasignatures portasignatures = new Portasignatures();
			portasignatures.setDocumentId(doc);
			portasignatures.setTokenId(tokenId);
			portasignatures.setDataEnviat(cal.getTime());
			portasignatures.setEstat(TipusEstat.PENDENT);
			portasignatures.setDocumentStoreId(documentId);
			portasignatures.setTransicioOK(transicioOK);
			portasignatures.setTransicioKO(transicioKO);
			portasignatures.setExpedient(expedient);
			portasignatures.setProcessInstanceId(processInstanceId.toString());
			pluginPortasignaturesDao.saveOrUpdate(portasignatures);
		} catch (Exception e) {
			throw new PluginException(getServiceUtils().getMessage("error.pluginService.pujarDocument"), e);
		}
	}

	public void deletePortasignatures(
			Integer documentId) throws PluginException {
		pluginPortasignaturesDao.deleteDocument(documentId);
	}
			
	public boolean processarDocumentCallbackPortasignatures(
			Integer id,
			boolean rebujat,
			String motiuRebuig) {
		try {
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
					
					if (!procesCallbackHelper.isDocumentEnProces(portasignatures.getDocumentId())) {
						procesCallbackHelper.afegirDocument(portasignatures.getDocumentId());
						try {
							processarDocumentPendentPortasignatures(id, portasignatures);
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
					logger.error("El document rebut al callback (id=" + id + ") no està pendent del callback, el seu estat és " + portasignatures.getEstat());
				}
			} else {
				logger.error("El document rebut al callback (id=" + id + ") no s'ha trobat entre els documents enviats al portasignatures");
			}
		} catch (Exception ex) {
			logger.error("El document rebut al callback (id=" + id + ") ha produit una excepció al ser processat: " + ex.getMessage());
			logger.debug("El document rebut al callback (id=" + id + ") ha produit una excepció al ser processat", ex);
		}
		return false;
	}

	public boolean processarDocumentPendentPortasignatures(Integer id) {
		return processarDocumentPendentPortasignatures(
				id,
				pluginPortasignaturesDao.findByDocument(id));
	}

	public void publicarExpedient(
			PublicarExpedientRequest request) {
		pluginTramitacioDao.publicarExpedient(request);
	}
	public void publicarEvent(
			PublicarEventRequest request) {
		pluginTramitacioDao.publicarEvent(request);
	}
	public DadesTramit obtenirDadesTramit(
			ObtenirDadesTramitRequest request) {
		return pluginTramitacioDao.obtenirDadesTramit(request);
	}
	public DadesVistaDocument obtenirVistaDocument(
			ObtenirVistaDocumentRequest request) {
		return pluginTramitacioDao.obtenirVistaDocument(request);
	}
	public void comunicarResultatProcesTramit(
			ResultatProcesTramitRequest request) {
		pluginTramitacioDao.comunicarResultatProcesTramit(request);
	}



	@Autowired
	public void setPluginPersonaDao(PluginPersonaDao pluginPersonaDao) {
		this.pluginPersonaDao = pluginPersonaDao;
	}
	@Autowired
	public void setUsuariDao(UsuariDao usuariDao) {
		this.usuariDao = usuariDao;
	}
	@Autowired
	public void setPluginPortasignaturesDao(
			PluginPortasignaturesDao pluginPortasignaturesDao) {
		this.pluginPortasignaturesDao = pluginPortasignaturesDao;
	}
	@Autowired
	public void setPluginTramitacioDao(PluginTramitacioDao pluginTramitacioDao) {
		this.pluginTramitacioDao = pluginTramitacioDao;
	}
	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
	}
	@Autowired
	public void setDocumentStoreDao(DocumentStoreDao documentStoreDao) {
		this.documentStoreDao = documentStoreDao;
	}
	@Autowired
	public void setDocumentHelper(DocumentHelper documentHelper) {
		this.documentHelper = documentHelper;
	}
	@Autowired
	public void setExpedientLogHelper(ExpedientLogHelper expedientLogHelper) {
		this.expedientLogHelper = expedientLogHelper;
	}
	@Autowired
	public void setExpedientHelper(ExpedientHelper expedientHelper) {
		this.expedientHelper = expedientHelper;
	}
	@Autowired
	public void setJbpmHelper(JbpmHelper jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setCampDao(CampDao campDao) {
		this.campDao = campDao;
	}
	@Autowired
	public void setConsultaCampDao(ConsultaCampDao consultaCampDao) {
		this.consultaCampDao = consultaCampDao;
	}
	@Autowired
	public void setAlertaDao(AlertaDao alertaDao) {
		this.alertaDao = alertaDao;
	}
	@Autowired
	public void setLuceneDao(LuceneDao luceneDao) {
		this.luceneDao = luceneDao;
	}
	@Autowired
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}
	@Autowired
	public void setAclServiceDao(AclServiceDao aclServiceDao) {
		this.aclServiceDao = aclServiceDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Autowired
	public void setMetricRegistry(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}
	@Autowired
	public void setProcesCallbackHelper(ProcesCallbackHelper procesCallbackHelper) {
		this.procesCallbackHelper = procesCallbackHelper;
	}


	private boolean processarDocumentPendentPortasignatures(
			Integer id,
			Portasignatures portasignatures) {
		boolean resposta = false;
		if (portasignatures != null) {
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
						if (portasignatures.getDataSignalIntent() == null)
							portasignatures.setDataSignalIntent(new Date());
						portasignatures.setDataSignalOk(new Date());
						portasignatures.setEstat(TipusEstat.PROCESSAT);
						
						// Guarda el document
						if (portasignatures.getDataCustodiaIntent() == null) {
							portasignatures.setDataCustodiaIntent(new Date());
						}
						afegirDocumentCustodia(
								portasignatures.getDocumentId(),
								documentStore);
						portasignatures.setDataCustodiaOk(new Date());

						JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(
								token.getProcessInstanceId());
						Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());

						if (ExpedientTipusTipusEnumDto.FLOW.equals(expedient.getTipus().getTipus())) {
							// Avança el flux
							jbpmDao.signalToken(
									tokenId.longValue(),
									portasignatures.getTransicioOK());

							//Actualitzem l'estat de l'expedient, ja que si tot el procés de firma i de custòdia
							// ha anat bé, es possible que s'avanci cap al node "fi"
							expedientHelper.verificarFinalitzacioExpedient(
									expedient);
							
							// Reindexa els possibles canvis
							getServiceUtils().expedientIndexLuceneUpdate(
									token.getProcessInstanceId());
						}
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
		}
		return resposta;
	}
	private void afegirDocumentCustodia(
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

	private boolean isSyncPersonesActiu() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.sync.actiu");
		return "true".equalsIgnoreCase(syncActiu);
	}
	private boolean isIgnoreCase() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.ignore.case");
		return "true".equalsIgnoreCase(syncActiu);
	}

	private ServiceUtils getServiceUtils() {
		if (serviceUtils == null) {
			serviceUtils = new ServiceUtils(
					expedientDao,
					definicioProcesDao,
					campDao,
					consultaCampDao,
					luceneDao,
					dtoConverter,
					jbpmDao,
					aclServiceDao,
					messageSource,
					metricRegistry);
		}
		return serviceUtils;
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
			alertaDao.saveOrUpdate(alerta);
		}
	}
	
	/*private void verificarFinalitzacioExpedient(String processInstanceId) {
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		if (pi.getEnd() != null) {
			// Actualitzar data de fi de l'expedient
			expedient.setDataFi(pi.getEnd());
			// Finalitzar terminis actius
			List<TerminiIniciat> llistaTerminis = terminiIniciatDao.findAmbProcessInstanceId(pi.getId());
			if (llistaTerminis != null && !llistaTerminis.isEmpty()) {
				for (TerminiIniciat terminiIniciat: llistaTerminis) {
					if (terminiIniciat.getDataInici() != null) {
						terminiIniciat.setDataCancelacio(new Date());
						long[] timerIds = terminiIniciat.getTimerIdsArray();
						for (int i = 0; i < timerIds.length; i++)
							jbpmDao.suspendTimer(
									timerIds[i],
									new Date(Long.MAX_VALUE));
					}
				}
			}
		}
	}*/

	/*private void enviarCorreuErrorPsigna(
			String subject,
			String text,
			Throwable tr) {
		String propRecipients = GlobalProperties.getInstance().getProperty("app.portasignatures.debug.email");
		if (propRecipients != null && propRecipients.trim().length() > 0) {
			List<String> recipients = new ArrayList<String>();
			if (propRecipients.contains(",")) {
				String[] recs = propRecipients.trim().split(",");
				for (String rec: recs) {
					if (rec.trim().length() > 0)
						recipients.add(rec.trim());
				}
			} else {
				recipients.add(propRecipients.trim());
			}
			StringBuilder sb = new StringBuilder();
			sb.append(text);
			if (tr != null) {
				sb.append(text);
				sb.append(getMissageFinalCadenaExcepcions(tr));
			}
			try {
				mailDao.send(
						GlobalProperties.getInstance().getProperty("app.correu.remitent"),
						recipients,
						null,
						null,
						subject,
						sb.toString());
			} catch (Exception ex) {
				logger.error("No s'ha pogut enviar el correu d'error del portasignatures: " + subject, ex);
			}
		}
		
		
	}*/

	private static final Log logger = LogFactory.getLog(PluginService.class);

}
