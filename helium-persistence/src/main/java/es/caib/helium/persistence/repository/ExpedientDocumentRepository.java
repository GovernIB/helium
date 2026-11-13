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
	public ExpedientDocument findByExpedientIdAndCodi(Long  expedientId, String codi);
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

	@Query(" FROM ExpedientDocument ed " +
		" WHERE " +
		" ed.expedient.id = :expedientId " +
		" AND ed.processInstanceId = :processId ")
	public List<ExpedientDocument> findByExpedientAndProcessInstance(
		@Param("expedientId") Long expedientId,
		@Param("processId") String processId);

	@Query( "FROM ExpedientDocument ed" +
			" WHERE " +
			" ed.taskId = :taskId")
	public List<ExpedientDocument> findByTaskId(@Param("taskId") String taskId);

	@Query(" FROM ExpedientDocument ed " +
		" WHERE " +
		" ed.expedient.id = :expedientId " +
		" AND ((:processId IS NULL AND ed.processInstanceId IS NULL) OR ed.processInstanceId = :processId) " +
		" AND ((:taskId IS NULL AND ed.taskId IS NULL) OR ed.taskId = :taskId) " +
		" AND ed.codi = :codi ")
	public ExpedientDocument findByCodi(
		@Param("codi") String codi,
		@Param("expedientId") Long expedientId,
		@Param("processId") String processId,
		@Param("taskId") String taskId);

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
		" 	ed.processInstanceId = :processId AND " +
		"	ed.taskId IS NULL ")
	public List<DocumentStore> findDocumentStoreByExpedientIdAndProcessId(
		@Param("expedientId") Long expedientId,
		@Param("processId") String processId);

	/** Obté els documents d'un expedient per un procés concret. */
	@Query( "SELECT ed.documentStore from ExpedientDocument ed" +
			" WHERE " +
			" 	ed.codi = :codi AND " +
			" 	ed.processInstanceId = :processId AND " +
			"	ed.taskId IS NULL ")
	public DocumentStore findDocumentStoreByProcessIdAndCodi(
		@Param("processId") String processId,
		@Param("codi") String codi);

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

	public void deleteByExpedientId(Long expedientId);

	public void deleteByTaskIdAndDocumentStoreId(Long taskId, Long documentStoreId);

	public void deleteByProcessInstanceIdAndDocumentStoreId(String processInstanceId, Long documentStoreId);

	
	/** Consulta els diferents id d'expedients que tenen instàncies de variables que apunten a un documentStore.
	 * 
	 * @param documentStoreId Identificador del documentStore
	 * @return Retorna una llista de tots els expedients que contenen instàncies de variables de tipus document o adjunt
	 * que apunten al document store per id.
	 */
	@Query("select distinct(ed.expedient.id) " + 
			"from ExpedientDocument ed " + 
			"where ed.documentStore.id = :documentStoreId ")
	public List<Long> findExpedientsIdsByDocumentStoreId(@Param("documentStoreId") Long documentStoreId);
	
	/** Troba la llista d'identificadors d'entrades de docuemnts d'expedients pel document store
	 * que a més siguin diferents al process instance passat per paràmetre o al task id passat per peràmetre. 
	 * També hauria de filtrar les tasques finalitzades.
	 */
	@Query("select ed.id " + 
			"from ExpedientDocument ed " +
			"where ed.documentStore.id = :documentStoreId " + 
			"	and (:processInstanceId <> ed.processInstanceId) " +
			"	and (ed.taskId is null or :taskId <> ed.taskId) ")
	public List<Long> findVariableInstanceIdsByDocumentStoreId(
			@Param("documentStoreId") Long documentStoreId,
			@Param("processInstanceId") String processInstanceId,
			@Param("taskId") String taskId);

}
