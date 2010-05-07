/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.dao.PluginPersonaDao;

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



	public List<Persona> findPersonaLikeNomSencer(String text) {
		return pluginPersonaDao.findLikeNomSencerPlugin(text);
	}
	public Persona findPersonaAmbCodi(String codi) {
		return pluginPersonaDao.findAmbCodiPlugin(codi);
	}



	@Autowired
	public void setPluginPersonaDao(PluginPersonaDao pluginPersonaDao) {
		this.pluginPersonaDao = pluginPersonaDao;
	}

}
