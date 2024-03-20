package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioListDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioMapeigResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;

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
			List<ExpedientTipusDto> expedientTipusDtoAccessibles,
			AnotacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams);
	
	/** Mètode per consultar el llistat d'identificadors de les anotacions a partir d'un filtre. */
	public List<Long> findIdsAmbFiltre(Long entornId, AnotacioFiltreDto filtreDto);

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
	 * @param comprovarPermis Indica si comprovar o no permisos de l'usuari actual ja que pot ser que la 
	 * 			incorporació sigui automàtica des de la recepció de l'anotació.
	 */
	public AnotacioDto incorporarReprocessarExpedient(
			Long anotacioId, 
			Long expedientTipusId, 
			Long expedientId, 
			boolean associarInteressats,
			boolean comprovarPermis,
			boolean reprocessar);
	
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
	
	/** Mètode per reprocessar una anotació que està en estat d'error de processament.
	 * 
	 * @param anotacioId
	 * @return 
	 * @throws Exception Llença excepció si no es té permís, no es troba l'anotació o hi ha algun
	 * error en el reprocessament.
	 * 
	 */
	public AnotacioDto reprocessar(
			Long anotacioId) throws Exception;

	/** Mètode per marcar com a pendent una anotació en estat de processament error.
	 * 
	 * @param anotacioId
	 * @return 
	 * @throws Exception Llença excepció si no es té permís, no es troba l'anotació o hi ha algun
	 * error en la operació.
	 * 
	 */
	public AnotacioDto marcarPendent(
			Long anotacioId) throws Exception;
	
	/** Mètode per consultar una anotació que està en estat comunicada i que hagi esgotat els reintents.
	 * 
	 * @param anotacioId
	 * @return 
	 * @throws Exception Llença excepció si no es té permís, no es troba l'anotació o hi ha algun
	 * error en el reprocessament.
	 * 
	 */
	public AnotacioDto reintentarConsulta(
			Long anotacioId) throws Exception;


	/** Mètode per consultar les firmes d'un annex
	 * 
	 * @param annexId
	 * @return
	 */
	public List<ArxiuFirmaDto> getAnnexFirmes(Long annexId);
	
	/** Reintenta el processament d'un annex per incorporar-lo a Helium.
	 * 
	 * @param anotacioId
	 * @param annexId
	 * @throws Llença excepció en cas de no anar bé.
	 * @return
	 */
	public void reintentarAnnex(Long anotacioId, Long annexId) throws Exception;

	/** Mètode en una nova transacció perquè s'han produït errors en PRO consultant
	 * anotacions per a un expedient. Issue #1480.
	 * @param expedientId
	 */
	public void esborrarAnotacionsExpedient(Long expedientId);

	/** Recupera el mapeig de Sistra i l'aplica a l'expedient.
	 * @return	Retorna un objecte de tipus <code>AnotacioMapeigResultatDto</code> amb el resultat del mapeig
	 * de variables, documents i adjunts per poder advertir a l'usuari o afegir una alerta dels mapejos que han fallat.
	 * @throws Exception 
	 */
	public AnotacioMapeigResultatDto reprocessarMapeigAnotacioExpedient(Long expedientId, Long anotacioId);

	/** Reintenta el processament dels annexos d'una anotació per incorporar-los a Helium.
	 * 
	 * @param anotacioId
	 * @throws Llença excepció en cas de no anar bé.
	 * @return
	 */
	public void reintentarTraspasAnotacio(Long anotacioId) throws Exception;

	/** Mètode per obtenir el contingut d'un annex (en la seva versió imprimible) per a la seva descàrrega
	 * 
	 * @param annexId
	 */
	public ArxiuDto getAnnexContingutVersioImprimible(Long annexId);

	/** Mètode per obtenir el contingut d'un annex per a la seva descàrrega
	 * 
	 * @param annexId
	 */
	public ArxiuDto getAnnexContingutVersioOriginal(Long annexId);

}