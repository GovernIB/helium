package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;

/** Servei per al manteniment de tipus d'expedient. */
public interface ExpedientTipusService {
	
	/**
	 * Per crear una nova entitat.
	 * 
	 * @param entornId
	 * @param codi
	 * @param nom
	 * @param teTitol
	 * @param demanaTitol
	 * @param teNumero
	 * @param demanaNumero
	 * @param expressioNumero
	 * @param reiniciarCadaAny
	 * @param sequenciesAny
	 * @param sequenciesValor
	 * @param map 
	 * @param sequencia
	 * @param responsableDefecteCodi
	 * @param restringirPerGrup
	 * @param seleccionarAny
	 * @param ambRetroaccio
	 * 
	 * @return El dto de la entitat creada.
	 * 
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientTipusDto create(
			Long entornId,
			String codi,
			String nom,
			boolean teTitol,
			boolean demanaTitol,
			boolean teNumero,
			boolean demanaNumero,
			String expressioNumero,
			boolean reiniciarCadaAny,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor, 
			Long sequencia,// = 1,
			String responsableDefecteCodi,
			boolean restringirPerGrup,
			boolean seleccionarAny,
			boolean ambRetroaccio);

	/** Per modificar una entitat existent per id.
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @param nom
	 * @param teTitol
	 * @param demanaTitol
	 * @param teNumero
	 * @param demanaNumero
	 * @param expressioNumero
	 * @param reiniciarCadaAny
	 * @param sequenciesAny
	 * @param sequenciesValor
	 * @param sequencia
	 * @param responsableDefecteCodi
	 * @param restringirPerGrup
	 * @param seleccionarAny
	 * @param ambRetroaccio
	 * 
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void update(
			Long entornId,
			Long expedientTipusId,
			String nom,
			boolean teTitol,
			boolean demanaTitol,
			boolean teNumero,
			boolean demanaNumero,
			String expressioNumero,
			boolean reiniciarCadaAny,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor, 
			Long sequencia,
			String responsableDefecteCodi,
			boolean restringirPerGrup,
			boolean seleccionarAny,
			boolean ambRetroaccio);
	
	/**
	 * Esborra una entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn per comprovar permisos.
	 * @param expedientTipusId
	 *            Atribut id de la entitat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void delete(
			Long entornId,
			Long expedientTipusId);

	/** Retorna la llista de tipus d'expedient per al datatable de tipus d'expedient.
	 * @param filtre
	 *            Cadena de text per filtrar per codi o el nom
	 * @param paginacioParams
	 *            Paràmetres de paginació.
	 * @return La pàgina del llistat de tipus d'expedients.
	 */
	public PaginaDto<ExpedientTipusDto> findTipusAmbFiltrePaginat(
			Long entornId,
			String filtre, 
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Retorna un tipus d'expedient donat el seu id.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn per comprovar permisos.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient que es vol consultar.
	 * @return El tipus d'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap tipus d'expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientTipusDto findTipusAmbId(
			Long entornId,
			Long expedientTipusId);

	/** Retorna un tipus d'expedient donat el seu codi.
	 * 
	 * @param entornId
	 * @param codi
	 * @return El tipus d'expedient o null si no el troba.
	 */
	public ExpedientTipusDto findTipusAmbCodi(
			Long entornId, 
			String codi);	
	
	/** Consulta si pot escriure el tipus d'expedient.
	 * 
	 * @param entornId
	 * @param id
	 * @return
	 */
	public boolean potEscriure(
			Long entornId,
			Long id);
	
	/** Consulta si pot esborrar el tipus d'expedient.
	 * 
	 * @param entornId
	 * @param id
	 * @return
	 */
	public boolean potEsborrar(
			Long entornId,
			Long id);

	/** Consulta si té permisos d'administració
	 * 
	 * @param entornId
	 * @param id
	 * @return
	 */
	public boolean potAdministrar(
			Long entornId,
			Long id);
}
