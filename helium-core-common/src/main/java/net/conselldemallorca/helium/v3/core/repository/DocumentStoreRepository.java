/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;

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

	/** Per trobar tots els documents que fan referència a un annex per actualitzar el seu UUID. */
	public List<DocumentStore> findByAnnexId(long annexId);

	/** Per trobar tots els documents continguts en cas de que sigui un zip. */
	@Query(
			"select ds.continguts from DocumentStore ds "+
			"where ds.id = :documentStoreId ")
	public List<DocumentStore> findDocumentsContinguts(
			@Param("documentStoreId") Long documentStoreId);
	
	
	/** Per trobar tots els identificadors de documents continguts en diferents .zips. */
	@Query(
			"select ds.continguts " + 
			"from DocumentStore ds "+
			"where ds.id in (:documentsContinentsIds) ")
	public List<DocumentStore> findDocumentsContingutsIds(
			@Param("documentsContinentsIds") List<Long> documentsContinentsIds);
	
	@Query( value = "SELECT DISTINCT e.ID AS EXP, ds.ID AS DOC " +
					"FROM " +
					"HEL_DOCUMENT_STORE ds, " +
					"JBPM_PROCESSINSTANCE pi, " +
					"HEL_EXPEDIENT e, " +
					"JBPM_VARIABLEINSTANCE vi " +
					"WHERE " +
						"ds.PROCESS_INSTANCE_ID = pi.ID_ AND " +
						"pi.EXPEDIENT_ID_ = e.ID AND " +
						"e.ARXIU_ACTIU  = 1 AND " +
						"vi.NAME_ = ds.JBPM_VARIABLE AND " +
						"vi.PROCESSINSTANCE_ = pi.ID_ AND " +
						"e.ARXIU_UUID IS NOT NULL AND " +
						"ds.ARXIU_UUID IS NULL AND " +
						"(ds.SYNC_REINTENTS IS NULL OR ds.SYNC_REINTENTS < :maxReintents) ",
			nativeQuery = true)
	public List<List<Long>> findDocumentsPendentsArxiu(@Param("maxReintents") Long maxReintents);
}
