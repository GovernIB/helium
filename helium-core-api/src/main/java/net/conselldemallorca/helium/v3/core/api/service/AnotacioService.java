package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioListDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;;

/**
 * Servei per a la consulta i gestió d'anotacions de distribució. Poden veure anotacions i realitzar accions 
 * sobre anotacions  els usuaris administradors d'Helium o els que tinguin el permís de relacionar sobre
 * el tipus d'expedient. Poden veure anotacions de registre els usuaris amb permís de lectura sobre
 * el tipus d'expedient des de la pipella d'anotacions de l'expedient.
 * 
 */
public interface AnotacioService {

	/** Mètode per consultar en el llistat les diferents anotacions */
	public PaginaDto<AnotacioListDto> findAmbFiltrePaginat(
			Long entornId,
			AnotacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams);

	/** Mètode per consultar una anotació per identificador.
	 * 
	 * @param id
	 * @return
	 */
	public AnotacioDto findAmbId(Long id);

	/** Mètode per rebutjar una petició d'anotació de registre passant l'identificador i les observacions.
	 * Aquest mètode s'encarrega de canviar l'estat i notificar el canvi a Distribució.
	 * 
	 * @param anotacioId
	 * @param observacions
	 */
	public void rebutjar(Long anotacioId, String observacions);

	/** Mètode per actualitzar la relació amb el tipus d'expedient i l'expedient des de l'acció de guardar al formulari d'acceptar.
	 * 
	 * @param anotacioId
	 * @param expedientTipusId
	 * @param expedientId
	 */
	public AnotacioDto updateExpedient(Long anotacioId, Long expedientTipusId, Long expedientId);	
	
	/** Mètode per incorporar la informació d'una anotació de registre a un expedient existent.
	 * @param anotacioId
	 * @param expedientTipusId
	 * @param expedientId
	 * @param associarInteressats Indica si incloure els interessats als interessats de l'expedient.
	 */
	public AnotacioDto incorporarExpedient(Long anotacioId, Long expedientTipusId, Long expedientId, boolean associarInteressats);
	
	/**
	 * Esborra una petició d'anotació de registre de Distribucio
	 * 
	 * @param anotacioId
	 *            Atribut id de l'anotació.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void delete(
			Long anotacioId) throws NoTrobatException, PermisDenegatException;

	/** Mètode per obtenir el contingut d'un annex per a la seva descàrrega
	 * 
	 * @param annexId
	 */
	public ArxiuDto getAnnexContingut(Long annexId);

	/** Mètode per consultar les firmes d'un annex
	 * 
	 * @param annexId
	 * @return
	 */
	public List<ArxiuFirmaDto> getAnnexFirmes(Long annexId);
}