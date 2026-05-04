/**
 *
 */
package es.caib.helium.persistence.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.commons.dto.PortafirmesEstatEnum;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.Portasignatures;

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
			@Param("estats") List<PortafirmesEstatEnum> estats);

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
			PortafirmesEstatEnum estat);

	Optional<Portasignatures> findById(Long portasignaturesId);

	@Query(	"select pf from Portasignatures pf, DocumentStore docs where pf.documentStoreId = docs.id " +
			"and  "+
			" (:esNullEntornId = true or pf.expedient.entorn.id = :entornId) " +
			" and (:esNullTipusPermesos = true or pf.expedient.tipus.id IN (:tipusPermesosIds)) " +
			" and (:esNullExpTipus = true or pf.expedient.tipus.id = :expTipus) " +
			" and (:esNullExpId = true or pf.expedient.id = :expId) " +
			" and (:esNullNumExp = true or lower(pf.expedient.numero) like lower('%'||:numExp||'%') or lower(pf.expedient.titol) like lower('%'||:numExp||'%')) " +
			" and (:esNullEstat = true or pf.estat = :estat) " +
			" and (:esNullTransicio = true or pf.transition = :transicio) " +
			" and (:esNullDocument = true or lower(docs.codi) like lower('%'||:document||'%') or lower(docs.arxiuNom) like lower('%'||:document||'%')) " +
			" and (:esNullDataIni = true or pf.dataEnviat >= :dataIni) " +
			" and (:esNullDataFi = true  or pf.dataEnviat <= :dataFi)" +
			" and (:esNullDocumentId = true or pf.documentId = :documentId) ")
	public Page<Portasignatures> findByFiltrePaginat(
			@Param("esNullEntornId") boolean esNullEntornId,
			@Param("entornId") Long entornId,
			@Param("esNullTipusPermesos") boolean esNullTipusPermesos,
			@Param("tipusPermesosIds") List<Long> tipusPermesosIds,
			@Param("esNullExpTipus") boolean esNullExpTipus,
			@Param("expTipus") Long expTipus,
			@Param("esNullExpId") boolean esNullExpId,
			@Param("expId") Long expId,
			@Param("esNullNumExp") boolean esNullNumExp,
			@Param("numExp") String numExp,
			@Param("esNullDocument") boolean esNullDocument,
			@Param("document") String document,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") PortafirmesEstatEnum estat,
			@Param("esNullTransicio") boolean esNullTransicio,
			@Param("transicio") Portasignatures.Transicio transicio,
			@Param("esNullDataIni") boolean esNullDataIni,
			@Param("dataIni") Date dataIni,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullDocumentId") boolean esNullDocumentId,
			@Param("documentId") Integer documentId,
			Pageable pageable);

}
