/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un expedient que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientRepository extends JpaRepository<Expedient, Long> {

	Expedient findByProcessInstanceId(String processInstanceId);

	Expedient findByTipusAndNumero(
			ExpedientTipus tipus,
			String numero);

}
