/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.CarrecJbpmId;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un carrecJbpmId que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CarrecJbpmIdRepository extends JpaRepository<CarrecJbpmId, Long> {

	CarrecJbpmId findByCodi(String codi);

	CarrecJbpmId findByCodiAndGrup(
			String codi,
			String grup);

}
