/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.model.dao.ExpedientDao;
import net.conselldemallorca.helium.model.dao.PermisDao;
import net.conselldemallorca.helium.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.model.dao.PluginPortasignaturesDao;
import net.conselldemallorca.helium.model.dao.RegistreDao;
import net.conselldemallorca.helium.model.dao.UsuariDao;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.model.hibernate.Usuari;
import net.conselldemallorca.helium.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les consultes als plugins
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class PluginService {

	private PluginPersonaDao pluginPersonaDao;
	private PluginPortasignaturesDao pluginPortasignaturesDao;
	private PluginCustodiaDao pluginCustodiaDao;
	private UsuariDao usuariDao;
	private PermisDao permisDao;
	private ExpedientDao expedientDao;
	private RegistreDao registreDao;
	private DocumentStoreDao documentStoreDao;
	private DtoConverter dtoConverter;
	private JbpmDao jbpmDao;



	public List<Persona> findPersonaLikeNomSencer(String text) {
		return pluginPersonaDao.findLikeNomSencerPlugin(text);
	}
	public Persona findPersonaAmbCodi(String codi) {
		return pluginPersonaDao.findAmbCodiPlugin(codi);
	}
	public void personesSync() {
		if (isSyncActiu()) {
			logger.debug("Inici de la sincronització de persones");
			List<Persona> persones = pluginPersonaDao.findAllPlugin();
	    	for (Persona persona: persones) {
	    		net.conselldemallorca.helium.model.hibernate.Persona p = pluginPersonaDao.findAmbCodi(persona.getCodi());
	    		if (p != null) {
	    			p.setNom(persona.getNom());
	    			p.setLlinatges(persona.getLlinatges());
	    			p.setDni(persona.getDni());
	    			p.setEmail(persona.getEmail());
	    		} else {
	    			logger.debug("Nova persona: " + persona.getCodi());
	    			p = new net.conselldemallorca.helium.model.hibernate.Persona();
	    			p.setCodi(persona.getCodi());
	    			p.setNom(persona.getNom());
	    			p.setLlinatge1((persona.getLlinatge1() != null) ? persona.getLlinatge1(): " ");
	    			p.setLlinatge2(persona.getLlinatge2());
	    			p.setDni(persona.getDni());
	    			p.setEmail(persona.getEmail());
	    			pluginPersonaDao.saveOrUpdate(p);
	    			Usuari usuari = usuariDao.getById(persona.getCodi(), false);
	    			if (usuari == null) {
	    				usuari = new Usuari();
	    				usuari.setCodi(persona.getCodi());
	    				usuari.setContrasenya(persona.getCodi());
	    				usuari.addPermis(permisDao.getByCodi("HEL_USER"));
	    				usuariDao.saveOrUpdate(usuari);
	    			}
	    		}
	    	}
	    	logger.debug("Fi de la sincronització de persones");
		}
	}

	public void enviarPortasignatures(
			Persona persona,
			DocumentDto documentDto,
			Expedient expedient,
			String importancia,
			Date dataLimit,
			Long tokenId,
			String transicioOK,
			String transicioKO) throws Exception {
		
		try {
			Integer doc = pluginPortasignaturesDao.UploadDocument(
							persona,
							documentDto,
							expedient,
							importancia,
							dataLimit);
			
			Calendar cal = Calendar.getInstance();
			Portasignatures portasignatures = new Portasignatures();
			portasignatures.setDocumentId(doc);
			portasignatures.setTokenId(tokenId);
			portasignatures.setDataEnviat(cal.getTime());
			portasignatures.setEstat(TipusEstat.PENDENT);
			portasignatures.setDocumentStoreId(documentDto.getId());
			portasignatures.setTransicioOK(transicioOK);
			portasignatures.setTransicioKO(transicioKO);
			pluginPortasignaturesDao.saveOrUpdate(portasignatures);
		} catch (Exception e) {
			throw new JbpmException("No s'ha pogut pujar el document al portasignatures", e);
		}
	}
	public Double processarDocumentSignatPortasignatures(Integer id) throws Exception {
		Double resposta = -1D;
		String transicio = "";
		
		try {
			Portasignatures portasignatures = pluginPortasignaturesDao.findByDocument(id);
			
			if (portasignatures != null) {
				DocumentStore documentStore = documentStoreDao.getById(portasignatures.getDocumentStoreId(), false);
				
				transicio = portasignatures.getTransicioOK();
				
				if ((portasignatures.getEstat() != TipusEstat.SIGNAT)
						&& (portasignatures.getTransition() != Transicio.SIGNAT)
						&& (!documentStore.isSignat())
						) {
					afegirDocumentCustodia(
							portasignatures.getDocumentId(),
							portasignatures.getDocumentStoreId());
				}
				
				portasignatures.setEstat(TipusEstat.SIGNAT);
				portasignatures.setTransition(Transicio.SIGNAT);
				pluginPortasignaturesDao.saveOrUpdate(portasignatures);
				
				Long token = portasignatures.getTokenId();
				jbpmDao.signalToken(token.longValue(), transicio);
				
				resposta = 1D;
			}
			
		} catch (Exception e) {
			logger.error("Error processant el document signat.", e);
			resposta = -1D;
		}
		
		return resposta;
	}
	public Double processarDocumentRebutjatPortasignatures(Integer id, String motiuRebuig) throws Exception {
		Double resposta = -1D;
		String transicio = "";
		
		try {
			Portasignatures portasignatures = pluginPortasignaturesDao.findByDocument(id);
			
			if (portasignatures != null) {
				transicio = portasignatures.getTransicioKO();
				
				portasignatures.setEstat(TipusEstat.REBUTJAT);
				portasignatures.setTransition(Transicio.REBUTJAT);
				portasignatures.setMotiuRebuig(motiuRebuig);
				pluginPortasignaturesDao.saveOrUpdate(portasignatures);
				
				Long token = portasignatures.getTokenId();
				jbpmDao.signalToken(token.longValue(), transicio);
				
				resposta = 1D;
			}
			
		} catch (Exception e) {
			logger.error("Error processant el document rebutjat.", e);
		}
		
		return resposta;
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
	public void setPermisDao(PermisDao permisDao) {
		this.permisDao = permisDao;
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
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}
	@Autowired
	public void setJbpmDao(JbpmDao jbpmDao) {
		this.jbpmDao = jbpmDao;
	}



	private void afegirDocumentCustodia(
			Integer documentId,
			Long documentStoreId) throws Exception {
		DocumentDto document = dtoConverter.toDocumentDto(documentStoreId, false, false);
		if (document != null) {
			DocumentStore docst = documentStoreDao.getById(documentStoreId, false);
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(docst.getProcessInstanceId());
			Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
			String varDocumentCodi = docst.getJbpmVariable().substring(TascaService.PREFIX_DOCUMENT.length());
			byte[] documentPortasignatures = obtenirDocumentPortasignatures(documentId);
			if ((documentPortasignatures != null) && !documentPortasignatures.equals("")) {
				String referenciaCustodia = null; 
				if (pluginCustodiaDao.isValidacioImplicita()) {
					referenciaCustodia = pluginCustodiaDao.afegirSignatura(
							documentStoreId.toString(),
							document.getArxiuNom(),
							document.getCustodiaCodi(),
							documentPortasignatures);
				} else {
					// TODO Comprovar validesa
					referenciaCustodia = pluginCustodiaDao.afegirSignatura(
							documentStoreId.toString(),
							document.getArxiuNom(),
							document.getCustodiaCodi(),
							documentPortasignatures);
				}
				docst.setReferenciaCustodia(referenciaCustodia);
				docst.setSignat(true);
				registreDao.crearRegistreSignarDocument(
						expedient.getId(),
						docst.getProcessInstanceId(),
						SecurityContextHolder.getContext().getAuthentication().getName(),
						varDocumentCodi);
			} else {
				throw new Exception("Error obtenint el document del Portasignautes.");
			}
		}
	}
	private byte[] obtenirDocumentPortasignatures(
			Integer documentId) {
		try {
			return pluginPortasignaturesDao.DownloadDocument(documentId);
		} catch (Exception e) {
			logger.error("Error obtenint el document del Portasignatures", e);
			return null;
		}
	}

	private boolean isSyncActiu() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.sync.actiu");
		return "true".equalsIgnoreCase(syncActiu);
	}

	private static final Log logger = LogFactory.getLog(PluginService.class);

}
