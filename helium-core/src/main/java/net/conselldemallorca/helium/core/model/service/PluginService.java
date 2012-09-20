/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaCampDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.dao.PluginPortasignaturesDao;
import net.conselldemallorca.helium.core.model.dao.PluginTramitacioDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dao.UsuariDao;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.exception.IllegalStateException;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.core.model.hibernate.Usuari;
import net.conselldemallorca.helium.core.security.acl.AclServiceDao;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesTramit;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.DadesVistaDocument;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirVistaDocumentRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ResultatProcesTramitRequest;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les consultes als plugins
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PluginService {

	private PluginPersonaDao pluginPersonaDao;
	private PluginPortasignaturesDao pluginPortasignaturesDao;
	private PluginCustodiaDao pluginCustodiaDao;
	private PluginTramitacioDao pluginTramitacioDao;
	private UsuariDao usuariDao;
	private ExpedientDao expedientDao;
	private RegistreDao registreDao;
	private DocumentStoreDao documentStoreDao;
	private JbpmDao jbpmDao;

	private DocumentHelper documentHelper;
	private ExpedientLogHelper expedientLogHelper;

	private DefinicioProcesDao definicioProcesDao;
	private CampDao campDao;
	private ConsultaCampDao consultaCampDao;
	private LuceneDao luceneDao;
	private DtoConverter dtoConverter;
	private AclServiceDao aclServiceDao;

	private ServiceUtils serviceUtils;
	private MessageSource messageSource;



	public List<PersonaDto> findPersonaLikeNomSencer(String text) {
		return pluginPersonaDao.findLikeNomSencerPlugin(text);
	}
	public PersonaDto findPersonaAmbCodi(String codi) {
		return pluginPersonaDao.findAmbCodiPlugin(codi);
	}
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
			String transicioOK,
			String transicioKO) throws Exception {
		try {
			DocumentDto document = documentHelper.getDocumentVista(
					documentId,
					true,
					true);
			List<DocumentDto> annexos = null;
			if (annexosId != null) {
				annexos = new ArrayList<DocumentDto>();
				for (Long docId: annexosId) {
					annexos.add(documentHelper.getDocumentVista(
							docId,
							false,
							false));
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
			Calendar cal = Calendar.getInstance();
			Portasignatures portasignatures = new Portasignatures();
			portasignatures.setDocumentId(doc);
			portasignatures.setTokenId(tokenId);
			portasignatures.setDataEnviat(cal.getTime());
			portasignatures.setEstat(TipusEstat.PENDENT);
			portasignatures.setDocumentStoreId(documentId);
			portasignatures.setTransicioOK(transicioOK);
			portasignatures.setTransicioKO(transicioKO);
			pluginPortasignaturesDao.saveOrUpdate(portasignatures);
		} catch (Exception e) {
			throw new JbpmException(getServiceUtils().getMessage("error.pluginService.pujarDocument"), e);
		}
	}
	public Double processarDocumentSignatPortasignatures(Integer id) throws Exception {
		Double resposta = -1D;
		Portasignatures portasignatures = pluginPortasignaturesDao.findByDocument(id);
		if (portasignatures != null) {
			Long tokenId = portasignatures.getTokenId();
			JbpmToken token = jbpmDao.getTokenById(tokenId.toString());
			expedientLogHelper.afegirLogExpedientPerProces(
					token.getProcessInstanceId(),
					ExpedientLogAccioTipus.PROCES_DOCUMENT_SIGNAR,
					new Boolean(true).toString());
			DocumentStore documentStore = documentStoreDao.getById(portasignatures.getDocumentStoreId(), false);
			//logger.info(">>> [PSIGN] Processant document signat (id=" + portasignatures.getId() + ", psignaId=" + portasignatures.getDocumentId() + ", docStoreId=" + portasignatures.getDocumentStoreId() + ", trobat=" + documentStore != null + ")");
			if (	(portasignatures.getEstat() != TipusEstat.SIGNAT) &&
					(portasignatures.getTransition() != Transicio.SIGNAT) &&
					(!documentStore.isSignat())) {
				/*if (documentStore.getReferenciaCustodia() != null)
					logger.warn("El document rebut al callback (id=" + id + ") ja ha estat custodiat amb anterioritat (però es segueix amb el seu procés)");*/
				//logger.info(">>> [PSIGN] Abans funció custòdia (id=" + portasignatures.getId() + ", psignaId=" + portasignatures.getDocumentId() + ", docStoreId=" + portasignatures.getDocumentStoreId() + ")");
				afegirDocumentCustodia(
						portasignatures.getDocumentId(),
						portasignatures.getDocumentStoreId());
				//logger.info(">>> [PSIGN] Després funció custòdia (id=" + portasignatures.getId() + ", psignaId=" + portasignatures.getDocumentId() + ", docStoreId=" + portasignatures.getDocumentStoreId() + ")");
			}
			portasignatures.setEstat(TipusEstat.SIGNAT);
			portasignatures.setTransition(Transicio.SIGNAT);
			pluginPortasignaturesDao.saveOrUpdate(portasignatures);
			jbpmDao.signalToken(
					tokenId.longValue(),
				portasignatures.getTransicioOK());
			getServiceUtils().expedientIndexLuceneUpdate(
					jbpmDao.getTokenById(tokenId.toString()).getProcessInstanceId());
			resposta = 1D;
		} else {
			logger.error("El document rebut al callback (id=" + id + ") no s'ha trobat en els documents pendents pel portasignatures");
			resposta = -1D;
		}
		return resposta;
	}
	public Double processarDocumentRebutjatPortasignatures(Integer id, String motiuRebuig) throws Exception {
		Double resposta = -1D;
		Portasignatures portasignatures = pluginPortasignaturesDao.findByDocument(id);
		if (portasignatures != null) {
			Long tokenId = portasignatures.getTokenId();
			JbpmToken token = jbpmDao.getTokenById(tokenId.toString());
			expedientLogHelper.afegirLogExpedientPerProces(
					token.getProcessInstanceId(),
					ExpedientLogAccioTipus.PROCES_DOCUMENT_SIGNAR,
					new Boolean(false).toString());
			portasignatures.setEstat(TipusEstat.REBUTJAT);
			portasignatures.setTransition(Transicio.REBUTJAT);
			portasignatures.setMotiuRebuig(motiuRebuig);
			pluginPortasignaturesDao.saveOrUpdate(portasignatures);
			jbpmDao.signalToken(
					tokenId.longValue(),
					portasignatures.getTransicioKO());
			getServiceUtils().expedientIndexLuceneUpdate(
					jbpmDao.getTokenById(token.toString()).getProcessInstanceId());
			resposta = 1D;
		} else {
			logger.error("El document rebut al callback (id=" + id + ") no s'ha trobat en els documents pendents pel portasignatures");
		}
		return resposta;
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
	public void setPluginCustodiaDao(PluginCustodiaDao pluginCustodiaDao) {
		this.pluginCustodiaDao = pluginCustodiaDao;
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
	public void setRegistreDao(RegistreDao registreDao) {
		this.registreDao = registreDao;
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
	public void setJbpmDao(JbpmDao jbpmDao) {
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



	private void afegirDocumentCustodia(
			Integer documentId,
			Long documentStoreId) throws Exception {
		DocumentDto document = documentHelper.getDocumentSenseContingut(documentStoreId);
		if (document != null) {
			DocumentStore docst = documentStoreDao.getById(documentStoreId, false);
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(docst.getProcessInstanceId());
			Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
			String varDocumentCodi = docst.getJbpmVariable().substring(DocumentHelper.PREFIX_VAR_DOCUMENT.length());
			List<byte[]> signatures = obtenirSignaturesDelPortasignatures(documentId);
			if (signatures != null) {
				//logger.info(">>> [PSIGN] Té signatures i comença custòdia (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + docst.getReferenciaCustodia() + ")");
				if (docst.getReferenciaCustodia() != null) {
					//logger.info(">>> [PSIGN] Abans esborrar signatures (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + docst.getReferenciaCustodia() + ")");
					pluginCustodiaDao.esborrarSignatures(docst.getReferenciaCustodia());
					//logger.info(">>> [PSIGN] Després esborrar signatures (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + docst.getReferenciaCustodia() + ")");
				}
				String referenciaCustodia = null;
				for (byte[] signatura: signatures) {
					try {
						//logger.info(">>> [PSIGN] Abans cridada custòdia 1 (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ")");
						referenciaCustodia = pluginCustodiaDao.afegirSignatura(
								documentStoreId,
								docst.getReferenciaFont(),
								document.getArxiuNom(),
								document.getCustodiaCodi(),
								signatura);
						//logger.info(">>> [PSIGN] Després cridada custòdia 1 (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + referenciaCustodia + ")");
					} catch (Exception ex) {
						// Si dona error perquè el document ja està arxivat l'esborra
						// i el torna a crear.
						//logger.info(">>> [PSIGN] Error custòdia (" + ex.getCause().getMessage() + ") (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + referenciaCustodia + ")");
						/*if (cercarMissatgeDinsCadenaExcepcions("ERROR_DOCUMENTO_ARCHIVADO", ex)) {
							logger.info(">>> [PSIGN] Abans cridada esborrar signatures (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + referenciaCustodia + ")");
							pluginCustodiaDao.esborrarSignatures(documentStoreId.toString());
							logger.info(">>> [PSIGN] Després cridada esborrar signatures (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + referenciaCustodia + ")");
							logger.info(">>> [PSIGN] Abans cridada custòdia 2 (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + referenciaCustodia + ")");
							referenciaCustodia = pluginCustodiaDao.afegirSignatura(
									documentStoreId,
									docst.getReferenciaFont(),
									document.getArxiuNom(),
									document.getCustodiaCodi(),
									signatura);
							logger.info(">>> [PSIGN] Després cridada custòdia 2 (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + referenciaCustodia + ")");
						} else {
							throw ex;
						}*/
						if (cercarMissatgeDinsCadenaExcepcions("ERROR_DOCUMENTO_ARCHIVADO", ex)) {
							referenciaCustodia = documentStoreId.toString();
						} else {
							throw ex;
						}
					}
				}
				//logger.info(">>> [PSIGN] Fi procés custòdia 1 (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + referenciaCustodia + ")");
				docst.setReferenciaCustodia(referenciaCustodia);
				docst.setSignat(true);
				registreDao.crearRegistreSignarDocument(
						expedient.getId(),
						docst.getProcessInstanceId(),
						SecurityContextHolder.getContext().getAuthentication().getName(),
						varDocumentCodi);
				//logger.info(">>> [PSIGN] Fi procés custòdia 2 (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + referenciaCustodia + ")");
			} else {
				//logger.info(">>> [PSIGN] No té signatures (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ", refCustòdia=" + docst.getReferenciaCustodia() + ")");
				throw new Exception(getServiceUtils().getMessage("error.pluginService.capSignatura"));
			}
		} else {
			//logger.info(">>> [PSIGN] No s'ha trobat diocument (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ")");
			throw new IllegalStateException(getServiceUtils().getMessage("error.pluginService.noDisponible"));
		}
		//logger.info(">>> [PSIGN] Fi procés custòdia 3 (psignaId=" + documentId + ", docStoreId=" + documentStoreId + ")");
	}
	private List<byte[]> obtenirSignaturesDelPortasignatures(
			Integer documentId) {
		try {
			return pluginPortasignaturesDao.obtenirSignaturesDocument(documentId);
		} catch (Exception e) {
			logger.error("Error obtenint el document del Portasignatures", e);
			return null;
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
					messageSource);
		}
		return serviceUtils;
	}

	private boolean cercarMissatgeDinsCadenaExcepcions(String missatge, Throwable ex) {
		//logger.info(">>> [PSIGN] Cercant missatge dins excepcio (missatge=" + missatge + ", getMessage=" + ex.getMessage() + ")");
		if (ex.getMessage().contains(missatge))
			return true;
		if (ex.getCause() != null)
			return cercarMissatgeDinsCadenaExcepcions(missatge, ex.getCause());
		else
			return false;
	}

	private static final Log logger = LogFactory.getLog(PluginService.class);

}
