/**
 * 
 */
package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.helium.commons.dto.ExcepcioLogDto;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.commons.dto.UsuariPreferenciesDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.SistemaExternException;

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
	public List<PersonaDto> findPersonaLikeCodiOrNomSencer(String text) throws SistemaExternException;

	/**
	 * Retorna informació d'una persona o càrrec donat el seu codi.
	 * 
	 * @param codi el codi de la persona o del càrrec
	 * @return la informació de la persona o càrrec
	 */
	public PersonaDto findPersonaCarrecAmbCodi(String codi) throws NoTrobatException, SistemaExternException;
	
	public void excepcioSave(String peticio, String params, Throwable exception);
	public ExcepcioLogDto excepcioFindOne(Long index);
	public List<ExcepcioLogDto> excepcioFindAll();
	
	/**
	 * Actualitza la preferencia del entorn actual per l'usuari
	 * @param entorn
	 * @throws NoTrobatException
	 */
	public void updateEntornActual(String entorn) throws NoTrobatException;

	/** Consulta totes les persones amb el plugin.
	 * 
	 * @return
	 */
	public List<PersonaDto> findPersonesAll();

	/** Fa la crida per esborrar la informació del l'expedient el el thread local info.
	 * 
	 */
	public void clearExpedient();
}
