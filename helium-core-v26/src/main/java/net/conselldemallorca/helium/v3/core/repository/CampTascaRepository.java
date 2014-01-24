/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.CampTasca;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Dao pels objectes de tipus camp de formulari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CampTascaRepository extends JpaRepository<CampTasca, Long> {
	@Query("select " +
			"	 max(ct.order) " +
			"from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId")
	public int getNextOrder(@Param("tascaId") Long tascaId) ;

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
}
