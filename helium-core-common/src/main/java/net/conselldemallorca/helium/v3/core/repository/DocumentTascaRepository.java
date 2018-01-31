/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;

/**
 * Dao pels objectes de tipus plantilla de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentTascaRepository extends JpaRepository<DocumentTasca, Long> {

	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( dt.order), -1) + 1 " +
			"from DocumentTasca dt " +
			"where " +
			"    dt.tasca.id = :tascaId " )
	public Integer getNextOrdre(@Param("tascaId") Long tascaId);

	@Query("select dt from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.id=:tascaId " +
			"and dt.order=:order")
	public DocumentTasca getAmbOrdre(
			@Param("tascaId") Long tascaId,
			@Param("order") int order);

	@Query("select dt from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.id=:tascaId " +
			"order by " +
			"    dt.order")
	public List<DocumentTasca> findAmbTascaOrdenats(
			@Param("tascaId") Long tascaId);
	
	@Query("select dt from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.document.id=:documentId " +
			"and dt.tasca.id=:tascaId")
	public DocumentTasca findAmbDocumentTasca(
			@Param("documentId") Long documentId,
			@Param("tascaId") Long tascaId);

	@Query(	"select " +
			"    dt " +
			"from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.definicioProces.id=:definicioProcesId " +
			"and dt.tasca.jbpmName=:jbpmName " +
			"order by " +
			"    dt.order")
	public List<DocumentTasca> findAmbDefinicioProcesITascaJbpmNameOrdenats(
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("jbpmName") String jbpmName);

	@Query(	"select count(dt)" +
			"from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.definicioProces.id=:definicioProcesId " +
			"and dt.tasca.jbpmName=:jbpmName")
	public Long countAmbDefinicioProcesITascaJbpmName(
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("jbpmName") String jbpmName);
	
	@Query(	"select count(dt)" +
			"from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.definicioProces.id=:definicioProcesId " +
			"and " +
			"    dt.tasca.jbpmName=:jbpmName " +
			"and " +
			"    dt.readOnly=false")
	public Long countAmbDefinicioProcesITascaJbpmNameINotReadOnly(
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("jbpmName") String jbpmName);

	@Query(	"select " +
			"    dt " +
			"from " +
			"    DocumentTasca dt " +
			"where " +
			"	 dt.document.id=:documentId " +
			"and dt.tasca.definicioProces.id=:definicioProcesId " +
			"and dt.tasca.jbpmName=:jbpmName ")
	public DocumentTasca findAmbDefinicioProcesITascaJbpmNameDocumentId(
			@Param("documentId") Long documentId,
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("jbpmName") String jbpmName);
	
	@Query(	"from DocumentTasca dt " +
			"where " +
			"   dt.tasca.id = :tascaId " +
			"	and (:esNullFiltre = true or lower(dt.document.codi) like lower('%'||:filtre||'%') or lower(dt.document.nom) like lower('%'||:filtre||'%')) ")
	public Page<DocumentTasca> findByFiltrePaginat(
			@Param("tascaId") Long tascaId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);

	@Query("select dt " +
			"from DocumentTasca dt " +
			"where dt.tasca.id = :tascaId " +
			"order by dt.order asc ")
	List<DocumentTasca> findDocumentsTasca(
			@Param("tascaId") Long tascaId);
}
