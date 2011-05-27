/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;


/**
 * Interfície per a la integració amb el registre.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public interface RegistrePlugin {

	/**
	 * Crea un registre d'entrada
	 * 
	 * @param dadesRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaAnotacioRegistre registrarEntrada(
			RegistreEntrada registreEntrada) throws RegistrePluginException;

	/**
	 * Consulta un registre d'entrada
	 * 
	 * @param numeroRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaConsulta consultarEntrada(
			String organCodi,
			String oficinaCodi,
			String numeroRegistre) throws RegistrePluginException;

	/**
	 * Crea un registre de sortida
	 * 
	 * @param dadesRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaAnotacioRegistre registrarSortida(
			RegistreSortida registreSortida) throws RegistrePluginException;

	/**
	 * Consulta un registre de sortida
	 * 
	 * @param numeroRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaConsulta consultarSortida(
			String organCodi,
			String oficinaCodi,
			String numeroRegistre) throws RegistrePluginException;

	/**
	 * Crea una notificació telemàtica
	 * 
	 * @param dadesRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaAnotacioRegistre registrarNotificacio(
			RegistreNotificacio registreNotificacio) throws RegistrePluginException;

	/**
	 * Obté l'acus de rebut per a una notificació telemàtica
	 * 
	 * @param numeroRegistre
	 * @return la data del justificant de recepció o null si encara no s'ha justificat
	 * @throws RegistrePluginException
	 */
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(
			String numeroRegistre) throws RegistrePluginException;

	/**
	 * Obté el nom de l'oficina a partir del codi de l'oficina
	 * 
	 * @param oficinaCodi
	 * @return el nom de l'oficina
	 * @throws RegistrePluginException
	 */
	public String obtenirNomOficina(String oficinaCodi) throws RegistrePluginException;

}
