package org.jbpm.identity.mail;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;

import org.jbpm.mail.AddressResolver;

/**
 * Address resolver que empra l'organigrama d'Helium
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumAddressResolver implements AddressResolver {

	private static final long serialVersionUID = 1L;

	public Object resolveAddress(String actorId) {
		PersonaDto persona = Jbpm3HeliumBridge.getInstance().getPersonaAmbCodi(actorId);
		if (persona != null)
			return persona.getEmail();
		else
			return null;
	}

}
