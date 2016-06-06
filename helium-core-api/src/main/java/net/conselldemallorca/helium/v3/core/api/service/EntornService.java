/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;

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
	 * @return la llista d'entorns.
	 */
	public List<EntornDto> findActiusAmbPermisAcces() throws NoTrobatException;

	/**
	 * Retorna una llista amb tots els entorns.
	 * 
	 * @return la llista d'entorns.
	 */
	public List<EntornDto> findActiusAll();

}
