/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;


import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;

/**
 * Servei per a gestionar els entorns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntornService {

	/**
	 * Crea un nou entorn.
	 * 
	 * @param entorn
	 *            La informació de l'entorn a crear.
	 * @return l'entorn creat.
	 */
	public EntornDto create(
			EntornDto entorn);

	/**
	 * Modificació d'un entorn existent.
	 * 
	 * @param entorn
	 *            La informació de l'entorn a crear.
	 * @return el tipus d'expedient modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public EntornDto update(
			EntornDto entorn) throws NoTrobatException;

	/**
	 * Esborra un entorn.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public void delete(
			Long entornId) throws NoTrobatException;

	/** 
	 * Retorna la llista d'entorns paginada per la datatable.
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat d'entorns.
	 */
	public PaginaDto<EntornDto> findPerDatatable(
			String filtre, 
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta les dades d'un entorn donat el seu id.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @return L'entorn amb l'id especificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public EntornDto findOne(
			Long entornId) throws NoTrobatException;

	/**
	 * Consulta les dades d'un entorn donat el seu codi.
	 * 
	 * @param entornCodi
	 *            Atribut codi de l'entorn.
	 * @return L'entorn amb el codi especificat.
	 */
	public EntornDto findAmbCodi(String entornCodi);

	/** Consulta l'entorn amb permís d'accés
	 * 
	 * @param entornId
	 * @return
	 */
	public EntornDto findAmbIdPermisAcces(Long entornId);

	
	/**
	 * Retorna una llista d'entorns per als quals l'usuari actual
	 * te permis d'accés (READ).
	 * 
	 * @return la llista d'entorns.
	 */
	public List<EntornDto> findActiusAmbPermisAcces() throws NoTrobatException;

	/**
	 * Retorna una llista amb tots els entorns.
	 * 
	 * @return la llista d'entorns.
	 */
	public List<EntornDto> findActiusAll();

	/**
	 * Modifica un permis existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param permis
	 *            La informació del permis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public void permisUpdate(
			Long entornId,
			PermisDto permis) throws NoTrobatException;

	/**
	 * Esborra un permis existent d'un entorn.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param permisId
	 *            Atribut id del permis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public void permisDelete(
			Long entornId,
			Long permisId) throws NoTrobatException;

	/**
	 * Retorna els permisos per a un entorn.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @return els permisos de l'entorn.
	 */
	public List<PermisDto> permisFindAll(
			Long entornId);

	/**
	 * Retorna un permis donat el seu id.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param permisId
	 *            Atribut id del permis.
	 * @return el permis amb l'id especificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PermisDto permisFindById(
			Long entornId,
			Long permisId);
	
	/**
	 * Retorna una llista d'entorns sobre els que es tenen permisos d'administració
	 * @return llista d'entorns
	 */
	public List<EntornDto> findActiusAmbPermisAdmin();


}
