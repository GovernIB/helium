/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.exception.PersonaPluginException;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.persones.DadesPersona;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPluginException;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.repository.PersonaRepository;

import org.springframework.stereotype.Component;

/**
 * Helper per a accedir a la funcionalitat dels plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PersonaHelper {
	
	private boolean pluginEvaluat = false;
	
	private PersonesPlugin personesPlugin;

	@Resource
	private PersonaRepository personaRepository;

	@Resource
	private PluginHelper pluginHelper;
	
	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	public List<PersonaDto> findPersonaLikeNomSencer(String text) {
		try {
			if (getPersonesPlugin() == null || isSyncActiu()) {
				List<Persona> persones = personaRepository.findLikeNomSencer(text);
				List<PersonaDto> resposta = new ArrayList<PersonaDto>();
				if (persones != null) {
					for (Persona persona: persones) {
						resposta.add(toPersonaPlugin(persona));
					}
				}
				return resposta;
			} else {
				List<DadesPersona> persones = personesPlugin.findLikeNomSencer(text);
				return conversioTipusHelper.convertirList(persones, PersonaDto.class);
			}
		} catch (PersonesPluginException ex) {
			throw new PluginException("Error al cercar les persones amb el nom sencer", ex);
		}
	}
	public PersonaDto findByCodi(String codi) throws Exception {
		PersonaDto resposta;
		if (pluginHelper == null || !pluginHelper.isPersonesPluginActiu() || pluginHelper.isPersonesSyncActiu()) {
			// Si la sincronització està activa les persones es guarden a dins la taula
			// de perones de Helium
			resposta = conversioTipusHelper.convertir(
					personaRepository.findByCodi(codi),
					PersonaDto.class);
		} else {
			resposta = pluginHelper.findPersonaAmbCodi(codi);
		}
		return resposta;
	}

	public PersonaDto toPersonaPlugin(Persona persona) {
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

	public PersonaDto findAmbCodiPlugin(String codi) {
		String codiPerConsulta = (isIgnoreCase()) ? codi.toLowerCase() : codi;
		try {
			return findByCodi(codiPerConsulta);
		} catch (Exception ex) {
			throw new PluginException("Error al cercar les persones amb el codi" + codi, ex);
		}
	}

	public boolean isSyncActiu() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.sync.actiu");
		return "true".equalsIgnoreCase(syncActiu);
	}
	private boolean isIgnoreCase() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.ignore.case");
		return "true".equalsIgnoreCase(syncActiu);
	}
}
