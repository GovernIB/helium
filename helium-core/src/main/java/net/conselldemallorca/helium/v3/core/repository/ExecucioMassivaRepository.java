/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Dao pels objectes del tipus ExecucioMassiva.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaRepository extends JpaRepository<ExecucioMassiva, Long> {

	List<ExecucioMassiva> findByUsuariAndEntorn(String usuari, Long entorn, Pageable pageable);
}
