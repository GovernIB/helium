/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.conselldemallorca.helium.core.model.hibernate.Anotacio;

/**
 * Repository per treballar amb entitats de tipus Anotacions provinents de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AnotacioRepository extends JpaRepository<Anotacio, Long> {
	
	@Query(	"from Anotacio a ")
	Page<Anotacio> findAmbFiltrePaginat(
			Pageable pageable);

}
