/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.CampRegistre;

/**
 * Repositori per a les dades dels registres dels camps de variables de tipus registres.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CampRegistreRepository extends JpaRepository<CampRegistre, Long> {

	/** Consulta el següent valor per a ordre dels camps del registre. */
	@Query(	"select coalesce( max( cr.ordre), -1) + 1 " +
			"from CampRegistre cr " +
			"where " +
			"    cr.registre.id = :campId " )
	Integer getNextOrdre(@Param("campId") Long campId);

	@Query("from CampRegistre cr " +
			"where " +
			"    cr.registre.id = :campId " +
			"order by " +
			"    cr.ordre")
	List<CampRegistre> findAmbCampOrdenats(
			@Param("campId") Long campId);
	
	@Query( "select cr.membre " +
			"from CampRegistre cr " +
			"where " +
			"    cr.registre.id = :registreId ")
	List<Camp> findMembresAmbRegistreId(@Param("registreId") Long registreId);

	@Query(	"from CampRegistre cr " +
			"where " +
			"   cr.registre.id = :campId")
	Page<CampRegistre> findByFiltrePaginat(
			@Param("campId") Long campId,
			Pageable pageable);
}
