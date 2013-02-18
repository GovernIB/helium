/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una definició de procés que està emmagatzemada
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DefinicioProcesRepository extends JpaRepository<DefinicioProces, Long> {

	DefinicioProces findByJbpmId(
			String jbpmId);

	DefinicioProces findByJbpmKeyAndVersio(
			String jbpmKey,
			int versio);

	List<DefinicioProces> findByEntornAndJbpmKey(
			Entorn entorn,
			String jbpmKey);

	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.entorn = ?1 " +
			"and dp.jbpmKey = ?2 " +
			"and dp.versio = (" +
			"    select " +
			"        max(dps.versio) " +
			"    from " +
			"        DefinicioProces dps " +
			"    where " +
			"        dps.entorn=?1 " +
			"    and dps.jbpmKey=dp.jbpmKey)")
	DefinicioProces findDarreraVersioByEntornAndJbpmKey(
			Entorn entorn,
			String jbpmKey);

}
