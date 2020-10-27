/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientReindexacio;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a les reindexacions dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientReindexacioRepository extends JpaRepository<ExpedientReindexacio, Long> {

}
