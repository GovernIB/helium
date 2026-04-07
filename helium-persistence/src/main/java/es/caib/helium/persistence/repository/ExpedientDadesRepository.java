/**
 * 
 */
package es.caib.helium.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientDades;

/**
 * Repositori amb els mètodes per consultar i obtenir les dades dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientDadesRepository extends JpaRepository<ExpedientDades, Long> {

	/** Obté les dades d'un expedient per una tasca concreta. */
	public ExpedientDades findByExpedientAndTaskId(Expedient expedient, String taskId);

	/** Obté les dades d'un expedient per un procés concret. */
	public ExpedientDades findByExpedientAndProcessId(Expedient expedient, String processId);

	/** Obté les dades d'un expedient sense procés ni tasca. */
	public ExpedientDades findByExpedient(Expedient expedient);

}
