/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.helium.persist.entity.AnotacioAnnex;

/**
 * Repository per treballar amb entitats de tipus AnotacioAnnexDto pels annexos de les anotacions provinents de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AnotacioAnnexRepository extends JpaRepository<AnotacioAnnex, Long> {

	/** Troba la llista d'annexos per un expedient. */
	List<AnotacioAnnex> findByAnotacioExpedientId(Long id);

}
