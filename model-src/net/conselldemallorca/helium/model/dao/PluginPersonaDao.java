/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.integracio.plugins.persones.Persona.Sexe;
import net.conselldemallorca.helium.model.exception.PersonaPluginException;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus persona
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class PluginPersonaDao extends PersonaDao {

	private PersonesPlugin personesPlugin;
	private boolean pluginEvaluat = false;



	public PluginPersonaDao() {
		super();
	}

	public List<Persona> findLikeNomSencerPlugin(String text) {
		
		if (getPersonesPlugin() == null) {
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
		if (getPersonesPlugin() == null) {
			return toPersonaPlugin(findAmbCodi(codi));
		} else {
			return personesPlugin.findAmbCodi(codi);
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
		return p;
	}

}
