package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;;

/**
 * Servei per al manteniment de terminis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiService {

	/**
	 * Crea un nou termini.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param termini	La informació del camp a crear.
	 * @return el termini creat.
	 */
	public TerminiDto create(
			Long expedientTipusId,
			Long definicioProcesId, 
			TerminiDto termini);
	
	/**
	 * Modificació d'un termini existent.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param trmini	La informació del termini a modificar.
	 * @return el termini modificat.
	 * @throws NoTrobatException	Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException	Si no es tenen els permisos necessaris.
	 */
	public TerminiDto update(TerminiDto termini);
	
	/**
	 * Esborra un termini.
	 * 
	 * @param terminiId	Atribut id del termini.
	 * @throws NoTrobatException	Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException	Si no es tenen els permisos necessaris.
	 */
	public void delete(Long terminiId) throws NoTrobatException, PermisDenegatException;
	
	/** 
	 * Retorna el termini donat el seu identificador.
	 * 
	 * @param expedientTipusId Identificador del tipus d'expedient al qual pertany si és d'un tipus d'expedient.
	 * @param terminiId
	 * @param id 
	 * 
	 * @return El termini del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public TerminiDto findAmbId(
			Long expedientTipusId, 
			Long terminiId) throws NoTrobatException;

	/**
	 * Retorna els terminis d'un tipus d'expedient sense tenir en compte la possible herència del tipus d'expedient o
	 * d'una definició de procés.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @return els terminis del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<TerminiDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException, PermisDenegatException;
	
	/** 
	 * Retorna la llista de terminis del tipus d'expedient paginada per la datatable.
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
	public PaginaDto<TerminiDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;

	/** Cerca el termini per codi per a la definicio de proces o el tipus d'expedient per validar la repeticio.
	 * 
	 * @param expedientTipusId
	 * @param definicioProcesId
	 * @param codi
	 * @return
	 */
	public TerminiDto findAmbCodi(Long expedientTipusId, Long definicioProcesId, String codi);		
}