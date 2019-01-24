/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.DocumentNotificacio;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;

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
