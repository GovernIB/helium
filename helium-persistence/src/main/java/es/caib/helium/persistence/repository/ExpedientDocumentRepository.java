/**
 *
 */
package es.caib.helium.persistence.repository;

import es.caib.helium.persistence.entity.DocumentStore;
import es.caib.helium.persistence.entity.ExpedientDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositori amb els mètodes per consultar i obtenir els documents dels expedients.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientDocumentRepository extends JpaRepository<ExpedientDocument, Long> {

	public ExpedientDocument findByDocumentStore(DocumentStore documentStore);
	public ExpedientDocument findByCodiAndExpedientId(String codi, Long  expedientId);
	/** Obté els documents d'un expedient sense procés ni tasca. */
	@Query(" FROM ExpedientDocument ed " +
			" WHERE " +
			" (:expedientIdIsNull = true OR ed.expedient.id = :expedientId) " +
			" AND (:processIdIsNull = true OR ed.processInstanceId = :processId) " +
			" AND (:taskIdIsNull = true OR ed.taskId = :taskId) " +
			" AND ed.codi = :codi ")
	public ExpedientDocument findByCodi(
		@Param("codi") String codi,
		@Param("expedientId") Long expedientId,
		@Param("expedientIdIsNull") Boolean expedientIdIsNull,
		@Param("processId") String processId,
		@Param("processIdIsNull") Boolean processIdIsNull,
		@Param("taskId") String taskId,
		@Param("taskIdIsNull") Boolean taskIdIsNull);

	public void deleteByDocumentStoreId(Long documentStoreId);

	/** Obté els documents d'un expedient per una tasca concreta. */
	@Query("SELECT ed.documentStore from ExpedientDocument ed" +
		" WHERE " +
		" 	ed.expedient.id = :expedientId AND " +
		" 	ed.taskId = :taskId")
	public List<DocumentStore> findDocumentStoreByExpedientIdAndTaskId(
		@Param("expedientId") Long expedientId,
		@Param("taskId") String  taskId);

	/** Obté els documents d'un expedient per un procés concret. */
	@Query("SELECT ed.documentStore from ExpedientDocument ed" +
		" WHERE " +
		" 	ed.expedient.id = :expedientId AND " +
		" 	ed.processInstanceId = :processId")
	public List<DocumentStore> findDocumentStoreByExpedientIdAndProcessId(
		@Param("expedientId") Long expedientId,
		@Param("processId") String processId);

	/** Obté els documents d'un expedient sense procés ni tasca. */
	@Query("SELECT ed.documentStore from ExpedientDocument ed" +
			" WHERE " +
			" ed.expedient.id = :expedientId")
	public List<DocumentStore> findDocumentStoreByExpedientId(@Param("expedientId") Long expedientId);

	/** Obté els documents d'un expedient sense procés ni tasca. */
	@Query("SELECT ed.documentStore from ExpedientDocument ed " +
		" WHERE " +
		" (:expedientIdIsNull = true OR ed.expedient.id = :expedientId) " +
		" AND (:processIdIsNull = true OR ed.processInstanceId = :processId) " +
		" AND (:taskIdIsNull = true OR ed.taskId = :taskId) " +
		" AND ed.codi = :codi ")
	public DocumentStore findDocumentStoreByCodi(
		@Param("codi") String codi,
		@Param("expedientId") Long expedientId,
		@Param("expedientIdIsNull") Boolean expedientIdIsNull,
		@Param("processId") String processId,
		@Param("processIdIsNull") Boolean processIdIsNull,
		@Param("taskId") String taskId,
		@Param("taskIdIsNull") Boolean taskIdIsNull);

	@Query("SELECT ed.documentStore from ExpedientDocument ed " +
			" WHERE " +
			" ed.expedient.id = :expedientId " +
			" AND ed.codi = :codi ")
	public DocumentStore findDocumentStoreByExpedientIdAndCodi(
		@Param("expedientId") Long expedientId,
		@Param("codi") String codi);

}
