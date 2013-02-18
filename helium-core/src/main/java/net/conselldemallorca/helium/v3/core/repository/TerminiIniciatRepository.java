/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un termini que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiIniciatRepository extends JpaRepository<TerminiIniciat, Long> {

	TerminiIniciat findByTerminiAndProcessInstanceId(
			Termini termini,
			String processInstanceId);

	List<TerminiIniciat> findByTaskInstanceId(String taskInstanceId);

}
