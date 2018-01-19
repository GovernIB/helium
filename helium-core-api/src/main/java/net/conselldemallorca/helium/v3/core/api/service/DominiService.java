package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;;

/**
 * Servei per al manteniment de dominis.
 * 
 */
public interface DominiService {

	/** 
	 * Retorna la llista de dominis paginat per la datatable.
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @param incloureGlobals
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */	public PaginaDto<DominiDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;
	
	public DominiDto create(
			Long entornId,
			Long expedientTipusId, 
			DominiDto domini) throws PermisDenegatException;
	
	public DominiDto findAmbCodi(
			Long entornId,
			Long expedientTipusId,
			String codi);
	
	public void delete(
			Long dominiId) throws NoTrobatException, PermisDenegatException;

	/** Consulta els dominis globals no lligats a cap expedient.
	 * @param entornId
	 *            Atribut id de l'entorn
	 * @return Els dominis globals de l'entorn
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<DominiDto> findGlobals(
			Long entornId) throws NoTrobatException;
	
	public DominiDto findAmbId(
			Long expedientTipusId,
			Long dominiId) throws NoTrobatException;
	
	public DominiDto update(
			DominiDto domini) throws NoTrobatException, PermisDenegatException;	
	
}