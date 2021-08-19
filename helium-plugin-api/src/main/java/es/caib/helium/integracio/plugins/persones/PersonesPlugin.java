/**
 * 
 */
package es.caib.helium.integracio.plugins.persones;

import java.util.List;

import es.caib.helium.client.integracio.persones.model.Persona;

/**
 * Interfície per a la connexió amb un sistema d'usuaris/persones
 * extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PersonesPlugin {

	/**
	 * Retorna el conjunt de persones amb el nom que comença per un
	 * text determinat.
	 * 
	 * @param text començament del nom
	 * @return la llista de persones
	 */
	//public List<DadesPersona> findLikeNomSencer(String text, Long entornId) throws PersonesPluginException;
	public List<Persona> findLikeNomSencer(String text, Long entornId) throws PersonesPluginException;

	/**
	 * Retorna la persona amb el codi especificat
	 * 
	 * @param codi el codi de la persona
	 * @return la persona
	 */
	public Persona findAmbCodi(String codi, Long entornId) throws PersonesPluginException;

	/**
	 * Retorna totes les persones
	 * 
	 * @return totes les persones
	 */
	public List<Persona> findAll(Long entornId) throws PersonesPluginException;

	/**
	 * Retorna els rols d'una persona amb el codi especificat
	 * 
	 * @param codi el codi de la persona
	 * @return els rols de la persona
	 */
	public List<String> findRolsAmbCodi(String codi, Long entonId) throws PersonesPluginException;
}
