/**
 *
 */
package es.caib.helium.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persistence.entity.AreaBpmId;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un areaBpmId que està emmagatzemat a dins la base
 * de dades.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AreaBpmIdRepository extends JpaRepository<AreaBpmId, Long> {

	@Query(	"from AreaBpmId a " +
			"where " +
			"    (:esNullFiltre = true or lower(a.codi) like lower('%'||:filtre||'%')) "
			+ " 	or lower(a.descripcio) like lower('%'||:filtre||'%') ")
	Page<AreaBpmId> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);

	@Query("select " +
			"    g.name " +
			"from " +
			"    org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl g " +
			"where " +
			"	 g.type = 'organisation' " +
			"    and (:esNullFiltre = true or lower(g.name) like lower('%'||:filtre||'%')) " +
			"    and g.name not in (" +
			"        select " +
			"            a.codi " +
			"        from " +
			"            AreaBpmId a) ")
	List<String> findSenseConfigurar(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);

	Optional<AreaBpmId> findById(Long id);

	AreaBpmId findByCodi(String codi);

	@Query( "select " +
			"    g.name " +
			"from " +
			"    org.flowable.idm.engine.impl.persistence.entity.MembershipEntityImpl m " +
			"    join org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl g on g.id = m.groupId " +
			"where " +
			"    m.userId = :usuariCodi")
	List<String> findAreesBpmIdMembre(
			@Param("usuariCodi") String usuariCodi);

	@Query( "select distinct " +
			"    m.name " +
			"from " +
			"    org.flowable.idm.engine.impl.persistence.entity.MembershipEntityImpl m " +
			"    join org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl g on g.id = m.groupId " +
			"where " +
			"    m.user.name = :usuariCodi " +
			"	 and g.type = 'security-role'")
	List<String> findRolesAmbUsuariCodi(
			@Param("usuariCodi") String usuariCodi);

}
