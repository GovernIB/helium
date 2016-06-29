/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un domini que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DominiRepository extends JpaRepository<Domini, Long> {

	Domini findByExpedientTipusAndCodi(
			ExpedientTipus expedientTipus,
			String codi);

	Domini findByEntornAndCodi(
			Entorn entorn,
			String codi);

	List<Domini> findByEntorn(
			Entorn entorn);

	@Query("select e from " +
			"    Domini e " +
			"where " +
			"    e.expedientTipus.id = :expedientTipusId " +
			"order by " +
			"    nom")
	List<Domini> findAmbExpedientTipus(
			@Param("expedientTipusId") Long expedientTipusId);

}
