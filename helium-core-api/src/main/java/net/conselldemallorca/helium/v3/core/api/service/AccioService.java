package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;;

/**
 * Servei per al manteniment d'accions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AccioService {

	/**
	 * Crea una nova accio.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param accio
	 *            La informació de la accio a crear.
	 * @return la accio creada.
	 * @throws AccioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public AccioDto create(
			Long expedientTipusId,
			Long definicioProcesId,
			AccioDto accio) throws PermisDenegatException;
	
	/**
	 * Modificació d'una accio existent.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param accio
	 *            La informació de la accio a modificar.
	 * @return la accio modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws AccioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public AccioDto update(
			AccioDto accio) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param accioId
	 *            Atribut id de la accio.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws AccioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void delete(
			Long accioId) throws NoTrobatException, PermisDenegatException;	
	
	/** 
	 * Retorna la accio del tipus d'expedient donat el seu identificador.
	 * 
	 * @param expedientTipusId
	 * @param id 
	 * 
	 * @return La accio del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public AccioDto findAmbId(
			Long expedientTipusId, 
			Long id) throws NoTrobatException;	
	
	/**
	 * Retorna les accions per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @return les accions del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<AccioDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException;
	
	/** 
	 * Retorna la llista d'accions del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<AccioDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	
	
	/**
	 * Retorna una accio d'un tipus d'expedient donat el seu codi.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param codi
	 *            El codi per a la consulta.
	 * @return La accio del tipus d'expedient o null si no el troba.
	 */
	public AccioDto findAmbCodi(
			Long expedientTipusId,
			Long definicioProcesId,
			String codi) throws NoTrobatException;	
}