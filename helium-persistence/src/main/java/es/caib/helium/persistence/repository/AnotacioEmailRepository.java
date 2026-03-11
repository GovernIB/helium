/**
 * 
 */
package es.caib.helium.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persistence.entity.AnotacioEmail;

/**
 * Repository per treballar amb entitats de tipus AnotacioEmail pels emails de les anotacions provinents de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AnotacioEmailRepository extends JpaRepository<AnotacioEmail, Long> {

	/** Troba la llista d'emails per un expedient. */
	List<AnotacioEmail> findByExpedientId(Long id);
	
	/** Troba la llista d'emails per una anotació. */
	List<AnotacioEmail> findByAnotacioId(Long id);
	
	/** Troba la llista d'emails per enviamentAgrupat
	 *  * 
	 * @param enviamentAgrupat
	 * @return
	 */
	@Query("select ae " +
			"from AnotacioEmail ae " +
			"where ae.enviamentAgrupat = :enviamentAgrupat " +
			"order by ae.destinatariCodi asc, ae.id ")		
	List<AnotacioEmail> findByEnviamentAgrupatOrderByDestinatariCodi
			(@Param("enviamentAgrupat") boolean enviamentAgrupat );
	
}
