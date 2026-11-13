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

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un document que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {

	@Query(	"select d from " +
			"    Document d " +
			"where " +
			"    d.definicioProces.id = :definicioProcesId " +
			"order by codi asc")
	List<Document> findByDefinicioProcesId(@Param("definicioProcesId") Long definicioProcesId);

	@Query(	"select d from " +
			"    Document d " +
			"where " +
			"    d.expedientTipus.id = :expedientTipusId " +
			"order by codi asc")
	List<Document> findByExpedientTipusId(@Param("expedientTipusId") Long expedientTipusId);

	
	@Query(	"select d from " +
			"    Document d " +
			"where " +
			"	(d.id not in ( " + 
						// Llistat de sobreescrits
			"			select ds.id " +
			"			from Document da " +
			"				join da.expedientTipus et with et.id = :expedientTipusId, " +
			"				Document ds " +
			"			where " +
			"				ds.codi = da.codi " +
			"			 	and ds.expedientTipus.id = et.expedientTipusPare.id " +
			"		) " +
			"	) " +
			"   and (d.expedientTipus.id = :expedientTipusId " +
						// Heretats
			"			or (d.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) )) " +
			"order by codi asc")
	List<Document> findByExpedientTipusAmbHerencia(@Param("expedientTipusId") Long expedientTipus);
	
	/** Consulta per expedient tipus i el codi. Té en compte l'herència. */
	@Query(	"from Document d " +
			"where " +
			"  	d.id = :id " +
			"  	and d.definicioProces.id = :definicioProcesId ")
	public Document findByDefinicioProces(
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("id") Long id);
	
	Document findByDefinicioProcesAndCodi(DefinicioProces definicioProces, String codi);
	
	@Query (
			"from Document d " + 
			"where (:ambHerencia = false " +
			"		or d.id not in ( " + 
						// Llistat de sobreescrits
			"			select ds.id " +
			"			from Document da " +
			"				join da.expedientTipus et with et.id = :expedientTipusId, " +
			"				Document ds " +
			"			where " +
			"				da.codi = :codi " +
			"				and ds.codi = da.codi " +
			"			 	and ds.expedientTipus.id = et.expedientTipusPare.id " +
			"		) " +
			"	) " +
			"	and	(d.expedientTipus.id = :expedientTipusId " +
						// Heretats
			"			or ( :ambHerencia = true and d.expedientTipus.id = ( " +	
			"					select et.expedientTipusPare.id " + 
			"					from ExpedientTipus et " + 
			"					where et.id = :expedientTipusId))) " +
			"	and d.codi = :codi"
			)
	Document findByExpedientTipusAndCodi(
			@Param("expedientTipusId") Long expedientTipus, 
			@Param("codi") String codi,
			@Param("ambHerencia") boolean ambHerencia);
	
	
	@Query(	"select " +
			"    dt.document, " +
			"    dt.required, " +
			"    dt.readOnly, " +
			"    dt.order " +
			"from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.definicioProces.id=:definicioProcesId " +
			"and dt.tasca.jbpmName=:jbpmName " +
			"order by " +
			"    dt.order")
	public List<Object[]> findAmbDefinicioProcesITascaJbpmNameOrdenats(
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("jbpmName") String jbpmName);
	
	
	@Query(	"from Document d " +
			"where " +
			"	(:ambHerencia = false " +
			"		or d.id not in ( " + 
						// Llistat de sobreescrits
			"			select ds.id " +
			"			from Document da " +
			"				join da.expedientTipus et with et.id = :expedientTipusId, " +
			"				Document ds " +
			"			where " +
			"				ds.codi = da.codi " +
			"			 	and ds.expedientTipus.id = et.expedientTipusPare.id " +
			"		) " +
			"	) " +
			"   and (d.expedientTipus.id = :expedientTipusId " +
						// Heretats
			"			or (:ambHerencia = true " +
			"					and d.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"			or d.expedientTipus.id is null) " +
			"   and (d.definicioProces.id = :definicioProcesId or d.definicioProces.id is null) " +
			"	and (:esNullFiltre = true or lower(d.codi) like lower('%'||:filtre||'%') or lower(d.nom) like lower('%'||:filtre||'%')) ")
	public Page<Document> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			@Param("ambHerencia") boolean ambHerencia,
			Pageable pageable);
			
	@Query( "select d " +
			"from Document d " +
			"	join d.expedientTipus et with et.id = :expedientTipusId, " +
			"	Document ds " +
			"where " +
			"	ds.codi = d.codi " +
			" 	and ds.expedientTipus.id = et.expedientTipusPare.id ")
	public List<Document> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);
	
	
	/** Consulta els diferents id d'expedients que tenen instàncies de variables que apunten a un documentStore.
	 * 
	 * @param documentStoreId Identificador del documentStore
	 * @return Retorna una llista de tots els expedients que contenen instàncies de variables de tipus document o adjunt
	 * que apunten al document store per id.
	 */
	@Query(value = "SELECT DISTINCT(pi.EXPEDIENT_ID_) " + 
			"	FROM JBPM_VARIABLEINSTANCE vi " + 
			"	JOIN JBPM_PROCESSINSTANCE pi ON pi.ID_ = vi.PROCESSINSTANCE_ " +
			"WHERE vi.LONGVALUE_ = :documentStoreId " +
			"	AND (vi.NAME_ LIKE 'H3l1um#document.%' OR NAME_ LIKE 'H3l1um#adjunt.%') ", nativeQuery = true)
	public List<Long> findExpedientsIdsByDocumentStoreId(@Param("documentStoreId") Long documentStoreId);
	
	/** Troba la llista d'identificaadors d'instàncies de variables per documents i adjunts per un document store
	 * que a més siguin diferents al processInstanceId passat per paràmetre o al taskInstanceId passat per peràmetre. 
	 * També filtra les tasques finalitzades.
	 * 
	 * @param documentStoreId
	 * @param esNullProcessInstanceId
	 * @param processInstanceId
	 * @param esNullTaskInstanceId
	 * @param taskInstanceId
	 * @return
	 */
	@Query(value = "SELECT vi.ID_ " +
			"FROM JBPM_VARIABLEINSTANCE vi " +
			"	JOIN JBPM_PROCESSINSTANCE pi ON pi.ID_ = vi.PROCESSINSTANCE_ " +
			"	LEFT JOIN JBPM_TASKINSTANCE ti ON ti.ID_ = vi.TASKINSTANCE_ " + 
			"WHERE vi.LONGVALUE_ = :documentStoreId " + 
			"	AND (vi.NAME_ LIKE 'H3l1um#document.%' OR vi.NAME_ LIKE 'H3l1um#adjunt.%') " +
			"	AND (:processInstanceId <> vi.PROCESSINSTANCE_) " +
			"	AND (vi.TASKINSTANCE_ IS NULL OR :taskInstanceId <> vi.TASKINSTANCE_ ) " +
			"	AND (vi.TASKINSTANCE_ IS NULL OR ti.END_ IS NULL)", nativeQuery = true)
	public List<Long> findVariableInstanceIdsByDocumentStoreId(
			@Param("documentStoreId") Long documentStoreId,
			@Param("processInstanceId") Long processInstanceId,
			@Param("taskInstanceId") Long taskInstanceId);

}
