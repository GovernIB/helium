/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.v3.core.helper.PluginPersonaHelper;

import org.springframework.stereotype.Service;

/**
 * Servei per gestionar les consultes als plugins
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("pluginServiceV3")
public class PluginServiceImpl implements PluginService{

	@Resource
	private PluginPersonaHelper pluginPersonaHelper;
	
	@Override
	public List<PersonaDto> findPersonaLikeNomSencer(String text) {
		return pluginPersonaHelper.findLikeNomSencerPlugin(text);
	}
	
	@Override
	public PersonaDto findPersonaAmbCodi(String codi) {
		return pluginPersonaHelper.findAmbCodiPlugin(codi);
	}
}
