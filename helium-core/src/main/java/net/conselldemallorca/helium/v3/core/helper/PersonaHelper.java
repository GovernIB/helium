package net.conselldemallorca.helium.v3.core.helper;

import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.util.GlobalProperties;
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

	@Resource
	private PersonaRepository personaRepository;
	@Resource
	private PluginHelper pluginHelper;	
	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	public List<PersonaDto> findLikeNomSencer(String text) {
		if (pluginHelper == null || !pluginHelper.isPersonesPluginActiu() || pluginHelper.isPersonesSyncActiu()) {
			List<Persona> persones = personaRepository.findLikeNomSencer(text);
			return conversioTipusHelper.convertirList(persones, PersonaDto.class);
		} else {
			return pluginHelper.findLikeNomSencer(text);
		}
	}
	
	public PersonaDto findAmbCodi(String codi) {
		String codiPerConsulta = (isIgnoreCase()) ? codi.toLowerCase() : codi;
		try {
			PersonaDto resposta;
			if (pluginHelper == null || !pluginHelper.isPersonesPluginActiu() || pluginHelper.isPersonesSyncActiu()) {
				// Si la sincronització està activa les persones es guarden a dins la taula
				// de perones de Helium
				resposta = conversioTipusHelper.convertir(
						personaRepository.findByCodi(codiPerConsulta),
						PersonaDto.class);
			} else {
				resposta = pluginHelper.findPersonaAmbCodi(codiPerConsulta);
			}
			return resposta;
		} catch (Exception ex) {
			throw new PluginException("Error al cercar les persones amb el codi" + codi, ex);
		}
	}
	private boolean isIgnoreCase() {
		String syncActiu = GlobalProperties.getInstance().getProperty("app.persones.plugin.ignore.case");
		return "true".equalsIgnoreCase(syncActiu);
	}
}
