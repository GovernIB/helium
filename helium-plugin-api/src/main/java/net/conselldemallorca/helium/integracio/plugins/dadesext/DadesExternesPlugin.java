package net.conselldemallorca.helium.integracio.plugins.dadesext;

import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;

/**
 * Plugin per a obtenir les dades externes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DadesExternesPlugin {
	
	
	/**
	 * Consulta la llista de paisos disponibles.
	 * 
	 * @return la llista de paisos.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public List<Pais> paisFindAll() throws SistemaExternException;

	/**
	 * Consulta la llista de comunitats autònomes disponibles.
	 * 
	 * @return la llista de comunitats.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public List<ComunitatAutonoma> comunitatFindAll() throws SistemaExternException;

	/**
	 * Consulta la llista de províncies disponibles.
	 * 
	 * @return la llista de províncies.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public List<Provincia> provinciaFindAll() throws SistemaExternException;

	/**
	 * Consulta la llista de províncies d'una comunitat autònoma.
	 * 
	 * @param comunitatCodi
	 *            El codi de la comunitat autònoma.
	 * @return la llista de províncies.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public List<Provincia> provinciaFindByComunitat(
			String comunitatCodi) throws SistemaExternException;

	/**
	 * Consulta la llista de municipis d'una província.
	 * 
	 * @param provinciaCodi
	 *            El codi de la província.
	 * @return la llista de municipis.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public List<Municipi> municipiFindByProvincia(
			String provinciaCodi) throws SistemaExternException;

	/**
	 * Retorna el llistat de entitats geogràfiques
	 * 
	 * @return Llista de entitats geogràfiques
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les entitats geogràfiques.
	 */
	public List<EntitatGeografica> entitatGeograficaFindAll() throws SistemaExternException;
	
	/**
	 * Retorna el llistat de nivells d'administració
	 * 
	 * @return Llista de nivells d'administració
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els nivells d'administració.
	 */
	public List<NivellAdministracio> nivellAdministracioFindAll() throws SistemaExternException;

	/**
	 * Retorna el llistat de tipus de via
	 * 
	 * @return Llista de tipus de via
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els tipus de via.
	 */
	public List<TipusVia> tipusViaFindAll() throws SistemaExternException;

}
