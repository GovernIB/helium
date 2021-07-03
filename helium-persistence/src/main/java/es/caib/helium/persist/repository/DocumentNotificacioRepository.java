/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.helium.persist.entity.DocumentNotificacio;
import es.caib.helium.persist.entity.Expedient;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una notificació electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentNotificacioRepository extends JpaRepository<DocumentNotificacio, Long> {

	DocumentNotificacio findByEnviamentIdentificadorAndEnviamentReferencia(
			String identificador,
			String referenciaEnviament);
	
	List<DocumentNotificacio> findByExpedientOrderByEnviatDataDesc(Expedient expedient);
	List<DocumentNotificacio> findByExpedientAndDocumentId(Expedient expedient, Long documentStoreId); 

}
