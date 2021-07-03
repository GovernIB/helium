/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.Portasignatures;
import es.caib.helium.persist.entity.Portasignatures.TipusEstat;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un document enviat al portasignatures que està
 * emmagatzemat a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortasignaturesRepository extends JpaRepository<Portasignatures, Long> {

	@Query(
			"from " +
			"    Portasignatures p " +
			"where " +
			"    processInstanceId = :processInstanceId")
	List<Portasignatures> findByProcessInstanceId(
			@Param("processInstanceId") String processInstanceId);

	@Query(
			"from " +
			"    Portasignatures p " +
			"where " +
			"    p.processInstanceId = :processInstanceId " +
			"and p.estat not in :estats ")
	List<Portasignatures> findByProcessInstanceIdAndEstatNotIn(
			@Param("processInstanceId") String processInstanceId,
			@Param("estats") List<TipusEstat> estats);

	@Query(
			"from " +
			"    Portasignatures p " +
			"where " +
			"    p.processInstanceId = :processInstanceId " +
			"and p.documentStoreId = :documentStoreId ")
	Portasignatures findByProcessInstanceIdAndDocumentStoreId(
			@Param("processInstanceId") String processInstanceId,
			@Param("documentStoreId") Long documentStoreId);

	Portasignatures findByDocumentId(Integer documentId);
	
	@Query("select p from Portasignatures p where processInstanceId=:processInstanceId")
	List<Portasignatures> findPendentsPerProcessInstanceId(@Param("processInstanceId") String processInstanceId);

	List<Portasignatures> findByExpedientAndEstat(
			Expedient expedient,
			TipusEstat estat);

	@Query(
			"from " +
			"    Portasignatures p " +
			"where " +
			"    p.expedient.id = :expedientId " +
			"and p.estat = :estat ")	
	List<Portasignatures> findAmbExpedientIdIEstat(
			@Param("expedientId") Long expedientId,
			@Param("estat") TipusEstat estat);
}
