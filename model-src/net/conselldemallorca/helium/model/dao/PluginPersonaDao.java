/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.integracio.plugins.persones.Persona.Sexe;
import net.conselldemallorca.helium.model.exception.PersonaPluginException;
import net.conselldemallorca.helium.model.service.PluginService;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus persona
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class PluginPersonaDao extends PersonaDao implements ApplicationContextAware {

	private PersonesPlugin personesPlugin;
	private boolean pluginEvaluat = false;
	private Timer syncTimer;



	public PluginPersonaDao() {
		super();
	}

	public List<Persona> findLikeNomSencerPlugin(String text) {
		if (getPersonesPlugin() == null || isSyncActiu()) {
			List<net.conselldemallorca.helium.model.hibernate.Persona> persones = findLikeNomSencer(text);
			List<Persona> resposta = new ArrayList<Persona>();
			for (net.conselldemallorca.helium.model.hibernate.Persona persona: persones) {
				resposta.add(toPersonaPlugin(persona));
			}
			return resposta;
		} else {
			return personesPlugin.findLikeNomSencer(text);
		}
	}

	public Persona findAmbCodiPlugin(String codi) {
		if (getPersonesPlugin() == null || isSyncActiu()) {
			return toPersonaPlugin(findAmbCodi(codi));
		} else {
			return personesPlugin.findAmbCodi(codi);
		}
	}

	public List<Persona> findAllPlugin() {
		if (getPersonesPlugin() == null) {
			List<net.conselldemallorca.helium.model.hibernate.Persona> persones = findAll();
			List<Persona> resposta = new ArrayList<Persona>();
			for (net.conselldemallorca.helium.model.hibernate.Persona persona: persones) {
				resposta.add(toPersonaPlugin(persona));
			}
			return resposta;
		} else {
			return personesPlugin.findAll();
		}
	}



	public void setApplicationContext(ApplicationContext applicationContext) {
		String pluginClass = GlobalProperties.getInstance().getProperty("app.persones.plugin.class");
		if (pluginClass != null) {
			if (isSyncActiu()) {
				long periode = 24 * 3600 * 1000; 
				String syncPeriode = GlobalProperties.getInstance().getProperty("app.persones.plugin.sync.periode");
				if (syncPeriode != null) {
					try {
						periode = new Long(syncPeriode).longValue();
					} catch (Exception ignored) {};
				}
				syncTimer = new Timer();
				syncTimer.scheduleAtFixedRate(
						new SyncTimerTask(applicationContext),
					    0,
					    periode);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected PersonesPlugin getPersonesPlugin() {
		try {
			if (pluginEvaluat)
				return personesPlugin;
			String pluginClass = GlobalProperties.getInstance().getProperty("app.persones.plugin.class");
			if (pluginClass != null) {
				Class clazz = Class.forName(pluginClass);
				personesPlugin = (PersonesPlugin)clazz.newInstance();
			}
			pluginEvaluat = true;
			return personesPlugin;
		} catch (Exception ex) {
			throw new PersonaPluginException("No s'ha pogut crear la instància del plugin", ex);
		}
	}



	private Persona toPersonaPlugin(net.conselldemallorca.helium.model.hibernate.Persona persona) {
		if (persona == null)
			return null;
		Persona.Sexe sexe = null;
		if (persona.getSexe().equals(net.conselldemallorca.helium.model.hibernate.Persona.Sexe.SEXE_HOME))
			sexe = Sexe.SEXE_HOME;
		else
			sexe = Sexe.SEXE_DONA;	
		Persona p = new Persona(
				persona.getCodi(),
				persona.getNomSencer(),
				persona.getEmail(),
				sexe);
		p.setDni(persona.getDni());
		return p;
	}

	private boolean isSyncActiu() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.sync.actiu");
		return "true".equalsIgnoreCase(syncActiu);
	}



	class SyncTimerTask extends TimerTask {
		private ApplicationContext applicationContext;
		public SyncTimerTask(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}
		public void run() {
			((PluginService)applicationContext.getBean("pluginService", PluginService.class)).sync();
        }
	}

}
