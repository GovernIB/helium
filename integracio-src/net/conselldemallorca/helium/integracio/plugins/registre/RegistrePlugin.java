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
	 */
	public String[] registrarEntrada(
			SeientRegistral dadesRegistre) throws RegistrePluginException;

	/**
	 * Consulta les dades d'un registre d'entrada
	 * 
	 * @param numero
	 * @param any
	 * @return
	 */
	public SeientRegistral consultarEntrada(
			String oficina,
			String numero,
			String any) throws RegistrePluginException;

	/**
	 * Crea un registre de sortida
	 * 
	 * @param dadesRegistre
	 * @return
	 */
	public String[] registrarSortida(
			SeientRegistral dadesRegistre) throws RegistrePluginException;

	/**
	 * Consulta les dades d'un registre de sortida
	 * 
	 * @param numero
	 * @param any
	 * @return
	 */
	public SeientRegistral consultarSortida(
			String oficina,
			String numero,
			String any) throws RegistrePluginException;

	/**
	 * Retorna el nom d'una oficina donat el seu codi
	 * 
	 * @param codi
	 * @return
	 * @throws RegistrePluginException
	 */
	public String getNomOficina(
			String codi) throws RegistrePluginException;

}
