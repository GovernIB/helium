/**
 * 
 */
package es.caib.helium.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.commons.dto.ExplotacioDimensioDto;
import es.caib.helium.persistence.entity.ExplotacioDimensio;

/**
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExplotacioDimensioRepository extends JpaRepository<ExplotacioDimensio, Long> {

	@Query("from ExplotacioDimensio order by unitatOrganitzativaId, unitatOrganitzativaCodi, entornId, entornCodi, tipusId, tipusCodi")
	List<ExplotacioDimensio> findAllOrdered();

	@Query(   "	SELECT new es.caib.helium.commons.dto.ExplotacioDimensioDto( "
			+ "		uo.id, "
			+ "		uo.codi, "
			+ "		e.entorn.id, "
			+ "		e.entorn.codi, "
			+ "		e.tipus.id, "
			+ "		e.tipus.codi) "
			+ "	FROM Expedient e "
			+ "		 LEFT OUTER JOIN e.unitatOrganitzativa uo "
			+ "	GROUP BY uo.id, uo.codi, e.entorn.id, e.entorn.codi, e.tipus.id, e.tipus.codi "
			+ "	ORDER BY uo.id, e.entorn.id, e.tipus.id ")
	public List<ExplotacioDimensioDto> getDimensionsPerEstadistiques();

	@Query(" FROM ExplotacioDimensio ed"
			+ " WHERE "
			+ "	 ed.entornId = :entornId AND "
			+ "	 ed.tipusId = :tipusId AND "
			+ "	 (	(:isUnitatOrganitzativaIdNull = true AND ed.unitatOrganitzativaId is null) OR"
			+ "	 	(:isUnitatOrganitzativaIdNull = false AND ed.unitatOrganitzativaId = :unitatOrganitzativaId)"
			+ "	 )")
	public ExplotacioDimensio findFirstByEntornIdAndTipusIdAndUnitatOrganitzativaIdOrderById(
			@Param("entornId") Long entornId,
			@Param("tipusId") Long tipusId,
			@Param("unitatOrganitzativaId") Long unitatOrganitzativaId,
			@Param("isUnitatOrganitzativaIdNull") Boolean isUnitatOrganitzativaIdNull);

}
