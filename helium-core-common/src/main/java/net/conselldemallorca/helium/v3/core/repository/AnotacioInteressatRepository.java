/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.AnotacioInteressat;

/**
 * Repository per treballar amb entitats de tipus AnotacioAnnexDto pels interessats de les anotacions provinents de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AnotacioInteressatRepository extends JpaRepository<AnotacioInteressat, Long> {

}
