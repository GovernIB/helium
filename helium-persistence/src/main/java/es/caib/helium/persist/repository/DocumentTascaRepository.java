/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.DocumentTasca;
import es.caib.helium.persist.entity.ExpedientTipus;

/**
 * Dao pels objectes de tipus plantilla de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentTascaRepository extends JpaRepository<DocumentTasca, Long> {

	/** Obté el següent odre per a un document d'una tasca.
	 * 
	 * @param tascaId
	 * @param expedientTipusId
	 * @return
	 */
	@Query(	"select coalesce( max( dt.order), -1) + 1 " +
			"from DocumentTasca dt " +
			"where " +
			"    dt.tasca.id = :tascaId " +
					// Propis
			"   and ( (dt.expedientTipus.id = :expedientTipusId or (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"			or " +
					// Definició de procés global
			" 		  (dt.tasca.definicioProces.expedientTipus is null) " +
			"       ) ")
	public Integer getNextOrdre(@Param("tascaId") Long tascaId, @Param("expedientTipusId") Long expedientTipusId);

	/** Consulta els documents de la tasca.
	 * 
	 * @param tascaId Identificador de la tasca.
	 * @param expedientTipusId Si s'informa llavors es filtren els camps que estiguin relacionats amb aquest tipus d'expedient.
	 * @return
	 */
	@Query("select dt from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.id=:tascaId " +
					// Propis
			"   and ( (dt.expedientTipus.id = :expedientTipusId or (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"			or " +
					// Definició de procés global
			" 		  (dt.tasca.definicioProces.expedientTipus is null) " +
			"       ) " +
			"order by " +
			"    dt.order")
	public List<DocumentTasca> findAmbTascaOrdenats(
			@Param("tascaId") Long tascaId,
			@Param("expedientTipusId") Long expedientTipusId);
	

	@Query(	"select count(dt)" +
			"from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.definicioProces.id=:definicioProcesId " +
			"	and dt.tasca.jbpmName=:jbpmName "  +
					// Propis
			"   and ( (dt.expedientTipus.id = :expedientTipusId or (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"			or " +
					// Definició de procés global
			" 		  (dt.tasca.definicioProces.expedientTipus is null) " +
			"       ) ")
	public Long countAmbDefinicioProcesITascaJbpmName(
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("jbpmName") String jbpmName,
			@Param("expedientTipusId") Long expedientTipusId);
	
	@Query(	"select count(dt)" +
			"from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.definicioProces.id=:definicioProcesId " +
			"	and dt.tasca.jbpmName=:jbpmName " +
			"	and dt.readOnly=false " +
					// Propis
			"   and ( (dt.expedientTipus.id = :expedientTipusId or (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"			or " +
					// Definició de procés global
			" 		  (dt.tasca.definicioProces.expedientTipus is null) " +
			"       ) " )
	public Long countAmbDefinicioProcesITascaJbpmNameINotReadOnly(
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("jbpmName") String jbpmName,
			@Param("expedientTipusId") Long expedientTipusId);
	
	@Query("select dt from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.id=:tascaId " +
			"	and dt.document.codi=:documentCodi " +
					// Propis
			"   and ( (dt.expedientTipus.id = :expedientTipusId or (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"			or " +
					// Definició de procés global
			" 		  (dt.tasca.definicioProces.expedientTipus is null) " +
			"       ) ")
	public DocumentTasca findAmbTascaCodi(
			@Param("tascaId") Long tascaId, 
			@Param("documentCodi") String documentCodi,
			@Param("expedientTipusId") Long expedientTipusId);
	
	
	/** Consulta els documents de la tasca.
	 * 
	 * @param tascaId Identificador de la tasca.
	 * @param expedientTipusId Si s'informa llavors es filtren els camps que estiguin relacionats amb aquest tipus d'expedient.
	 * @return
	 */
	@Query("from DocumentTasca dt " +
			"where " +
			"    dt.tasca.id = :tascaId " +
					// Propis
			"   and ( (dt.expedientTipus.id = :expedientTipusId or (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (dt.expedientTipus is null and dt.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"			or " +
					// Definició de procés global
			" 		  (dt.tasca.definicioProces.expedientTipus is null) " +
			"       ) " +
			"order by " +
			"    dt.order")
	public List<DocumentTasca> findAmbTascaIdOrdenats(
			@Param("tascaId") Long tascaId,
			@Param("expedientTipusId") Long expedientTipusId);
	
	@Query(	"from DocumentTasca dt " +
			"where " +
			"   dt.tasca.id = :tascaId " +
						// Propis
			"   and (dt.expedientTipus is null or dt.expedientTipus.id = :expedientTipusId " +
						// Heretats
			" 			or dt.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) " +
						// Definició de procés global
			"			or (dt.tasca.definicioProces.expedientTipus is null)) " +
			"	and (:esNullFiltre = true or lower(dt.document.codi) like lower('%'||:filtre||'%') or lower(dt.document.nom) like lower('%'||:filtre||'%')) ")
	public Page<DocumentTasca> findByFiltrePaginat(
			@Param("tascaId") Long tascaId,
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);

	/** Troba tots els documents relacionats amb l'expedient tipus. */
	public List<DocumentTasca> findAllByExpedientTipus(ExpedientTipus expedientTipus);
}
