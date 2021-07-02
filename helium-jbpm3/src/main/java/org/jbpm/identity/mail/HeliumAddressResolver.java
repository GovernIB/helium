package org.jbpm.identity.mail;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.jbpm.mail.AddressResolver;

import es.caib.helium.logic.intf.dto.PersonaDto;

/**
 * Address resolver que empra l'organigrama d'Helium
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumAddressResolver implements AddressResolver {

	private static final long serialVersionUID = 1L;

	public Object resolveAddress(String actorId) {
		PersonaDto persona = Jbpm3HeliumBridge.getInstanceService().getPersonaAmbCodi(actorId);
		if (persona != null)
			return persona.getEmail();
		return null;
	}

}
