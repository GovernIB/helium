/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.dao.PermisDao;
import net.conselldemallorca.helium.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.model.dao.UsuariDao;
import net.conselldemallorca.helium.model.hibernate.Usuari;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les consultes als plugins
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class PluginService {

	private PluginPersonaDao pluginPersonaDao;
	private UsuariDao usuariDao;
	private PermisDao permisDao;



	public List<Persona> findPersonaLikeNomSencer(String text) {
		return pluginPersonaDao.findLikeNomSencerPlugin(text);
	}
	public Persona findPersonaAmbCodi(String codi) {
		return pluginPersonaDao.findAmbCodiPlugin(codi);
	}
	public void sync() {
		if (isSyncActiu()) {
			logger.info("Inici de la sincronització de persones");
			List<Persona> persones = pluginPersonaDao.findAllPlugin();
	    	for (Persona persona: persones) {
	    		net.conselldemallorca.helium.model.hibernate.Persona p = pluginPersonaDao.findAmbCodi(persona.getCodi());
	    		if (p != null) {
	    			p.setNom(persona.getNom());
	    			p.setLlinatges(persona.getLlinatges());
	    			p.setDni(persona.getDni());
	    			p.setEmail(persona.getEmail());
	    		} else {
	    			logger.info("Nova persona: " + persona.getCodi());
	    			p = new net.conselldemallorca.helium.model.hibernate.Persona();
	    			p.setCodi(persona.getCodi());
	    			p.setNom(persona.getNom());
	    			p.setLlinatge1((persona.getLlinatge1() != null) ? persona.getLlinatge1(): "");
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
	    	logger.info("Fi de la sincronització de persones");
		}
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



	private boolean isSyncActiu() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.sync.actiu");
		return "true".equalsIgnoreCase(syncActiu);
	}

	private static final Log logger = LogFactory.getLog(PluginService.class);

}
