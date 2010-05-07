package org.jbpm.identity.mail;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.dao.DaoProxy;

import org.jbpm.mail.AddressResolver;

/**
 * Address resolver que empra l'organigrama d'Helium
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class HeliumAddressResolver implements AddressResolver {

	private static final long serialVersionUID = 1L;

	public Object resolveAddress(String actorId) {
		Persona persona = DaoProxy.getInstance().getPluginPersonaDao().findAmbCodiPlugin(actorId);
		if (persona != null)
			return persona.getEmail();
		return null;
	}

}
