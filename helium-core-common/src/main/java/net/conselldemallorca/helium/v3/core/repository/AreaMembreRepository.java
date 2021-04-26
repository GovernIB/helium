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

import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.AreaMembre;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AreaMembreRepository extends JpaRepository<AreaMembre, Long> {
	
	@Query(	"from AreaMembre am " +
			"where " +
			"    am.area.id = :entornAreaId " + 
			"    and (:esNullFiltre = true or lower(am.codi) like lower('%'||:filtre||'%') " +
			"		or lower(am.codi) like lower('%'||:filtre||'%'))")
	Page<Area> findByFiltrePaginat(
			@Param("entornAreaId") Long entornAreaId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	List<AreaMembre> findByCodi(String usuariCodi);
	
	AreaMembre findByCodiAndAreaId(String codi, Long areaId);

}
