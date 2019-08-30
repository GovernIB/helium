/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Validacio;

/**
 * Dao pels objectes de tipus Validacio de camps de tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CampValidacioRepository extends JpaRepository<Validacio, Long> {
	
	@Query(	"from Validacio v " +
			"where " +
			"   v.camp.id = :campId " +
			"	and (:esNullFiltre = true " +
			"			or lower(v.expressio) like lower('%'||:filtre||'%') " +
			"			or lower(v.missatge) like lower('%'||:filtre||'%')) ")
	Page<Validacio> findByFiltrePaginat(
			@Param("campId") Long campId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);	

	@Query("from Validacio v " +
			"where " +
			"    v.camp.id = :campId " +
			"order by " +
			"    ordre")
	List<Validacio> findAmbCampOrdenats(
			@Param("campId") Long campId);

	@Query("select v from " +
			"    Validacio v " +
			"where " +
			"    v.camp.id=:campId " +
			"and v.ordre=:ordre")
	Validacio getAmbOrdre(@Param("campId") Long campId, 
			@Param("ordre") int ordre);

	/** Consulta el següent valor per a l'ordre de les validaciosn. */
	@Query(	"select coalesce( max( v.ordre), -1) + 1 " +
			"from Validacio v " +
			"where " +
			"    v.camp.id = :campId " )
	Integer getNextOrdre(@Param("campId") Long campId);		
}
