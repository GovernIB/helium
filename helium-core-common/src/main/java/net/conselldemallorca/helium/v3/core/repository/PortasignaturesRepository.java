/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesEstatEnum;

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
	
	Portasignatures findById(Long portasignaturesId);
	
	@Query(	"select pf from Portasignatures pf, DocumentStore docs where pf.documentStoreId = docs.id " +
			"and  "+
			" (:esNullEntorn = true or pf.expedient.entorn.id IN (:entornsId)) " +
			" and (:esNullExpTipus = true or pf.expedient.tipus.id = :expTipus) " +
			" and (:esNullExpId = true or pf.expedient.id = :expId) " +
			" and (:esNullNumExp = true or lower(pf.expedient.numero) like lower('%'||:numExp||'%') or lower(pf.expedient.titol) like lower('%'||:numExp||'%')) " +
			" and (:esNullEstat = true or pf.estat = :estat) " +
			" and (:esNullDocument = true or lower(replace(docs.jbpmVariable, 'H3l1um#document.', '')) like lower('%'||:document||'%') or lower(docs.arxiuNom) like lower('%'||:document||'%')) " +
			" and (:esNullDataIni = true or pf.dataEnviat >= :dataIni) " +
			" and (:esNullDataFi = true  or pf.dataEnviat <= :dataFi)")	
	public Page<Portasignatures> findByFiltrePaginat(
			@Param("esNullEntorn") boolean esNullEntorn,
			@Param("entornsId") List<Long> entornsId,
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
			@Param("esNullDataIni") boolean esNullDataIni,
			@Param("dataIni") Date dataIni,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			Pageable pageable);
}
