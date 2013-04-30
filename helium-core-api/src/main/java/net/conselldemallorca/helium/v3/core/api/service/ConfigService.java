/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.exception.PluginException;


/**
 * Servei per a accedir a la funcionalitat dels plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigService {

	public List<EntornDto> findEntornsPermesosUsuariActual();

	public void setEntornActual(EntornDto entorn);

	public EntornDto getEntornActual();

	public String getUsuariCodiActual();

	public PersonaDto getPersonaAmbCodi(String codi) throws PluginException;

	public List<FestiuDto> findFestiusAll();

	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(
			String usuari);

	public String getHeliumProperty(String propertyName);

	public UsuariPreferenciesDto getPreferenciesUsuariActual();

}
