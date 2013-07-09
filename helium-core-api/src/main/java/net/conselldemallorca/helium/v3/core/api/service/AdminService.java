/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AdminService {

	/**
	 * Retorna una llista d'entorns per als quals l'usuari actual
	 * te permis de lectura.
	 * 
	 * @return El llistat d'entorns.
	 */
	public List<EntornDto> findEntornAmbPermisReadUsuariActual();

	/**
	 * Retorna les preferències de l'usuari actual.
	 * 
	 * @return Les preferències.
	 */
	public UsuariPreferenciesDto getPreferenciesUsuariActual();

	/**
	 * Obté les mesures temporals per a evaluar el rendiment
	 * de l'aplicació al efectuar determinades tasques.
	 * 
	 * @return El llistat de mesures.
	 */
	//@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<MesuraTemporalDto> findMesuresTemporals();

	

}
