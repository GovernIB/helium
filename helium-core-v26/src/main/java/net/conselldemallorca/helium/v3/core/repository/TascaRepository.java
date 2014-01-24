/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una tasca que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TascaRepository extends JpaRepository<Tasca, Long> {

	Tasca findByJbpmNameAndDefinicioProces(
			String jbpmName,
			DefinicioProces definicioProces);

	@Query(	"select t from " +
						"    Tasca t " +
						"where " +
						"    t.jbpmName=:jbpmName " +
						"and t.definicioProces.jbpmId=:jbpmId")
	Tasca findAmbActivityNameIProcessDefinitionId(
			@Param("jbpmName") String name,
			@Param("jbpmId") String jbpmId);	
	
	@Query(	"select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
			"order by ct.order")
	public List<CampTasca> findAmbTascaOrdenats(
			@Param("tascaId") Long tascaId);
}
