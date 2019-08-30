/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Properties;

import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;

/**
 * Servei amb funcionalitat a nivell d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {

	/**
	 * Obté la versió actual de l'aplicació.
	 * 
	 * @return La versió actual.
	 */
	public String getVersioActual();

	/**
	 * Retorna les preferències de l'usuari actual.
	 * 
	 * @return Les preferències.
	 */
	public UsuariPreferenciesDto getUsuariPreferencies() throws NoTrobatException;

	/**
	 * Retorna informació d'una persona donat el seu codi.
	 * 
	 * @param codi el codi de la persona
	 * @return la informació de la persona
	 */
	public PersonaDto findPersonaAmbCodi(String codi) throws NoTrobatException, SistemaExternException;
	
	/**
	 * Retorna informació de la persona actual.
	 * 
	 * @param codi el codi de la persona
	 * @return la informació de la persona
	 */
	public PersonaDto findPersonaActual() throws NoTrobatException, SistemaExternException;

	/**
	 * Retorna una llista de persones que tenen una part del nom que
	 * coincideix amb el text especificat.
	 * 
	 * @param text el text per a fer la consulta
	 * @return la llista de persones
	 */
	public List<PersonaDto> findPersonaLikeNomSencer(String text) throws SistemaExternException;

	/**
	 * Consulta les propietats de l'aplicació que tenen un
	 * determinat prefix.
	 * 
	 * @param prefix el valor del prefix.
	 * @return la llista de propietats
	 */
	public Properties propertyFindByPrefix(String prefix);

	/**
	 * Consulta la propietat de l'aplicació amb un nom determinat.
	 * 
	 * @param nom el valor del nom.
	 * @return la llista de propietats
	 */
	public String propertyFindByNom(String nom);

	/**
	 * Consulta totes les propietats de l'aplicació.
	 * 
	 * @return la llista de propietats
	 */
	public Properties propertyFindAll();

}
