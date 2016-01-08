/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.AreaJbpmId;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un areaJbpmId que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AreaJbpmIdRepository extends JpaRepository<AreaJbpmId, Long> {

	AreaJbpmId findByCodi(String codi);

}
