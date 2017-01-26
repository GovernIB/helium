package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;;

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

	public List<EnumeracioDto> findAmbEntorn(
			Long entornId) throws NoTrobatException;
	
	public EnumeracioDto findAmbId(
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
	
}