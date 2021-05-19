package es.caib.helium.domini.repository;

import es.caib.helium.domini.domain.Domini;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DominiRepository extends BaseRepository<Domini, Long>  {

    // TODO: Investigar per què no funcionen les comandes de delete
    @Modifying
    @Query("delete from Domini where id = ?1")
    void delete(Long dominiId);

    @Modifying
    @Query("delete from Domini")
    void deleteAll();

    Optional<Domini> findByEntornAndId(
            Long entorn,
            Long id);

    @Query("from Domini d " +
            "where d.entorn = :entorn " +
            "  and d.codi = :codi " +
            "  and d.expedientTipus is null")
    Optional<Domini> findByEntornAndCodi(
            @Param("entorn") Long entorn,
            @Param("codi") String codi);

    Optional<Domini> findByExpedientTipusAndCodi(
            Long expedientTipus,
            String codi);

    /** Consulta per expedient tipus i el codi. Té en compte l'herència. */
    @Query(	"from Domini d " +
            "where " +
            "  	d.codi = :codi " +
            "	and d.id not in ( " +
            // Llistat de sobreescrits
            "			select ds.id " +
            "			from Domini da, " +
            "				 Domini ds " +
            "			where " +
            "				ds.codi = da.codi " +
            "				and da.expedientTipus = :expedientTipus " +
            "			 	and ds.expedientTipus = :expedientTipusPare " +
            "   ) " +
            // Directes
            "  	and (d.expedientTipus = :expedientTipus " +
            // Heretats
            "		or d.expedientTipus = :expedientTipusPare) "
    )
    Optional<Domini> findByExpedientTipusAndCodiAmbHerencia(
            @Param("expedientTipus") Long expedientTipus,
            @Param("expedientTipusPare") Long expedientTipusPare,
            @Param("codi") String codi);
    
	@Query( "select ds " +
			"from Domini d, " +
			"	Domini ds " +
			"where " +
			"	ds.codi = d.codi " +
			" 	and d.expedientTipus.id = expedientTipusId " +
			" 	and ds.expedientTipus.id = expedientTipusPareId ")
	List<Domini> findSobreescrits(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("expedientTipusPareId") Long expedientTipusPareId);


}
