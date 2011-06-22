/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.dao.PluginPortasignaturesDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dao.UsuariDao;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.exception.IllegalStateException;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Usuari;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
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
	private UsuariDao usuariDao;
	private ExpedientDao expedientDao;
	private RegistreDao registreDao;
	private DocumentStoreDao documentStoreDao;
	private DtoConverter dtoConverter;
	private JbpmDao jbpmDao;
	private MessageSource messageSource;



	public List<PersonaDto> findPersonaLikeNomSencer(String text) {
		return pluginPersonaDao.findLikeNomSencerPlugin(text);
	}
	public PersonaDto findPersonaAmbCodi(String codi) {
		return pluginPersonaDao.findAmbCodiPlugin(codi);
	}
	public void personesSync() {
		if (isSyncPersonesActiu()) {
			logger.info("Inici de la sincronització de persones");
			List<PersonaDto> persones = pluginPersonaDao.findAllPlugin();
			int nsyn = 0;
			int ncre = 0;
	    	for (PersonaDto persona: persones) {
	    		net.conselldemallorca.helium.core.model.hibernate.Persona p = pluginPersonaDao.findAmbCodi(persona.getCodi());
	    		if (p != null) {
	    			p.setNom(persona.getNom());
	    			p.setLlinatges(persona.getLlinatges());
	    			p.setDni(persona.getDni());
	    			p.setEmail(persona.getEmail());
	    		} else {
	    			//logger.info("Nova persona: " + persona.getCodi());
	    			p = new net.conselldemallorca.helium.core.model.hibernate.Persona();
	    			p.setCodi(persona.getCodi());
	    			p.setNom(persona.getNom());
	    			p.setLlinatge1((persona.getLlinatge1() != null) ? persona.getLlinatge1(): " ");
	    			p.setLlinatge2(persona.getLlinatge2());
	    			p.setDni(persona.getDni());
	    			p.setEmail(persona.getEmail());
	    			pluginPersonaDao.saveOrUpdate(p);
	    			pluginPersonaDao.flush();
	    			Usuari usuari = usuariDao.getById(persona.getCodi(), false);
	    			if (usuari == null) {
	    				usuari = new Usuari();
	    				usuari.setCodi(persona.getCodi());
	    				usuari.setContrasenya(persona.getCodi());
	    				//usuari.addPermis(permisDao.getByCodi("HEL_USER"));
	    				usuariDao.saveOrUpdate(usuari);
	    				usuariDao.flush();
	    			}
	    			ncre++;
	    		}
	    		nsyn++;
	    	}
	    	logger.info("Fi de la sincronització de persones (total: " + nsyn + ", creades: " + ncre + ")");
		}
	}

	public void enviarPortasignatures(
			PersonaDto persona,
			Long documentStoreId,
			Expedient expedient,
			String importancia,
			Date dataLimit,
			Long tokenId,
			String transicioOK,
			String transicioKO) throws Exception {
		try {
			DocumentDto document = dtoConverter.toDocumentDto(documentStoreId, true, true, true, true);
			String documentTitol = expedient.getIdentificador() + ": " + document.getDocumentNom();
			Integer doc = pluginPortasignaturesDao.uploadDocument(
							persona,
							documentTitol,
							document.getVistaNom(),
							document.getVistaContingut(),
							document.getTipusDocPortasignatures(),
							expedient,
							importancia,
							dataLimit);
			Calendar cal = Calendar.getInstance();
			Portasignatures portasignatures = new Portasignatures();
			portasignatures.setDocumentId(doc);
			portasignatures.setTokenId(tokenId);
			portasignatures.setDataEnviat(cal.getTime());
			portasignatures.setEstat(TipusEstat.PENDENT);
			portasignatures.setDocumentStoreId(documentStoreId);
			portasignatures.setTransicioOK(transicioOK);
			portasignatures.setTransicioKO(transicioKO);
			pluginPortasignaturesDao.saveOrUpdate(portasignatures);
		} catch (Exception e) {
			throw new JbpmException(getMessage("error.pluginService.pujarDocument"), e);
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
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private void afegirDocumentCustodia(
			Integer documentId,
			Long documentStoreId) throws Exception {
		DocumentDto document = dtoConverter.toDocumentDto(
				documentStoreId,
				true,
				true,
				true,
				true);
		if (document != null) {
			DocumentStore docst = documentStoreDao.getById(documentStoreId, false);
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(docst.getProcessInstanceId());
			Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
			String varDocumentCodi = docst.getJbpmVariable().substring(TascaService.PREFIX_DOCUMENT.length());
			List<byte[]> signatures = obtenirSignaturesDelPortasignatures(documentId);
			if (signatures != null) {
				String referenciaCustodia = null;
				for (byte[] signatura: signatures) {
					referenciaCustodia = pluginCustodiaDao.afegirSignatura(
							documentStoreId,
							docst.getReferenciaFont(),
							document.getArxiuNom(),
							document.getCustodiaCodi(),
							signatura);
				}
				docst.setReferenciaCustodia(referenciaCustodia);
				docst.setSignat(true);
				registreDao.crearRegistreSignarDocument(
						expedient.getId(),
						docst.getProcessInstanceId(),
						SecurityContextHolder.getContext().getAuthentication().getName(),
						varDocumentCodi);
			} else {
				throw new Exception(getMessage("error.pluginService.capSignatura"));
			}
		}
		throw new IllegalStateException(getMessage("error.pluginService.noDisponible"));
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

	/*@SuppressWarnings("unchecked")
	private List<Persona> getPersonesSync() {
		List<Persona> persones = (List<Persona>)deserializeFromFile();
		if (persones != null) {
			return persones;
		} else {
			persones = pluginPersonaDao.findAllPlugin();
			serializeToFile(persones);
			logger.info("Persones serialitzades");
			return persones;
		}
	}
	private void serializeToFile(Object obj) {
		try {
			FileOutputStream fout = new FileOutputStream("c:/tmp/helium/persones.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(obj);
			oos.close();
			fout.close();
		} catch (Exception ex) {
			logger.error(ex);
		}
	}
	private Object deserializeFromFile() {
		try {
			FileInputStream fin = new FileInputStream("c:/tmp/helium/persones.dat");
			ObjectInputStream ois = new ObjectInputStream(fin);
			Object obj = ois.readObject();
			ois.close();
			fin.close();
			return obj;
		} catch (Exception ex) {
			logger.error(ex);
		}
		return null;
	}*/
	
	
	protected String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}

	protected String getMessage(String key) {
		return getMessage(key, null);
	}

	private static final Log logger = LogFactory.getLog(PluginService.class);

}
