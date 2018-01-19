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
	
	/** Consulta per expedient tipus i id. Té en compte l'herència. */
	@Query(	"from Document d " +
			"where " +
			"  	d.id = :id " +
			"  	and (d.expedientTipus.id = :expedientTipusId " +
			"			or d.expedientTipus.id = ( " + 
			"				select et.expedientTipusPare.id " + 
			"				from ExpedientTipus et " + 
			"				where et.id = :expedientTipusId)) ")
	public Document findByExpedientTipusAndIdAmbHerencia(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("id") Long id);
	
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
			"where (:herencia = false " +
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
			"			or ( :herencia = true and d.expedientTipus.id = ( " +	
			"					select et.expedientTipusPare.id " + 
			"					from ExpedientTipus et " + 
			"					where et.id = :expedientTipusId))) " +
			"	and d.codi = :codi"
			)
	Document findByExpedientTipusAndCodi(
			@Param("expedientTipusId") Long expedientTipus, 
			@Param("codi") String codi,
			@Param("herencia") boolean herencia);
	
	
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
			"	(:herencia = false " +
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
			"			or (:herencia = true " +
			"					and d.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"			or d.expedientTipus.id is null) " +
			"   and (d.definicioProces.id = :definicioProcesId or d.definicioProces.id is null) " +
			"	and (:esNullFiltre = true or lower(d.codi) like lower('%'||:filtre||'%') or lower(d.nom) like lower('%'||:filtre||'%')) ")
	public Page<Document> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			@Param("herencia") boolean herencia,
			Pageable pageable);
			
	@Query( "select ds " +
			"from Document d " +
			"	join d.expedientTipus et with et.id = :expedientTipusId, " +
			"	Document ds " +
			"where " +
			"	ds.codi = d.codi " +
			" 	and ds.expedientTipus.id = et.expedientTipusPare.id ")
	public List<Document> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);


}
