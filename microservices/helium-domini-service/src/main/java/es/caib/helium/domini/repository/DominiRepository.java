package es.caib.helium.domini.repository;

import es.caib.helium.domini.domain.Domini;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DominiRepository extends BaseRepository<Domini, Long>  {

    Optional<Domini> findByEntornAndId(
            Long entorn,
            Long id);

    Optional<Domini> findByEntornAndCodi(
            Long entorn,
            String codi);

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

}
