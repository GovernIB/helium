/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.logic.intf.dto.ExpedientTipusEstadisticaDto;
import es.caib.helium.persist.entity.Entorn;
import es.caib.helium.persist.entity.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un tipus d'expedient que està emmagatzemat a dins
 * la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTipusRepository extends JpaRepository<ExpedientTipus, Long> {

	List<ExpedientTipus> findByEntorn(Entorn entorn);
	
	List<Long> findIdByEntornId(Long entornId);
	
	List<ExpedientTipus> findByEntornOrderByNomAsc(Entorn entorn);
	
	List<ExpedientTipus> findByEntornOrderByCodiAsc(Entorn entorn);

	ExpedientTipus findByEntornAndCodi(Entorn entorn, String codi);

	ExpedientTipus findByEntornAndId(Entorn entorn, Long id);

	Optional<ExpedientTipus> findById(Long expedientTipusId);

	/** Per consultar l'expedient tipus amb bloqueig de BBDD per actualitzar les seqüències de números
	 * @param expedientTipusId
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from ExpedientTipus where id = :expedientTipusId")
	public ExpedientTipus findByIdAmbBloqueig(@Param("expedientTipusId") Long expedientTipusId);

	@Query(	"from ExpedientTipus e " +
			"where " +
			"    e.entorn = :entorn " +
			"and e.id in (:tipusPermesosIds) " +
			"and (:esNullFiltre = true or lower(e.nom) like lower('%'||:filtre||'%') or lower(e.codi) like lower('%'||:filtre||'%')) ")
	Page<ExpedientTipus> findByFiltreGeneralPaginat(
			@Param("entorn") Entorn entorn,
			@Param("tipusPermesosIds") Collection<Long> tipusPermesosIds,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	@Query(	"from ExpedientTipus e " +
			"where " +
			"    e.entorn = :entorn " +
			"	and e.heretable = true " +
			"order by e.nom asc ")
	List<ExpedientTipus> findHeretablesByEntorn( @Param("entorn") Entorn entorn);
	
	List<ExpedientTipus> findByExpedientTipusPareIdOrderByCodiAsc(Long expedientTipusPareId);

	/** Mètode per cercar un tipus d'expedient activat per distribuir anotacions de registre i configurat segons el codi
	 * de procediment o el codi del tipus d'assumpte.
	 * 
	 * @param codiProcediment
	 * @param codiAssumpte
	 * @return
	 */
	@Query(	"from " +
			"    ExpedientTipus et " +
			"where " +
			"	et.distribucioActiu = true " + 
			"and ((et.distribucioCodiProcediment is null) or (et.distribucioCodiProcediment = :codiProcediment)) " + 
			"and ((et.distribucioCodiAssumpte is null) or (et.distribucioCodiAssumpte = :codiAssumpte)) " +
			"order by " + 
			"	et.distribucioCodiProcediment asc NULLS LAST, " +
			"	et.distribucioCodiAssumpte asc NULLS LAST ")
	List<ExpedientTipus> findPerDistribuir(
			@Param("codiProcediment") String codiProcediment, 
			@Param("codiAssumpte") String codiAssumpte);
	
	/** Mètode per cercar un tipus d'expedient per validar la configuració de distribució segons els valors
	 * del codi de procediment i el codi d'assumpte.
	 * 
	 * @param codiProcediment
	 * @param esNullCodiProcediment
	 * @param codiAssumpte
	 * @param esNullCodiAssumpte
	 * @return
	 */
	@Query(	"from " +
			"    ExpedientTipus et " +
			"where " +
			"	et.distribucioActiu = true " + 
			"and ((:esNullCodiProcediment = true and et.distribucioCodiProcediment is null) or (et.distribucioCodiProcediment like :codiProcediment)) " + 
			"and ((:esNullCodiAssumpte = true and et.distribucioCodiAssumpte is null) or (et.distribucioCodiAssumpte like :codiAssumpte)) ")
	List<ExpedientTipus> findPerDistribuirValidacio(
			@Param("esNullCodiProcediment") boolean esNullCodiProcediment, 
			@Param("codiProcediment") String codiProcediment,
			@Param("esNullCodiAssumpte") boolean esNullCodiAssumpte,
			@Param("codiAssumpte") String codiAssumpte);
    
    @Query(    " select new net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto( "
            + "     et.id, "
            + "     et.codi, "
            + "     et.nom, "
            + "     count(*), "
            + "     to_char(e.dataInici, 'YYYY'))"
            + "    	from Expedient e "
            + "        inner join e.tipus as et  "
            + "    	where "
            + "     	et.entorn = :entorn"
            + "    		and (:isNullExpedientTipus = true or et = :expedientTipus)"
            + "			and (:isNullAnulat = true or e.anulat = :anulat) "
			+ "			and (:isNullDataIniciFinal = true or year(e.dataInici) <= :anyFinal) "
			+ "			and (:isNullDataIniciInicial = true or year(e.dataInici) >= :anyInicial) "
			+ "			and (:isNullNumero = true or lower(e.numero) like lower('%'||:numero||'%') ) "
			+ "			and (:isNullTitol = true or lower(e.titol) like lower('%'||:titol||'%') ) "
			+ "			and (:nomesIniciats = false or e.dataFi is null) "
			+ "			and (:nomesFinalitzats = false or e.dataFi is not null) "
            + "			and (:isNullAturat = true or (:aturat = true and e.infoAturat is not null) or (:aturat = false and e.infoAturat is null)) "
            + "			and (:isEstatIdNull = true or e.estat.id = :estatId) "
            + " group by "
            + "        et.id, "
            + "        et.codi, "
            + "        et.nom, "
            + "        to_char(e.dataInici, 'YYYY')")
    List<ExpedientTipusEstadisticaDto> findEstadisticaByFiltre(
            @Param("isNullDataIniciInicial") boolean isNullDataIniciInicial,
            @Param("anyInicial") Integer anyInicial,
            @Param("isNullDataIniciFinal") boolean isNullDataIniciFinal,
            @Param("anyFinal") Integer anyFinal,
            @Param("entorn") Entorn entorn,
            @Param("isNullExpedientTipus") boolean isNullExpedientTipus,
            @Param("expedientTipus") ExpedientTipus expedientTipus,
            @Param("isNullAnulat") boolean isNullAnulat,
            @Param("anulat") Boolean anulat,
            @Param("isNullNumero") boolean isNullNumero,
            @Param("numero") String numero, 
            @Param("isNullTitol") boolean isNullTitol,
            @Param("titol") String titol, 
            @Param("nomesIniciats") boolean nomesIniciats,
			@Param("nomesFinalitzats") boolean nomesFinalitzats,
			@Param("isEstatIdNull") boolean isEstatIdNull,
			@Param("estatId") Long estatId,
            @Param("isNullAturat") boolean isNullAturat,
            @Param("aturat") Boolean aturat
            );

    /** Consulta de la llista de tipus d'expedients configurats per a u
     * codi de tràmit SISTRA.
     * 
     * @param tramitCodi
     * @return
     */
	List<ExpedientTipus> findBySistraTramitCodi(String tramitCodi);
}
