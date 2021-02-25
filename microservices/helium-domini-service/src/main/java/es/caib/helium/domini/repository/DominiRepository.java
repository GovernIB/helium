package es.caib.helium.domini.repository;

import es.caib.helium.domini.domain.Domini;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DominiRepository extends BaseRepository<Domini, Long>  {

    Optional<Domini> findByEntornAndId(
            Long entorn,
            Long id);

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


//    // Per Tipus expedient
//    // ====================================================================
//
//
//    @Query("from " +
//            "    Domini d " +
//            "where " +
//            "    d.expedientTipus = :expedientTipus " +
//            "order by " +
//            "    d.nom")
//    List<Domini> findAmbExpedientTipus(
//            @Param("expedientTipus") Long expedientTipus);
////    findAll(DominiSpecifications.belongsToExpedientTipus(expedientTipus));
//
//    // Per entorn (Glogals)
//    // ====================================================================
//
//    /** Troba el domini global per entorn i codi amb expedient tipus null. */
//    @Query(	"from " +
//            "    Domini d " +
//            "where " +
//            "    d.entorn = :entorn " +
//            "and d.codi = :codi " +
//            "and d.expedientTipus is null ")
//    Optional<Domini> findByEntornAndCodi(
//            @Param("entorn") Long entorn,
//            @Param("codi") String codi);
//
//    /** Consulta la llista de tots els dominis d'un entorn siguin o no globals. */
//    List<Domini> findByEntorn(Long entorn);
////    findAll(DominiSpecifications.belongsToEntorn(entorn));
//
//    /** Troba per entorn i codi global amb expedient tipus null. */
//    @Query(	"from " +
//            "    Domini d " +
//            "where " +
//            "    d.entorn = :entorn " +
//            "and d.expedientTipus is null ")
//    List<Domini> findGlobals(
//            @Param("entorn") Long entorn);
////    findAll(DominiSpecifications.isGlobal(entorn));
//
//    /** Troba les enumeracions per a un tipus d'expedient i també les globals de l'entorn i les ordena per nom.*/
//    @Query(	"from Domini d " +
//            "where " +
//            "   d.entorn = :entorn " +
//            "	and ((d.expedientTipus = :expedientTipus) or (d.expedientTipus is null )) " +
//            "order by " +
//            "	d.nom")
//    List<Domini> findAmbExpedientTipusIGlobals(
//            @Param("entorn") Long entorn,
//            @Param("expedientTipus") Long expedientTipus);
////    findAll(DominiSpecifications.belongsToExpedientTipusOrIsGlobal(entorn, expedientTipus));
//
//
//    // Llistat
//    // ====================================================================
//
//    @Query(	"from Domini d " +
//            "where " +
//            "   d.entorn = :entorn " +
//            "	and (:ambHerencia = false " +
//            "		or d.id not in ( " +
//            // Llistat de sobreescrits
//            "			select ds.id " +
//            "			from Domini da, " +
//            "				Domini ds " +
//            "			where " +
//            "				ds.codi = da.codi " +
//            "				and da.expedientTipus = :expedientTipus " +
//            "			 	and ds.expedientTipus = :expedientTipusPare " +
//            "		) " +
//            "	) " +
//            "	and ((d.expedientTipus = :expedientTipus) " +
//            // Heretats
//            "			or (:ambHerencia = true " +
//            "					and d.expedientTipus = :expedientTipusPare)  " +
//            "			or (d.expedientTipus is null and (:esNullExpedientTipus = true or :incloureGlobals = true))) " +
//            "	and (:esNullFiltre = true or lower(d.codi) like lower('%'||:filtre||'%') or lower(d.nom) like lower('%'||:filtre||'%')) ")
//    Page<Domini> findByFiltrePaginat(
//            @Param("entorn") Long entorn,
//            @Param("esNullExpedientTipus") boolean esNullExpedientTipus,
//            @Param("expedientTipus") Long expedientTipus,
//            @Param("expedientTipusPare") Long expedientTipusPare,
//            @Param("incloureGlobals") boolean incloureGlobals,
//            @Param("esNullFiltre") boolean esNullFiltre,
//            @Param("filtre") String filtre,
//            @Param("ambHerencia") boolean ambHerencia,
//            Pageable pageable);
////    findAll(DominiSpecifications.dominisList(entorn, expedientTipus, expedientTipusPare, incloureGlobals));
//
//    @Query( "select ds " +
//            "from Domini d, " +
//            "	  Domini ds " +
//            "where " +
//            "	ds.codi = d.codi " +
//            " 	and d.expedientTipus = :expedientTipus " +
//            " 	and ds.expedientTipus = :expedientTipusPare ")
//    List<Domini> findSobreescrits(
//            @Param("expedientTipus") Long expedientTipus,
//            @Param("expedientTipusPare") Long expedientTipusPare);
////    findAll(DominiSpecifications.isSobreescrit(expedientTipus, expedientTipusPare));

}
