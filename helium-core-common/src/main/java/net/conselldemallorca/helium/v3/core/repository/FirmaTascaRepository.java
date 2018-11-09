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

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;

/**
 * Dao pels objectes de tipus firma de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaTascaRepository extends JpaRepository<FirmaTasca, Long> {
	
	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( ft.order), -1) + 1 " +
			"from FirmaTasca ft " +
			"where " +
			"    ft.tasca.id = :tascaId " +
					// Propis
			"   and ( (ft.expedientTipus.id = :expedientTipusId or (ft.expedientTipus is null and ft.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (ft.expedientTipus is null and ft.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"       ) ")
	public Integer getNextOrdre(@Param("tascaId") Long tascaId, @Param("expedientTipusId") Long expedientTipusId);
	
	/** Consulta les firmes de la tasca.
	 * 
	 * @param tascaId Identificador de la tasca.
	 * @param expedientTipusId Si s'informa llavors es filtren els camps que estiguin relacionats amb aquest tipus d'expedient.
	 * @return
	 */
	@Query("from FirmaTasca ft " +
			"where ft.tasca.id=:tascaId " +
					// Propis
			"   and ( (ft.expedientTipus.id = :expedientTipusId or (ft.expedientTipus is null and ft.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (ft.expedientTipus is null and ft.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"       ) " +
			"order by ft.order")
	public List<FirmaTasca> findAmbTascaIdOrdenats(
			@Param("tascaId") Long tascaId,
			@Param("expedientTipusId") Long expedientTipusId);	

	@Query("select count(ft) from " +
			"    FirmaTasca ft " +
			"where " +
			"   ft.tasca.jbpmName=:jbpmName " +
			"	and ft.tasca.definicioProces.jbpmId=:jbpmId")
	Long countAmbTasca(
			@Param("jbpmName") String name,
			@Param("jbpmId") String jbpmId);

	@Query("select ft from " +
			"    FirmaTasca ft " +
			"where " +
			"    ft.document.id=:documentId " +
			"	and ft.tasca.id=:tascaId "  +
					// Propis
			"   and ( (ft.expedientTipus.id = :expedientTipusId or (ft.expedientTipus is null and ft.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (ft.expedientTipus is null and ft.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"       ) ")
	FirmaTasca findAmbDocumentTasca(@Param("documentId") Long documentId, @Param("tascaId") Long tascaId, @Param("expedientTipusId") Long expedientTipusId);
	
	@Query(	"from FirmaTasca ft " +
			"where " +
			"   ft.tasca.id = :tascaId " +
					// Propis
			"   and ( (ft.expedientTipus.id = :expedientTipusId or (ft.expedientTipus is null and ft.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (ft.expedientTipus is null and ft.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"       ) " +
			"	and (:esNullFiltre = true or lower(ft.document.codi) like lower('%'||:filtre||'%') or lower(ft.document.nom) like lower('%'||:filtre||'%')) ")
	public Page<FirmaTasca> findByFiltrePaginat(
			@Param("tascaId") Long tascaId,
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);

	/** Troba totes les firmes relacionades amb l'expedient tipus. */
	public List<FirmaTasca> findAllByExpedientTipus(ExpedientTipus expedientTipus);
}
