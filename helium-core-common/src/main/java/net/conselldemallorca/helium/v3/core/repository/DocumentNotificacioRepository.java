/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	
	/** Consulta la llista d'identificadors de documents notificats a l'expedient.
	 * 
	 * @param expedient Expedient on es busquen les notificacions.
	 * @return Llista de documents notificats.
	 */
	@Query ("select distinct dn.document.id " +
			"from DocumentNotificacio dn " +
			"where dn.expedient = :expedient" )
	public List<Long> getDocumentsNotificatsIdsPerExpedient(
			@Param("expedient") Expedient expedient);


}
