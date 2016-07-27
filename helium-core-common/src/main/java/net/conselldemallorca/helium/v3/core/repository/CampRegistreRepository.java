/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;

/**
 * Repositori per a les dades dels registres dels camps de variables de tipus registres.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CampRegistreRepository extends JpaRepository<CampRegistre, Long> {

}
