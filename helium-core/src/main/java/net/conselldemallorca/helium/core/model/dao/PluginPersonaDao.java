/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.exception.PersonaPluginException;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.persones.DadesPersona;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPluginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class PluginPersonaDao extends PersonaDao {

	private PersonesPlugin personesPlugin;
	private boolean pluginEvaluat = false;



	public PluginPersonaDao() {
		super();
	}

	public List<PersonaDto> findLikeNomSencerPlugin(String text) {
		try {
			if (getPersonesPlugin() == null || isSyncActiu()) {
				List<Persona> persones = findLikeNomSencer(text);
				List<PersonaDto> resposta = new ArrayList<PersonaDto>();
				for (Persona persona: persones) {
					resposta.add(toPersonaPlugin(persona));
				}
				return resposta;
			} else {
				List<DadesPersona> persones = personesPlugin.findLikeNomSencer(text);
				List<PersonaDto> resposta = new ArrayList<PersonaDto>();
				for (DadesPersona persona: persones)
					resposta.add(toPersonaPlugin(persona));
				return resposta;
			}
		} catch (PersonesPluginException ex) {
			logger.error("Error al cercar les persones amb el nom sencer", ex);
			throw new PluginException("Error al cercar les persones amb el nom sencer", ex);
		}
	}

	public PersonaDto findAmbCodiPlugin(String codi) {
		try {
			if (getPersonesPlugin() == null || isSyncActiu()) {
				return toPersonaPlugin(findAmbCodi(codi));
			} else {
				return toPersonaPlugin(personesPlugin.findAmbCodi(codi));
			}
		} catch (PersonesPluginException ex) {
			logger.error("Error al cercar les persones amb el codi", ex);
			throw new PluginException("Error al cercar les persones amb el codi", ex);
		}
	}

	public List<PersonaDto> findAllPlugin() {
		try {
			if (getPersonesPlugin() == null) {
				List<Persona> persones = findAll();
				List<PersonaDto> resposta = new ArrayList<PersonaDto>();
				for (Persona persona: persones)
					resposta.add(toPersonaPlugin(persona));
				return resposta;
			} else {
				List<DadesPersona> persones = personesPlugin.findAll();
				List<PersonaDto> resposta = new ArrayList<PersonaDto>();
				for (DadesPersona persona: persones)
					resposta.add(toPersonaPlugin(persona));
				return resposta;
			}
		} catch (PersonesPluginException ex) {
			logger.error("Error al cercar totes les persones", ex);
			throw new PluginException("Error al cercar totes les persones", ex);
		}
	}



	@SuppressWarnings("rawtypes")
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



	private PersonaDto toPersonaPlugin(Persona persona) {
		if (persona == null)
			return null;
		PersonaDto.Sexe sexe = null;
		if (persona.getSexe().equals(Persona.Sexe.SEXE_HOME))
			sexe = PersonaDto.Sexe.SEXE_HOME;
		else
			sexe = PersonaDto.Sexe.SEXE_DONA;	
		PersonaDto p = new PersonaDto(
				persona.getCodi(),
				persona.getNomSencer(),
				persona.getEmail(),
				sexe);
		p.setDni(persona.getDni());
		return p;
	}
	private PersonaDto toPersonaPlugin(DadesPersona persona) {
		if (persona == null)
			return null;
		PersonaDto.Sexe sexe = null;
		if (persona.getSexe().equals(DadesPersona.Sexe.SEXE_HOME))
			sexe = PersonaDto.Sexe.SEXE_HOME;
		else
			sexe = PersonaDto.Sexe.SEXE_DONA;	
		PersonaDto p = new PersonaDto(
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

	private static final Log logger = LogFactory.getLog(PluginPersonaDao.class);

}
