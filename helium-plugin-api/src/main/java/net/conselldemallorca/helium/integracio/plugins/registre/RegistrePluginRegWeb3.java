/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;


/**
 * Interfície per a la integració amb el registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistrePluginRegWeb3 {

	/**
	 * Crea un registre de sortida
	 * 
	 * @param dadesRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaAnotacioRegistre registrarSortida(
			RegistreAssentament registreSortida,
			String aplicacioNom,
			String aplicacioVersio) throws RegistrePluginException;

	public RespostaConsultaRegistre obtenirRegistrSortida(
			String numRegistre, 
			String usuariCodi,
			String entitatCodi) throws RegistrePluginException;

}
