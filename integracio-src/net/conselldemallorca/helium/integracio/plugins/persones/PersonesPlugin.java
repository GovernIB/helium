/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.persones;

import java.util.List;

/**
 * Interfície per a la connexió amb un sistema d'usuaris/persones
 * extern.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public interface PersonesPlugin {

	/**
	 * Retorna el conjunt de persones amb el nom que comença per un
	 * text determinat.
	 * 
	 * @param text començament del nom
	 * @return la llista de persones
	 */
	public List<Persona> findLikeNomSencer(String text);

	/**
	 * Retorna la persona amb el codi especificat
	 * 
	 * @param codi el codi de la persona
	 * @return la persona
	 */
	public Persona findAmbCodi(String codi);

	/**
	 * Retorna totes les persones
	 * 
	 * @return totes les persones
	 */
	public List<Persona> findAll();

}
