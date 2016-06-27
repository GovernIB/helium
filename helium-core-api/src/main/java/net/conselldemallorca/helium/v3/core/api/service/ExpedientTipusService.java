package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
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
	 * Esborra un permis existent d'un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param permisId
	 *            Atribut id del permis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void permisDelete(
			Long entornId,
			Long expedientTipusId,
			Long permisId) throws NoTrobatException, PermisDenegatException;

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
	
	
	/**
	 * Retorna les agrupacions per a un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return les agrupacions del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<CampAgrupacioDto> agrupacioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;
		
	/**
	 * Crea una nova agrupació.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param agrupacio
	 *            La informació de la agrupació a crear.
	 * @return la agrupació creada.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampAgrupacioDto agrupacioCreate(
			Long expedientTipusId,
			CampAgrupacioDto agrupacio) throws PermisDenegatException;
	
	/**
	 * Modificació d'una agrupació existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param agrupacio
	 *            La informació de la agrupació a modificar.
	 * @return la agrupació modificada.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampAgrupacioDto agrupacioUpdate(
			Long expedientTipusId,
			CampAgrupacioDto agrupacio) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra una entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param agrupacioCampId
	 *            Atribut id de la agrupació.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void agrupacioDelete(
			Long agrupacioCampId) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Retorna una agrupació de camps d'un tipus d'expedient donat el seu codi.
	 * 
	 * @param tipusExpedientId
	 * @param codi
	 *            El codi per a la consulta.
	 * @return La agrupació de camps del tipus d'expedient o null si no el troba.
	 */
	public CampAgrupacioDto agrupacioFindAmbCodiPerValidarRepeticio(
			Long tipusExpedientId,
			String codi) throws NoTrobatException;	

	/** 
	 * Retorna la agrupació de camps del tipus d'expedient donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return La agrupació de camps del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public CampAgrupacioDto agrupacioFindAmbId(
			Long id) throws NoTrobatException;	

	/**
	 * Crea un nou camp.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param camp
	 *            La informació del camp a crear.
	 * @return el camp creat.
	 * @throws CampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampDto campCreate(
			Long expedientTipusId,
			CampDto camp) throws PermisDenegatException;
	
	/**
	 * Modificació d'un camp existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param camp
	 *            La informació del camp a modificar.
	 * @return el camp modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampDto campUpdate(
			CampDto camp) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param campCampId
	 *            Atribut id del camp.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void campDelete(
			Long campCampId) throws NoTrobatException, PermisDenegatException;	
	
	/** 
	 * Retorna el camp del tipus d'expedient donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return El camp del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public CampDto campFindAmbId(
			Long id) throws NoTrobatException;	
	
	/** 
	 * Retorna la llista de camps del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<CampDto> campFindPerDatatable(
			Long expedientTipusId,
			Long agrupacioId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	
	
	/**
	 * Retorna un camp d'un tipus d'expedient donat el seu codi.
	 * 
	 * @param tipusExpedientId
	 * @param codi
	 *            El codi per a la consulta.
	 * @return El camp del tipus d'expedient o null si no el troba.
	 */
	public CampDto campFindAmbCodiPerValidarRepeticio(
			Long tipusExpedientId,
			String codi) throws NoTrobatException;

	/**
	 * Afegeix un camp a una agrupació.
	 * @param id
	 * 			Identificador del camp
	 * @param agrupacioId
	 * 			Identificador de la agrupació
	 * @return
	 * 			Retorna true si ha anat bé o false si no s'ha trobat el camp o la agrupació 
	 * 			o el seu tipus d'expedient no és el mateix.
	 */
	public boolean campAfegirAgrupacio(Long campId, Long agrupacioId);

	/**
	 * Remou un camp de la seva agrupació.
	 * 
	 * @param id
	 * 			Identificador del camp
	 * @return
	 * 			Retorna cert si s'ha remogut correctament o false si no existia el camp o no tenia
	 * 			agrupació.
	 */
	public boolean campRemoureAgrupacio(Long campId);
	
	
	/***********************************************/
	/******************DOCUMENTS********************/
	/***********************************************/
	
	/** 
	 * Retorna la llista de camps del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<DocumentDto> documentFindPerDatatable(
			Long expedientTipusId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;
	
	public DocumentDto documentCreate(
			Long expedientTipusId, 
			DocumentDto document) throws PermisDenegatException;
	
	public DocumentDto documentFindAmbCodi(
			Long expedientTipusId, 
			String codi) throws NoTrobatException;
	
	public void documentDelete(
			Long documentId) throws NoTrobatException, PermisDenegatException;
	
	public DocumentDto documentFindAmbId(
			Long documentId) throws NoTrobatException;
	
	public DocumentDto documentUpdate(
			DocumentDto document) throws NoTrobatException, PermisDenegatException;
}
