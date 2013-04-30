/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;


/**
 * Servei per a gestionar entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntornService {

	/**
	 * Retorna els entorns als quals l'usuari actual hi te accés.
	 * 
	 * @return els entorns permesos.
	 */
	public List<EntornDto> findPermesosUsuariActual();

}
