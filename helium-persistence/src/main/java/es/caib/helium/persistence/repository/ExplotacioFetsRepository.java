/**
 *
 */
package es.caib.helium.persistence.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persistence.entity.ExplotacioFets;

/**
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExplotacioFetsRepository extends JpaRepository<ExplotacioFets, Long> {

	@Query(value = " SELECT " +
			"               ep.ENTORN_ID, " +
			"               ep.TIPUS_ID, " +
			"               ep.UNITAT_ORGANITZATIVA_ID, " +
			"               SUM(ep.expedientsOberts) AS expedientsOberts, " +
			"               SUM(ep.expedientsTancats) AS expedientsTancats , " +
			"               SUM(CASE WHEN ep.anulat = 1 AND trunc(ep.DATA_ANULAT) = trunc(:data) THEN 1 ELSE 0 END) AS expedientsAnulats, " +
			"               SUM(ep.expedientsArxiu) AS expedientsArxiu, " +
			"               SUM(ep.expedientsNoAnulats) AS expedientsNoAnulats, " +
			"               SUM(ep.expedientsTotals) AS expedientsTotals, " +
			"               0 AS tasquesPendents, " +
			"               0 AS tasquesFinalitzades, " +
			"               0 AS anotacionPendents, " +
			"               0 AS anotacionProcessades, " +
			"               0 AS peticionsPinbal, " +
			"               0 AS peticionsPortafib, " +
			"               0 AS peticionsNotib " +
			"          FROM ( " +
			"			SELECT " +
			"				e.ENTORN_ID, " +
			"				e.TIPUS_ID, " +
			"				e.UNITAT_ORGANITZATIVA_ID, " +
			"				SUM(CASE WHEN trunc(e.data_Inici) = trunc(:data) THEN 1 ELSE 0 END) AS expedientsOberts, " +
			"				SUM(CASE WHEN e.data_Fi IS NOT NULL AND trunc(e.data_Fi) = trunc(:data) THEN 1 ELSE 0 END) AS expedientsTancats, " +
			"				SUM(CASE WHEN e.arxiu_Actiu = 1 AND e.arxiu_Uuid IS NOT NULL AND trunc(e.data_Inici) = trunc(:data) THEN 1 ELSE 0 END) AS expedientsArxiu, " +
			"				SUM(CASE WHEN (trunc(e.data_Inici) = trunc(:data) AND (e.anulat IS NULL OR e.anulat = 0)) THEN 1 ELSE 0 END) AS expedientsNoAnulats, " +
			"				SUM(CASE WHEN trunc(e.data_Inici) = trunc(:data) THEN 1 ELSE 0 END) AS expedientsTotals, " +
			"				e.anulat, " +
			"				MAX(rr.DATA) AS DATA_ANULAT " +
			"			FROM " +
			"				HEL_EXPEDIENT e " +
			"			LEFT JOIN HEL_REGISTRE rr ON " +
			"				rr.EXPEDIENT_ID = e.ID " +
			"				AND rr.ACCIO = 9 " +
			"			WHERE " +
			"				trunc(e.data_Inici) <= trunc(:data) " +
			"				OR trunc(e.data_Fi) <= trunc(:data) " +
			"			GROUP BY " +
			"				e.ENTORN_ID, " +
			"				e.TIPUS_ID, " +
			"				e.UNITAT_ORGANITZATIVA_ID, " +
			"				e.anulat " +
			"  ) ep " +
			" GROUP BY "+
			" ep.ENTORN_ID, " +
			" ep.TIPUS_ID, " +
			" ep.UNITAT_ORGANITZATIVA_ID"
		, nativeQuery = true)
	public List<Object[]> getFetsPerEstadistiques(
			@Param("data") Date data);

	@Query("from ExplotacioFets ef where ef.temps.id = :tempsId order by ef.dimensio.unitatOrganitzativaId, ef.dimensio.unitatOrganitzativaCodi, ef.dimensio.entornId, ef.dimensio.entornCodi, ef.dimensio.tipusId, ef.dimensio.tipusCodi")
	public List<ExplotacioFets> findByTempsId(@Param("tempsId") Long tempsId);
}
