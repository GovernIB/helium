/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.DocumentStore;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un documentStore que està emmagatzemat a dins la 
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentStoreRepository extends JpaRepository<DocumentStore, Long> {

	public List<DocumentStore> findByProcessInstanceId(String processInstanceId);

	public DocumentStore findByProcessInstanceIdAndArxiuNom(String processInstanceId, String arxiuNom);

	public List<DocumentStore> findByProcessInstanceId(Long processInstanceId);

	public DocumentStore findByIdAndProcessInstanceId(
			Long id,
			String processInstanceId);

	@Query(
			"from DocumentStore " +
			"where " +
			"    processInstanceId in (:processInstanceIds) " +
			"and signat = true ")
	public List<DocumentStore> findByProcessInstanceIdInAndSignatTrue(
			@Param("processInstanceIds") List<String> processInstanceIds);

}
