package es.caib.helium.logic.intf.service;

import es.caib.helium.logic.intf.dto.EnumeracioDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusEnumeracioValorDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.exception.ValidacioException;

import java.util.List;

;

/**
 * Servei per al manteniment d'enumeracions.
 * 
 */
public interface EnumeracioService {

	public PaginaDto<EnumeracioDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;
	
	public EnumeracioDto create(
			Long entornId,
			Long expedientTipusId, 
			EnumeracioDto enumeracio) throws PermisDenegatException;
	
	public EnumeracioDto findAmbCodi(
			Long entornId,
			Long expedientTipusId,
			String codi);
	
	public void delete(
			Long enumeracioId) throws NoTrobatException, PermisDenegatException;

	/** Consulta les enumeracions globals no lligades a cap expedient.
	 * @param entornId
	 *            Atribut id de l'entorn
	 * @return Les enumeracions globals de l'entorn
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<EnumeracioDto> findGlobals(
			Long entornId) throws NoTrobatException;
	
	/** 
	 * Retorna l'enumeració del tipus d'expedient donat el seu identificador. Té en compte els
	 * heretats i informa el camps d'herència del dto.
	 * 
	 * @param expedientTipusId
	 * @param enumeracioId
	 * 
	 * @return L'enumeració del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public EnumeracioDto findAmbId(
			Long expedientTipusId, 
			Long enumeracioId) throws NoTrobatException;
	
	public EnumeracioDto update(
			EnumeracioDto enumeracio) throws NoTrobatException, PermisDenegatException;
	
	/// VALORS

	public PaginaDto<ExpedientTipusEnumeracioValorDto>valorFindPerDatatable(
			Long enumeracioId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;
	
	public ExpedientTipusEnumeracioValorDto valorsCreate(
			Long expedientTipusId, 
			Long enumeracioId,
			Long entornId,
			ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException;
	
	public void valorDelete(
			Long valorId) throws NoTrobatException, PermisDenegatException;
	
	public void enumeracioDeleteAllByEnumeracio(Long enumeracioId) throws NoTrobatException, PermisDenegatException, ValidacioException;
	
	public ExpedientTipusEnumeracioValorDto valorFindAmbCodi(
			Long expedientTipusId,
			Long enumeracioId,
			String codi) throws NoTrobatException;	
	
	public ExpedientTipusEnumeracioValorDto valorFindAmbId(
			Long valorId) throws NoTrobatException;
	
	public ExpedientTipusEnumeracioValorDto valorUpdate(
			ExpedientTipusEnumeracioValorDto enumeracio) throws NoTrobatException, PermisDenegatException;
	
	public boolean valorMoure(Long valorId, int posicio) throws NoTrobatException;

	List<ExpedientTipusEnumeracioValorDto> valorsFind(Long enumeracioId) throws NoTrobatException;
	
}