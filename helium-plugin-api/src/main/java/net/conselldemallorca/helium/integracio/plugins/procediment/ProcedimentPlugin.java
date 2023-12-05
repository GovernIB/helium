/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.procediment;

import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;


/**
 * Plugin per a consultar la llista de procediments d'una font externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentPlugin {

	/**
	 * Retorna els procediment associats a una unitat organitzativa.
	 * 
	 * @param codiDir3
	 *            Codi DIR3 de l'unitat organitzativa.
	 * @return la llista de procediments.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els procediments.
	 */
	public List<Procediment> findAmbCodiDir3(String codiDir3) throws SistemaExternException;

	/** Mètode per obtenir la informació d'unia unitat administrativa a partir del seu codi intern
	 * que es retorna en la consulta del procediment.
	 * 
	 * @param codi
	 * 
	 * @return Objecte amb la informació o null si no s'ha trobat.
	 */
	public UnitatAdministrativa findUnitatAdministrativaAmbCodi(String codi) throws SistemaExternException;
}
