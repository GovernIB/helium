/**
 * 
 */
package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.helium.logic.intf.dto.PermisRolDto;


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
