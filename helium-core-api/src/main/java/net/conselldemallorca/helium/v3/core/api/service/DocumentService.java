package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;

/**
 * Servei per al manteniment de documents dels tipus d'expedient o de les definicions de procés.
 * 
 */
public interface DocumentService {

	/** 
	 * Retorna la llista de camps del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<DocumentDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;
	
	public DocumentDto create(
			Long expedientTipusId,
			Long definicioProcesId, 
			DocumentDto document) throws PermisDenegatException;
	
	public DocumentDto findAmbCodi(
			Long expedientTipusId,
			Long definicioProcesId, 
			String codi);

	/**
	 * Retorna tots els documents d'un tipus d'expedient o definició de procés
	 *  donat el seu identificador ordenat pel codi del document.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @return Els documents del tipus d'expedient.
	 */
	public List<DocumentDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId);	
	
	public void delete(
			Long documentId) throws NoTrobatException, PermisDenegatException;
	
	public DocumentDto findAmbId(
			Long documentId) throws NoTrobatException;
	
	public DocumentDto update(
			DocumentDto document,
			boolean actualitzarContingut) throws NoTrobatException, PermisDenegatException;
		
	public ArxiuDto getArxiu(
			Long documentId) throws NoTrobatException;

}