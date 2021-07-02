/**
 * 
 */
package es.caib.helium.logic.intf.service;

import java.util.List;

import org.springframework.security.acls.model.NotFoundException;

import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.exception.ValidacioException;


/**
 * Servei encarregat de gestionar les tasques dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTascaService {

	/**
	 * Retorna el llistat de tasques d'una instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @return el llistat de tasques.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientTascaDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna la llista de tasques pendents de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @return La llista de tasques pendents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientTascaDto> findPendents(
			Long expedientId,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) throws NoTrobatException, PermisDenegatException;

	/**
	 * Cancel·la una tasca de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param expedientId
	 *            Atribut id de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void cancelar(
			Long expedientId,
			String tascaId) throws NoTrobatException, ValidacioException;

	/**
	 * Suspen la tramitació una tasca de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param expedientId
	 *            Atribut id de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void suspendre(
			Long expedientId,
			String tascaId) throws NoTrobatException, ValidacioException;

	/**
	 * Repren la tramitació d'una tasca suspesa de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param expedientId
	 *            Atribut id de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void reprendre(
			Long expedientId,
			String tascaId) throws NoTrobatException, ValidacioException;

	/**
	 * Canvia el responsable d'una tasca de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param expedientId
	 *            Atribut id de la tasca.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void reassignar(
			Long expedientId,
			String tascaId,
			String expressio) throws NoTrobatException, ValidacioException;
}
