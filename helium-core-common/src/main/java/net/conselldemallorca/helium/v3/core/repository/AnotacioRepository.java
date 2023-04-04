/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;

/**
 * Repository per treballar amb entitats de tipus Anotacions provinents de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AnotacioRepository extends JpaRepository<Anotacio, Long> {
	

	/** Mètode per consultar les anotacions de distribució amb filtre pagina des del llistat d'anotacions o amb filtre i expedient.id des de la pestanya d'
	 * anotacions de la gestió de l'expedient.
	 * @param esNullCodiProcediment
	 * @param codiProcediment
	 * @param esNullCodiAssumpte
	 * @param codiAssumpte
	 * @param esNullNumeroExpedient
	 * @param numeroExpedient
	 * @param esNullNumero
	 * @param numero
	 * @param esNullExtracte
	 * @param extracte
	 * @param esNullDataInicial
	 * @param dataInicial
	 * @param esNullDataFinal
	 * @param dataFinal
	 * @param esNullEstat
	 * @param estat
	 * @param esNullExpedientTipusId
	 * @param expedientTipusId
	 * 			Es pot filtrar des del llistat d'anotacions per un tipus d'expedient concret
	 * @param esNullExpedientId
	 * @param expedientId
	 * 			Des de la pestanya d'anotacions de l'expedient es pot filtrar les anotacions per a un expedient
	 * @param esNullExpedientTipusIds
	 * @param expedientTipusIds
	 * 			Al llistat d'anotacions es filtren per tipus d'expedients permesos en el cas que l'usuari no sigui administrador
	 * @param esNullFiltre
	 * @param filtre
	 * @param pageable
	 * @return
	 */
	@Query(	"from Anotacio a " +
			"where " +
			"    (:esNullCodiProcediment = true or lower(a.procedimentCodi) like lower('%'||:codiProcediment||'%')) " +
			"and (:esNullCodiAssumpte = true or lower(a.assumpteCodiCodi) like lower('%'||:codiAssumpte||'%')) " +
			"and (:esNullNumeroExpedient = true or lower(a.expedientNumero) like lower('%'||:numeroExpedient||'%')) " +
			"and (:esNullNumero = true or lower(a.identificador) like lower('%'||:numero||'%')) " +
			"and (:esNullExtracte = true or lower(a.extracte) like lower('%'||:extracte||'%')) " +
			"and (:esNullDataInicial = true or a.data >= :dataInicial) " +
			"and (:esNullDataFinal = true or a.data <= :dataFinal) " +
			"and (:esNullEstat = true or a.estat = :estat) " + 
			"and (:esNullExpedientTipusId = true or a.expedientTipus.id = :expedientTipusId) " + 
			"and (:esNullExpedientId = true or a.expedient.id = :expedientId) " + 
			"and (:esNullExpedientTipusIds = true or a.expedientTipus.id in (:expedientTipusIds)) " + 
			"and (:esNullFiltre = true or (lower(a.identificador) like lower('%'||:filtre||'%')) or lower(a.extracte) like lower('%'||:filtre||'%')) ")
	Page<Anotacio> findAmbFiltrePaginat(
			@Param("esNullCodiProcediment") boolean esNullCodiProcediment,
			@Param("codiProcediment") String codiProcediment,
			@Param("esNullCodiAssumpte") boolean esNullCodiAssumpte,
			@Param("codiAssumpte") String codiAssumpte,
			@Param("esNullNumeroExpedient") boolean esNullNumeroExpedient,
			@Param("numeroExpedient") String numeroExpedient,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullExtracte") boolean esNullExtracte,
			@Param("extracte") String extracte,
			@Param("esNullDataInicial") boolean esNullDataInicial,
			@Param("dataInicial") Date dataInicial,
			@Param("esNullDataFinal") boolean esNullDataFinal,
			@Param("dataFinal") Date dataFinal,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") AnotacioEstatEnumDto estat, 
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("esNullExpedientId") boolean esNullExpedientId,
			@Param("expedientId") Long expedientId, 
			@Param("esNullExpedientTipusIds") boolean esNullExpedientTipusIds,
			@Param("expedientTipusIds") List<Long> expedientTipusIds, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre, 
			Pageable pageable);
	
	/** Mètode per consultar les anotacions de distribució amb filtre pagina des del llistat d'anotacions o amb filtre i expedient.id des de la pestanya d'
	 * anotacions de la gestió de l'expedient.
	 * @param esNullCodiProcediment
	 * @param codiProcediment
	 * @param esNullCodiAssumpte
	 * @param codiAssumpte
	 * @param esNullNumeroExpedient
	 * @param numeroExpedient
	 * @param esNullNumero
	 * @param numero
	 * @param esNullExtracte
	 * @param extracte
	 * @param esNullDataInicial
	 * @param dataInicial
	 * @param esNullDataFinal
	 * @param dataFinal
	 * @param esNullEstat
	 * @param estat
	 * @param esNullExpedientTipusId
	 * @param expedientTipusId
	 * 			Es pot filtrar des del llistat d'anotacions per un tipus d'expedient concret
	 * @param esNullExpedientId
	 * @param expedientId
	 * 			Des de la pestanya d'anotacions de l'expedient es pot filtrar les anotacions per a un expedient
	 * @param esNullExpedientTipusIds
	 * @param expedientTipusIds
	 * 			Al llistat d'anotacions es filtren per tipus d'expedients permesos en el cas que l'usuari no sigui administrador
	 * @return
	 */
	@Query(	"select a.id " +
			"from Anotacio a " +
			"where " +
			"    (:esNullCodiProcediment = true or lower(a.procedimentCodi) like lower('%'||:codiProcediment||'%')) " +
			"and (:esNullCodiAssumpte = true or lower(a.assumpteCodiCodi) like lower('%'||:codiAssumpte||'%')) " +
			"and (:esNullNumeroExpedient = true or lower(a.expedientNumero) like lower('%'||:numeroExpedient||'%')) " +
			"and (:esNullNumero = true or lower(a.identificador) like lower('%'||:numero||'%')) " +
			"and (:esNullExtracte = true or lower(a.extracte) like lower('%'||:extracte||'%')) " +
			"and (:esNullDataInicial = true or a.data >= :dataInicial) " +
			"and (:esNullDataFinal = true or a.data <= :dataFinal) " +
			"and (:esNullEstat = true or a.estat = :estat) " + 
			"and (:esNullExpedientTipusId = true or a.expedientTipus.id = :expedientTipusId) " + 
			"and (:esNullExpedientId = true or a.expedient.id = :expedientId) " + 
			"and (:esNullExpedientTipusIds = true or a.expedientTipus.id in (:expedientTipusIds)) ")
	List<Long> findIdsAmbFiltre (
			@Param("esNullCodiProcediment") boolean esNullCodiProcediment,
			@Param("codiProcediment") String codiProcediment,
			@Param("esNullCodiAssumpte") boolean esNullCodiAssumpte,
			@Param("codiAssumpte") String codiAssumpte,
			@Param("esNullNumeroExpedient") boolean esNullNumeroExpedient,
			@Param("numeroExpedient") String numeroExpedient,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullExtracte") boolean esNullExtracte,
			@Param("extracte") String extracte,
			@Param("esNullDataInicial") boolean esNullDataInicial,
			@Param("dataInicial") Date dataInicial,
			@Param("esNullDataFinal") boolean esNullDataFinal,
			@Param("dataFinal") Date dataFinal,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") AnotacioEstatEnumDto estat, 
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("esNullExpedientId") boolean esNullExpedientId,
			@Param("expedientId") Long expedientId, 
			@Param("esNullExpedientTipusIds") boolean esNullExpedientTipusIds,
			@Param("expedientTipusIds") List<Long> expedientTipusIds);
	
	/** Mètode per recuperar les peticions d'anotació per id de Distribucio. */
	List<Anotacio> findByDistribucioIdAndDistribucioClauAcces(String distribucioId, String distribucioClauAcces);
	
	/** Mètode per recuperar les anotacions associades a un expedient. */
	@Query("from Anotacio a where a.expedient.id = :expedientId")
	List<Anotacio> findByExpedientId(@Param("expedientId") Long expedientId);

	/** Mètode per recuperar les anotacions associades a un tipus d'expedient. */
	@Query("from Anotacio a where a.expedientTipus.id = :expedientTipusId")
	List<Anotacio> findByExpedientTipusId(@Param("expedientTipusId") Long expedientTipusId);

	@Query(
			"from" +
			"    Anotacio a " +
			"where " +
			"    a.estat = net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto.COMUNICADA " +
			"and a.consultaIntents < :maxReintents ")
	public Page<Anotacio> findAnotacionsPendentConsultarPaged(
			@Param("maxReintents") int maxReintents,
			Pageable pageable);

}
