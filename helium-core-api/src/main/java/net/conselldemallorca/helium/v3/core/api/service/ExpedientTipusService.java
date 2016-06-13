package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;;

/**
 * Servei per al manteniment de tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTipusService {

	/**
	 * Crea un nou tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipus
	 *            La informació del tipus d'expedient a crear.
	 * @param sequenciesAny
	 *            Els anys de les seqüències.
	 * @param sequenciesValor
	 *            Els valors de les seqüències.
	 * @return el tipus d'expedient creat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto create(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) throws NoTrobatException, PermisDenegatException;

	/**
	 * Modificació d'un tipus d'expedient existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipus
	 *            La informació del tipus d'expedient per a fer la modificació.
	 * @param sequenciesAny
	 *            Els anys de les seqüències.
	 * @param sequenciesValor
	 *            Els valors de les seqüències.
	 * @return el tipus d'expedient modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto update(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra una entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void delete(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna un tipus d'expedient donat el seu id.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return El tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto findAmbIdPerDissenyar(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna un tipus d'expedient donat el seu codi.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param codi
	 *            El codi per a la consulta.
	 * @return El tipus d'expedient o null si no el troba.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public ExpedientTipusDto findAmbCodiPerValidarRepeticio(
			Long entornId,
			String codi) throws NoTrobatException;

	/** 
	 * Retorna la llista de tipus d'expedient paginada per la datatable.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<ExpedientTipusDto> findPerDatatable(
			Long entornId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;

	/**
	 * Modifica un permis existent d'un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param permis
	 *            La informació del permis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public PermisDto permisUpdate(
			Long entornId,
			Long expedientTipusId,
			PermisDto permis) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els permisos per a un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return els permisos del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<PermisDto> permisFindAll(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna un permis donat el seu id.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param permisId
	 *            Atribut id del permis.
	 * @return el permis amb l'id especificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public PermisDto permisFindById(
			Long entornId,
			Long expedientTipusId,
			Long permisId) throws NoTrobatException, PermisDenegatException;

}
