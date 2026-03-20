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

import es.caib.helium.persistence.entity.CarrecJbpmId;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un carrecJbpmId que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CarrecJbpmIdRepository extends JpaRepository<CarrecJbpmId, Long> {
	
	@Query(	"from CarrecJbpmId c " +
			"where " +
			"    :esNullFiltre = true or lower(c.codi) like lower('%'||:filtre||'%') "
			+ "		or lower(c.nomHome) like lower('%'||:filtre||'%')"
			+ "		or lower(c.nomDona) like lower('%'||:filtre||'%')"
			+ " 	or lower(c.descripcio) like lower('%'||:filtre||'%') ")
	Page<CarrecJbpmId> findConfigurats(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	@Query("select " +
				"    distinct m.groupId," +
				"    g.name " +
				"from " +
				"    org.flowable.idm.engine.impl.persistence.entity.MembershipEntityImpl m " +
				"    join org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl u on u.id = m.userId " +
				"    join org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl g on g.id = m.groupId " +
				"where " +
				"    (:esNullFiltre = true or lower(m.groupId) like lower('%'||:filtre||'%') " +
				" 		or lower(g.name) like lower('%'||:filtre||'%'))" +
				"    and m.role is not null " +
				"    and (m.groupId, g.name) not in (" +
				"        select " +
				"            c.codi," +
				"            c.grup " +
				"        from " +
				"            CarrecJbpmId c) ")
	List<Object[]> findSenseConfigurar(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);		
	
	Optional<CarrecJbpmId> findById(Long id);

	CarrecJbpmId findByCodi(String codi);

	CarrecJbpmId findByCodiAndGrup(
			String codi,
			String grup);

	@Query("select " +
			"    m.user.name " +
			"from " +
			"    org.flowable.idm.engine.impl.persistence.entity.MembershipEntityImpl m " +
			"    join org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl u on u.id = m.userId " +
			"    join org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl g on g.id = m.groupId " +
			"where " +
			"    g.name = :grupCodi " +
			"and m.role = :carrecCodi")
	List<String> findPersonaCodiByGrupCodiAndCarrecCodi(
			@Param("grupCodi") String grupCodi,
			@Param("carrecCodi") String carrecCodi);

	@Query("select " +
			"    g.name " +
			"from " +
			"    org.flowable.idm.engine.impl.persistence.entity.MembershipEntityImpl m " +
			"    join org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl g on g.id = m.groupId " +
			"    join org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl u on u.id = m.userId " +
			"where " +
			"   u.name = :personaCodi " +
			" and g.name = :grupCodi")
	List<String> findCarrecsCodiByPersonaCodiAndGrupCodi(
			@Param("personaCodi") String personaCodi,
			@Param("grupCodi") String grupCodi);

	@Query("select " +
			"    u.name " +
			"from " +
			"    org.flowable.idm.engine.impl.persistence.entity.MembershipEntityImpl m " +
			"    join org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl u on u.id = m.userId " +
			"where " +
			"    m.groupId = :carrecCodi")
	List<String> findPersonesCodiByCarrecCodi(
			@Param("carrecCodi") String carrecCodi);

	@Query("select distinct " +
			"    u.name " +
			"from " +
			"    org.flowable.idm.engine.impl.persistence.entity.MembershipEntityImpl m " +
			"    join org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl u on u.id = m.userId " +
			"where " +
			"    m.groupId = :grupCodi")
	List<String> findPersonesCodiByGrupCodi(
			@Param("grupCodi") String grupCodi);
}
