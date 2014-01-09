/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dao.UsuariDao;
import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.model.hibernate.Usuari;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.helper.PluginPersonaHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les consultes als plugins
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("pluginServiceV3")
public class PluginServiceImpl {

	@Resource
	private PluginPersonaHelper pluginPersonaHelper;
	private UsuariDao usuariDao;

	public List<PersonaDto> findPersonaLikeNomSencer(String text) {
		return pluginPersonaHelper.findLikeNomSencerPlugin(text);
	}
	public PersonaDto findPersonaAmbCodi(String codi) {
		return pluginPersonaHelper.findAmbCodiPlugin(codi);
	}
	public void personesSync() {
		if (isSyncPersonesActiu()) {
			List<PersonaDto> persones = pluginPersonaHelper.findAllPlugin();
			int nsyn = 0;
			int ncre = 0;
			logger.info("Inici de la sincronització de persones (" + persones.size() + " registres)");
	    	for (PersonaDto persona: persones) {
	    		try {
		    		Persona p = pluginPersonaHelper.findByCodi(persona.getCodi());
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
		    			pluginPersonaHelper.save(p);
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

	@Autowired
	public void setPluginPersonaDao(PluginPersonaHelper pluginPersonaHelper) {
		this.pluginPersonaHelper = pluginPersonaHelper;
	}
	@Autowired
	public void setUsuariDao(UsuariDao usuariDao) {
		this.usuariDao = usuariDao;
	}

	private boolean isSyncPersonesActiu() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.sync.actiu");
		return "true".equalsIgnoreCase(syncActiu);
	}
	private boolean isIgnoreCase() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.ignore.case");
		return "true".equalsIgnoreCase(syncActiu);
	}

	private static final Log logger = LogFactory.getLog(PluginServiceImpl.class);

}
