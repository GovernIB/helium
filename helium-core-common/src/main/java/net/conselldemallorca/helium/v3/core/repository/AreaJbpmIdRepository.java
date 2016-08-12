/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.AreaJbpmId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un areaJbpmId que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AreaJbpmIdRepository extends JpaRepository<AreaJbpmId, Long> {

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
