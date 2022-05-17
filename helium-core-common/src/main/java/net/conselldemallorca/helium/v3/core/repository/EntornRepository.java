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

import net.conselldemallorca.helium.core.model.hibernate.Entorn;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un entorn que està emmagatzemat a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntornRepository extends JpaRepository<Entorn, Long> {

	@Query(	"from Entorn e " +
			"order by e.nom asc ")
	List<Entorn> findByActiuTrue();

	Entorn findByCodi(String codi);

	@Query(	"from Entorn e " +
			"where " +
			"    (:esNullFiltre = true or lower(e.nom) like lower('%'||:filtre||'%') or lower(e.codi) like lower('%'||:filtre||'%'))")
	
	Page<Entorn> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

}
