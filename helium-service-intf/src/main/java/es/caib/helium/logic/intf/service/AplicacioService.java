/**
 * 
 */
package es.caib.helium.logic.intf.service;

import es.caib.helium.logic.intf.dto.DocumentConversioDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.UsuariPreferenciesDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.SistemaExternException;
import es.caib.helium.logic.intf.util.GlobalProperties;
import es.caib.helium.client.integracio.persones.model.Persona;

import java.util.List;

/**
 * Servei amb funcionalitat a nivell d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {

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
	public List<Persona> findPersonaLikeNomSencer(String text) throws SistemaExternException;

	public List<PersonaDto> findPersonesAmbGrup(String grup) throws SistemaExternException;

	public List<PersonaDto> findPersonaAll() throws SistemaExternException;


	// Properties
	public GlobalProperties getGlobalProperties();

	// Conversió
	public DocumentConversioDto convertFile(
			String arxiuNom,
			byte[] arxiuContingut,
			String extensioSortida) throws Exception;

	public String getArxiuMediaType(String nomFitxer);
}
