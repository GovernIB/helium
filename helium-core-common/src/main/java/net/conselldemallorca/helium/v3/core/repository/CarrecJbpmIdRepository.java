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

import net.conselldemallorca.helium.core.model.hibernate.CarrecJbpmId;

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
			"    (:esNullFiltre = true or lower(c.codi) like lower('%'||:filtre||'%') "
			+ "		or lower(c.nomHome) like lower('%'||:filtre||'%'))"
			+ "		or lower(c.nomDona) like lower('%'||:filtre||'%'))"
			+ " 	or lower(c.descripcio) like lower('%'||:filtre||'%') ")
	Page<CarrecJbpmId> findConfigurats(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	@Query("select " +
				"    distinct m.role," +
				"    m.group.name " +
				"from " +
				"    org.jbpm.identity.Membership m " +
				"where " +
				"    (:esNullFiltre = true or lower(m.role) like lower('%'||:filtre||'%') " +
				" 		or lower(m.group.name) like lower('%'||:filtre||'%'))" +
				"    and (m.role, m.group.name) not in (" +
				"        select " +
				"            c.codi," +
				"            c.grup " +
				"        from " +
				"            CarrecJbpmId c) ")
	List<Object[]> findSenseConfigurar(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);		
	
	CarrecJbpmId findById(Long id);

	CarrecJbpmId findByCodi(String codi);

	CarrecJbpmId findByCodiAndGrup(
			String codi,
			String grup);

	@Query("select " +
			"    m.user.name " +
			"from " +
			"    org.jbpm.identity.Membership m " +
			"where " +
			"    m.group.name = :grupCodi " +
			"and m.role = :carrecCodi")
	List<String> findPersonaCodiByGrupCodiAndCarrecCodi(
			@Param("grupCodi") String grupCodi,
			@Param("carrecCodi") String carrecCodi);

	@Query("select " +
			"    m.role " +
			"from " +
			"    org.jbpm.identity.Membership m " +
			"where " +
			"    m.user.name = :personaCodi " +
			"and m.group.name = :grupCodi")
	List<String> findCarrecsCodiByPersonaCodiAndGrupCodi(
			@Param("personaCodi") String personaCodi,
			@Param("grupCodi") String grupCodi);

	@Query("select " +
			"    m.user.name " +
			"from " +
			"    org.jbpm.identity.Membership m " +
			"where " +
			"    m.role = :carrecCodi")
	List<String> findPersonesCodiByCarrecCodi(
			@Param("carrecCodi") String carrecCodi);

	@Query("select distinct " +
			"    m.user.name " +
			"from " +
			"    org.jbpm.identity.Membership m " +
			"where " +
			"    m.group.name = :grupCodi")
	List<String> findPersonesCodiByGrupCodi(
			@Param("grupCodi") String grupCodi);
}
