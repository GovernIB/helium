package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PermisHeliumDto;;

/**
 * Servei per a gestionar permisos Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PermisService {

	/**
	 * Retorna una llista amb tots els permisos Helium.
	 * 
	 * @return la llista amb els permisos Helium.
	 */
	List<PermisHeliumDto> findAll();

}