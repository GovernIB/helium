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

import es.caib.helium.persist.entity.Area;
import es.caib.helium.persist.entity.Entorn;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una àrea que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AreaRepository extends JpaRepository<Area, Long> {
	
	@Query(	"from Area a " +
			"where " +
			"    (:esNullFiltre = true or lower(a.codi) like lower('%'||:filtre||'%') "
			+ "		or lower(a.nom) like lower('%'||:filtre||'%'))"
			+ " 	or lower(a.descripcio) like lower('%'||:filtre||'%') ")
	Page<Area> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	Area findByEntornAndCodi(
			Entorn entorn,
			String codi);

	List<Area> findByEntornAndPareCodi(
			Entorn entorn,
			String pareCodi);

	List<Area> findByEntornId(Long entornId);

	@Query("from Area a where a.entorn.id = :entornId and a.id != :id")
	List<Area> findByEntornId(@Param("entornId") Long entornId, @Param("id") Long id);

	Area findByEntornIdAndId(Long entornId, Long id);
	
	Area findByCodiAndEntornId(String codi, Long entornId);
}
