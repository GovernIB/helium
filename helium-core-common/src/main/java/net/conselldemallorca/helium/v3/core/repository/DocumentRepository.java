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
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un document que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {

	List<Document> findByDefinicioProces(DefinicioProces definicioProces);
	List<Document> findByExpedientTipus(ExpedientTipus expedientTipus);
	
	Document findByDefinicioProcesAndCodi(DefinicioProces definicioProces, String codi);
	Document findByExpedientTipusAndCodi(ExpedientTipus expedientTipus, String codi);
	
	@Query(	"select d from " +
			"    Document d " +
			"where " +
			"    d.definicioProces.id=:id " +
			"order by codi asc")
	List<Document> findAmbDefinicioProces(@Param("id") Long id);

	@Query(	"select d from " +
			"    Document d " +
			"where " +
			"    d.definicioProces.id=:definicioProcesId " +
			"and d.codi=:codi")
	Document findAmbDefinicioProcesICodi(@Param("definicioProcesId") Long definicioProcesId, @Param("codi") String codi);

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
			"   d.expedientTipus.id = :expedientTipusId " +
			"	and (:esNullFiltre = true or lower(d.codi) like lower('%'||:filtre||'%') or lower(d.nom) like lower('%'||:filtre||'%')) ")
	public Page<Document> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	List<Document> findByExpedientTipusOrderByCodiAsc(ExpedientTipus expedientTipus);
}
