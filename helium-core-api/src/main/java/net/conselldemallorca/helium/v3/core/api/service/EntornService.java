/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;

/**
 * Servei per a gestionar els entorns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntornService {

	/**
	 * Retorna una llista d'entorns per als quals l'usuari actual
	 * te permis d'accés (READ).
	 * 
	 * @return El llistat d'entorns.
	 */
	public List<EntornDto> findAmbPermisAcces();

	/**
	 * Retorna una llista de tots els entorns del sistema
	 * 
	 * @return El llistat d'entorns.
	 */
	public List<EntornDto> findAll();

}
