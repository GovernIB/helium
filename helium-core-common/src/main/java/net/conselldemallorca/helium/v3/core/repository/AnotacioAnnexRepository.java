/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.AnotacioAnnex;

/**
 * Repository per treballar amb entitats de tipus AnotacioAnnexDto pels annexos de les anotacions provinents de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AnotacioAnnexRepository extends JpaRepository<AnotacioAnnex, Long> {

	/** Troba la llista d'annexos per un expedient. */
	List<AnotacioAnnex> findByAnotacioExpedientId(Long id);
	
	/** Troba la llista d'annexos per un documentStoreId, només n'hauria de trobar un. */
	List<AnotacioAnnex> findByDocumentStoreId(Long documentStoreId);
	

}
