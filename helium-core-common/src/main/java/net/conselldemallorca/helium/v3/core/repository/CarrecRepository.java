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
import net.conselldemallorca.helium.core.model.hibernate.Carrec;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un càrrec que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CarrecRepository extends JpaRepository<Carrec, Long> {
	
	@Query(	"from Carrec c " +
			"where " +
			"    (:esNullFiltre = true or lower(c.codi) like lower('%'||:filtre||'%') or lower(c.descripcio) like lower('%'||:filtre||'%'))")
	Page<Carrec> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	Carrec findByEntornAndCodi(
			Entorn entorn,
			String codi);

	Carrec findByEntornAndAreaAndCodi(
			Entorn entorn,
			Area area,
			String codi);
	Carrec findByEntornAndAreaCodiAndCodi(
			Entorn entorn,
			String areaCodi,
			String codi);

	List<Carrec> findByEntornAndPersonaCodi(
			Entorn entorn,
			String personaCodi);
	
	List<Carrec> findByEntornId(Long entornId);

	Carrec findByPersonaCodi(String personaCodi);

	Carrec findByEntornIdAndId(Long entornId, Long id);
	
	Carrec findByEntornIdAndCodi(Long entornId, String codi);
	
	List<Carrec> findByEntornIdAndAreaId(Long entornId, Long areaId);
}
