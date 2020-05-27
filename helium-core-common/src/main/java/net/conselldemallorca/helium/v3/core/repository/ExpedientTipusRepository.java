/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto;

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

	ExpedientTipus findById(Long expedientTipusId);

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
            @Param("anulat") Boolean anulat
            );
    
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
            + "			and (:isNullAturat = true or true = :aturat) "
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
            @Param("isNullAturat") boolean isNullAturat,
            @Param("aturat") Boolean aturat
            );
}
