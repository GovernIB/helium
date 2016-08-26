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

import net.conselldemallorca.helium.core.model.hibernate.CampTasca;

/**
 * Dao pels objectes de tipus camp de formulari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CampTascaRepository extends JpaRepository<CampTasca, Long> {
	
	@Query("select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
			"and ct.order=:order")
	public CampTasca getAmbOrdre(@Param("tascaId") Long tascaId, @Param("order") int order);

	@Query("select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
			"order by " +
			"    ct.order")
	public List<CampTasca> findAmbTascaOrdenats(@Param("tascaId") Long tascaId);

	@Query("select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
			"and ct.camp.id=:campId")
	public CampTasca findAmbTascaCamp(@Param("tascaId") Long tascaId, @Param("campId") Long campId);

	@Query("select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
			"and ct.camp.codi=:campCodi")
	public CampTasca findAmbTascaCodi(@Param("tascaId") Long tascaId, @Param("campCodi") String campCodi);

	@Query(	"select count(ct)" +
			"from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id = :tascaId")
	public long countAmbTasca(
			@Param("tascaId") Long tascaId);
	
	@Query("from CampTasca ct " +
			"where " +
			"    ct.tasca.id = :tascaId " +
			"order by " +
			"    ct.order")
	public List<CampTasca> findAmbTascaIdOrdenats(
			@Param("tascaId") Long tascaId);

	
	@Query(	"from CampTasca ct " +
			"where " +
			"   ct.tasca.id = :tascaId " +
			"	and (:esNullFiltre = true or lower(ct.camp.codi) like lower('%'||:filtre||'%')) ")
	public Page<CampTasca> findByFiltrePaginat(
			@Param("tascaId") Long tascaId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);

	@Query("select ct " +
			"from CampTasca ct " +
			"where ct.tasca.id = :tascaId " +
			"order by ct.order asc ")
	List<CampTasca> findCampsTasca(
			@Param("tascaId") Long tascaId);
	
	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( ct.order), -1) + 1 " +
			"from CampTasca ct " +
			"where " +
			"    ct.tasca.id = :tascaId " )
	public Integer getNextOrdre(@Param("tascaId") Long tascaId);
	
}
