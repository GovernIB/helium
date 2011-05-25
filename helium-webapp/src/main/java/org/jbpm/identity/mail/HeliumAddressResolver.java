package org.jbpm.identity.mail;

import net.conselldemallorca.helium.core.model.dao.DaoProxy;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;

import org.jbpm.mail.AddressResolver;

/**
 * Address resolver que empra l'organigrama d'Helium
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumAddressResolver implements AddressResolver {

	private static final long serialVersionUID = 1L;

	public Object resolveAddress(String actorId) {
		PersonaDto persona = DaoProxy.getInstance().getPluginPersonaDao().findAmbCodiPlugin(actorId);
		if (persona != null)
			return persona.getEmail();
		return null;
	}

}
