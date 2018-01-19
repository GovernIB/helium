package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampRegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;;

/**
 * Servei per al manteniment de camps dels tipus d'expedient o de les definicions de procés.
 * 
 */
public interface CampService {
	
	/**
	 * Crea un nou camp.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param camp
	 *            La informació del camp a crear.
	 * @return el camp creat.
	 * @throws CampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampDto create(
			Long expedientTipusId,
			Long definicioProcesId,
			CampDto camp) throws PermisDenegatException;
	
	/**
	 * Modificació d'un camp existent.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param camp
	 *            La informació del camp a modificar.
	 * @return el camp modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampDto update(
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
	public void delete(
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
	public CampDto findAmbId(
			Long expedientTipusId, Long id) throws NoTrobatException;	
	
	/** 
	 * Retorna la llista de camps del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param totes
	 * 			  Indica si llistat totes les variables tinguin o no agrupació.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés. Si és null llavors es llistaran les que no tinguin agrupació.
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<CampDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			boolean totes,
			Long agrupacioId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;
	
	/** 
	 * Retorna la llista de camps de tipus data per llistar en un selector.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @return Llistat de camps.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<CampDto> findTipusData(
			Long expedientTipusId,
			Long definicioProcesId) throws NoTrobatException;
	
	/**
	 * Retorna un camp d'un tipus d'expedient donat el seu codi.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param codi
	 *            El codi per a la consulta.
	 * @param herencia
	 *            Determina si tenir en compte els camps heretats
	 * @return El camp del tipus d'expedient o null si no el troba.
	 */
	public CampDto findAmbCodi(
			Long expedientTipusId,
			Long definicioProcesId,
			String codi,
			boolean herencia);

	/**
	 * Retorna tots els camps d'un tipus d'expedient donat el seu identificador.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @return Els camps del tipus d'expedient.
	 */
	public List<CampDto> findAllOrdenatsPerCodi(Long expedientTipusId, Long definicioProcesId);

	/**
	 * Retorna les agrupacions per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param herencia
	 * 			  Indica si incloure les possibles agrupacions heretades del tipus d'expedient.
	 * @return les agrupacions del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<CampAgrupacioDto> agrupacioFindAll(
			Long expedientTipusId,
			Long definicioProcesId,
			boolean herencia) throws NoTrobatException, PermisDenegatException;
		
	/**
	 * Crea una nova agrupació.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param agrupacio
	 *            La informació de la agrupació a crear.
	 * @return la agrupació creada.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampAgrupacioDto agrupacioCreate(
			Long expedientTipusId, 
			Long definicioProcesId, 
			CampAgrupacioDto agrupacio) throws PermisDenegatException;
	
	/**
	 * Modificació d'una agrupació existent.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param agrupacio
	 *            La informació de la agrupació a modificar.
	 * @return la agrupació modificada.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampAgrupacioDto agrupacioUpdate(
			CampAgrupacioDto agrupacio) throws NoTrobatException, PermisDenegatException;
	
	/** Mou la agrupacio id cap a la posició indicada reassignant el valor pel camp ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean agrupacioMourePosicio(Long id, int posicio);
	
	/**
	 * Esborra una entitat.
	 * 
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
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param codi
	 *            El codi per a la consulta.
	 * @return La agrupació de camps del tipus d'expedient o null si no el troba.
	 */
	public CampAgrupacioDto agrupacioFindAmbCodiPerValidarRepeticio(
			Long expedientTipusId, 
			Long definicioProcesId, 
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
	 * Retorna la llista d'agrupacions del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            Atribut id de la definició de procés.
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat d'agrupacions del tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<CampAgrupacioDto> agrupacioFindPerDatatable(
			Long expedientTipusId, 
			Long definicioProcesId, 
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	

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
	public boolean afegirAgrupacio(Long campId, Long agrupacioId);

	/**
	 * Remou un camp de la seva agrupació.
	 * 
	 * @param id
	 * 			Identificador del camp
	 * @return
	 * 			Retorna cert si s'ha remogut correctament o false si no existia el camp o no tenia
	 * 			agrupació.
	 */
	public boolean remoureAgrupacio(Long campId);

	/** Mou el camp id cap a la posició indicada reassignant el valor pel camp ordre dins de la agrupació.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean mourePosicio(Long id, int posicio);

	
	/**
	 * Crea un nou camp pel registre.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param campId
	 *            Atribut id del camp del tipus d'expedient.
	 * @param campRegistre
	 *            La informació del camp del registre a crear.
	 * @return el camp del registre creat.
	 * @throws CampRegistreDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampRegistreDto registreCreate(
			Long campId,
			CampRegistreDto campRegistre) throws PermisDenegatException;
	
	/**
	 * Modificació d'un camp del registre existent.
	 * 
	 * @param campRegistre
	 *            La informació del camp del registre a modificar.
	 * @return el camp del registre modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampRegistreDto registreUpdate(
			CampRegistreDto campRegistre) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un camp del registre.
	 * 
	 * @param id
	 *            Atribut id del camp del registre.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void registreDelete(
			Long id) throws NoTrobatException, PermisDenegatException;	
	
	/** 
	 * Retorna el camp del registre donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return La camp del registre.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public CampRegistreDto registreFindAmbId(
			Long id) throws NoTrobatException;	
	
	/** 
	 * Retorna els membres del campRegistre donat l'identificador del registre.
	 * 
	 * @param id
	 * 
	 * @return La llista de membres.
	 */
	public List<CampDto> registreFindMembresAmbRegistreId(Long registreId);

	/** 
	 * Retorna la llista de camps de la variable de tipus registre del tipus d'expedient paginada per la datatable.
	 * 
	 * @param campId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<CampRegistreDto> registreFindPerDatatable(
			Long campId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	

	/** Mou el camp del registre amb id de camp cap a la posició indicada reassignant el valor pel camp ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean registreMourePosicio(Long id, int posicio);
	
	/** Retorna la llista de tasques de la definició de procés que continguin el camp.
	 * 
	 * @param campId
	 * @return
	 */
	public List<TascaDto> findTasquesPerCamp(Long campId);

	/** Retorna la llista de consultes que continguin el camp.
	 * 
	 * @param campId
	 * @return
	 */
	public List<ConsultaDto> findConsultesPerCamp(Long campId);

	/** Retorna la llista de registres que continguin el camp.
	 * 
	 * @param campId
	 * @return
	 */
	public List<CampDto> findRegistresPerCamp(Long campId);
}