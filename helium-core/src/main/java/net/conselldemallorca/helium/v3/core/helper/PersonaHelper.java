/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import javax.annotation.Resource;

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



	public PersonaDto findByCodi(String codi) throws Exception {
		PersonaDto resposta;
		if (!pluginHelper.isPersonesPluginActiu() || pluginHelper.isPersonesSyncActiu()) {
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

}
