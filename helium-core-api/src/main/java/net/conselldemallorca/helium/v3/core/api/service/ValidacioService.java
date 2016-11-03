package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;

public interface ValidacioService {
	/**
	 * Crea una nova validacio.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param campId
	 *            Atribut id del camp del tipus d'expedient.
	 * @param validacio
	 *            La informació de la validacio a crear.
	 * @return la validacio creada.
	 * @throws ValidacioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ValidacioDto validacioCreate(
			Long campId,
			ValidacioDto validacio) throws PermisDenegatException;
	
	/**
	 * Modificació d'una validacio existent.
	 * 
	 * @param validacio
	 *            La informació de la validacio a modificar.
	 * @return la validacio modificada.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ValidacioDto validacioUpdate(
			ValidacioDto validacio) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra una validacio.
	 * 
	 * @param id
	 *            Atribut id de la validacio.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void validacioDelete(
			Long id) throws NoTrobatException, PermisDenegatException;	
	
	/** 
	 * Retorna la validacio donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return La validacio de la variable.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public ValidacioDto validacioFindAmbId(
			Long id) throws NoTrobatException;	
	
	/** 
	 * Retorna la llista de validacios de la variable del tipus d'expedient paginada per la datatable.
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
	public PaginaDto<ValidacioDto> validacioFindPerDatatable(
			Long campId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	

	/** Mou la validació id de camp cap a la posició indicada reassignant el valor pel camp ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean validacioMourePosicio(Long id, int posicio);
}
