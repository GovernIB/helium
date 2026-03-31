/**
 * 
 */
package es.caib.helium.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.helium.persistence.entity.ExpedientDades;

/**
 * Repositori amb els mètodes per consultar i obtenir les dades dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientDadesRepository extends JpaRepository<ExpedientDades, Long> {

}
