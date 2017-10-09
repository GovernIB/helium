/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.util.List;

/**
 * Interfície per a la integració amb el registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistrePluginRegWeb3 {

	/**
	 * Crea un registre de sortida
	 * 
	 */
	public RespostaAnotacioRegistre registrarSortida(
			RegistreAssentament registreSortida,
			String aplicacioNom,
			String aplicacioVersio) throws RegistrePluginException;

	public RespostaConsultaRegistre obtenirRegistreSortida(
			String numRegistre, 
			String usuariCodi,
			String entitatCodi) throws RegistrePluginException;
	
	/**
	 * Crea un registre d'entrada
	 * 
	 */
	public RespostaAnotacioRegistre registrarEntrada(
			RegistreAssentament registreEntrada,
			String aplicacioNom,
			String aplicacioVersio) throws RegistrePluginException;
	
	public RespostaConsultaRegistre obtenirRegistreEntrada(
			String numRegistre, 
			String usuariCodi,
			String entitatCodi) throws RegistrePluginException;

	public void anularRegistreEntrada(
			String registreNumero,
			String usuari,
			String entitat,
			boolean anular) throws RegistrePluginException;

	public void anularRegistreSortida(
			String registreNumero,
			String usuari,
			String entitat,
			boolean anular) throws RegistrePluginException;

	public List<TipusAssumpte> llistarTipusAssumpte(
			String entitat) throws RegistrePluginException;

	public List<CodiAssumpte> llistarCodisAssumpte(
			String entitat,
			String tipusAssumpte) throws RegistrePluginException;
	
	public List<Oficina> llistarOficines(
			String entitat,
			Long autoritzacio) throws RegistrePluginException;
	
	public List<Llibre> llistarLlibres(
			String entitat,
			String oficina,
			Long autoritzacio) throws RegistrePluginException;
	
	public List<Organisme> llistarOrganismes(
			String entitat) throws RegistrePluginException;

}
