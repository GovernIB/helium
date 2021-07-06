/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.AreaJbpmId;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un areaJbpmId que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AreaJbpmIdRepository extends JpaRepository<AreaJbpmId, Long> {
	
	@Query(	"from AreaJbpmId a " +
			"where " +
			"    (:esNullFiltre = true or lower(a.codi) like lower('%'||:filtre||'%')) "
			+ " 	or lower(a.descripcio) like lower('%'||:filtre||'%') ")
	Page<AreaJbpmId> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	@Query("select " +
			"    g.name " +
			"from " +
			"    org.jbpm.identity.Group g " +
			"where " +
			"	 g.type = 'organisation' " +
			"    and (:esNullFiltre = true or lower(g.name) like lower('%'||:filtre||'%')) " +
			"    and g.name not in (" +
			"        select " +
			"            a.codi " +
			"        from " +
			"            AreaJbpmId a) ")
	List<String> findSenseConfigurar(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);

	Optional<AreaJbpmId> findById(Long id);
	
	AreaJbpmId findByCodi(String codi);
	
	@Query( "select " +
			"    m.group.name " +
			"from " +
			"    org.jbpm.identity.Membership m " +
			"where " +
			"    m.user.name = :usuariCodi")
	List<String> findAreesJbpmIdMembre(
			@Param("usuariCodi") String usuariCodi);
	
	@Query( "select distinct " +
			"    m.group.name " +
			"from " +
			"    org.jbpm.identity.Membership m " +
			"where " +
			"    m.user.name = :usuariCodi " +
			"	 and m.group.type = 'security-role'")
	List<String> findRolesAmbUsuariCodi(
			@Param("usuariCodi") String usuariCodi);

}
