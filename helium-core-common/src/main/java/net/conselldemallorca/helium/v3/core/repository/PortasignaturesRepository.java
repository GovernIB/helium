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
			"    p.processInstanceId = :processInstanceId " +
			"and p.estat not in :estats " +
			"order by p.id desc ")
	List<Portasignatures> findByProcessInstanceIdAndEstatNotIn(
			@Param("processInstanceId") String processInstanceId,
			@Param("estats") List<TipusEstat> estats);

	@Query("from " +
			"    Portasignatures p " +
			"where " +
			"    p.processInstanceId = :processInstanceId " +
			"and p.documentStoreId = :documentStoreId " +
			"order by p.id desc ")
	List<Portasignatures> findByProcessInstanceIdAndDocumentStoreId(
			@Param("processInstanceId") String processInstanceId,
			@Param("documentStoreId") Long documentStoreId);

	/** Per trobar per id del portasignatures. */
	Portasignatures findByDocumentId(Integer documentId);
		
	@Query("select p " +
			"from Portasignatures p " +
			"where processInstanceId = :processInstanceId "  +
			"order by p.id desc ")
	List<Portasignatures> findPerProcessInstanceId(@Param("processInstanceId") String processInstanceId);

	List<Portasignatures> findByExpedientAndEstat(
			Expedient expedient,
			TipusEstat estat);
	
	Portasignatures findById(Long portasignaturesId);
}
