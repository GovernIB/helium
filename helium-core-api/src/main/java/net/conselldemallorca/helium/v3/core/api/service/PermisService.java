/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PermisRolDto;


/**
 * Servei per gestionar els permisos definits a nivell d'Helium
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PermisService {


	/** Consulta la llista de tots els permisos definits a nivell d'Helium
	 * 
	 * @return
	 */
	public List<PermisRolDto> findAll();

}
