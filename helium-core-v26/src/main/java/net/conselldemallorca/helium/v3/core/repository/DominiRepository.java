/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

import org.springframework.data.jpa.repository.JpaRepository;

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

}
