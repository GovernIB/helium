/**
 * 
 */
package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.helium.logic.intf.dto.TokenDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;


/**
 * Servei per a gestionar els tokens d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTokenService {

	/**
	 * Retorna la llista de tokens d'una instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @return la llista de tokens de la instància de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<TokenDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Canvia l'estat del token a actiu o a inactiu.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param tokenId
	 *            Atribut id del token.
	 * @param activar
	 * @return
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public boolean canviarEstatActiu(
			Long expedientId,
			String processInstanceId,
			Long tokenId,
			boolean activar) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els nodes que tenen una transició amb desti igual
	 * al node a on està situat el token.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param tokenId
	 *            Atribut id del token.
	 * @return la llista de noms de node.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<String> findArrivingNodeNames(
			Long expedientId,
			String processInstanceId,
			String tokenId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna la informació d'un token donat el seu id.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param tokenId
	 *            Atribut id del token.
	 * @return la informació del token.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public TokenDto findById(
			Long expedientId,
			String processInstanceId,
			String tokenId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retrocedeix un token fins al node especificat.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param tokenId
	 *            Atribut id del token.
	 * @param nodeName
	 *            Node destí del retrocés.
	 * @param cancelarTasques
	 *            Indica si s'han de cancel·lar les tasques actives del token.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void retrocedir(
			Long expedientId,
			String processInstanceId,
			String tokenId,
			String nodeName,
			boolean cancelarTasques) throws NoTrobatException, PermisDenegatException;

}
