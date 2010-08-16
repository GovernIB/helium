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
			DadesRegistre dadesRegistre) throws RegistrePluginException;

	/**
	 * Consulta les dades d'un registre d'entrada
	 * 
	 * @param numero
	 * @param any
	 * @return
	 */
	public DadesRegistre consultarEntrada(
			String numero,
			String any) throws RegistrePluginException;

	/**
	 * Crea un registre de sortida
	 * 
	 * @param dadesRegistre
	 * @return
	 */
	public String[] registrarSortida(
			DadesRegistre dadesRegistre) throws RegistrePluginException;

	/**
	 * Consulta les dades d'un registre de sortida
	 * 
	 * @param numero
	 * @param any
	 * @return
	 */
	public DadesRegistre consultarSortida(
			String numero,
			String any) throws RegistrePluginException;

}
