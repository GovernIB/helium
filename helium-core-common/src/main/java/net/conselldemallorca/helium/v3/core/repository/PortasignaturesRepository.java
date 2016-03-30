/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
			"and p.estat <> 'PENDENT' " +
			"and p.estat <> 'SIGNAT' " +
			"and p.estat <> 'REBUTJAT' " +
			"and p.estat <> 'ERROR' ")
	List<Portasignatures> findByProcessInstanceIdAndPendent(
			@Param("processInstanceId") String processInstanceId);

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
}
