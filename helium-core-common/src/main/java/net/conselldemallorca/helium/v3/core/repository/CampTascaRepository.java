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

import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Dao pels objectes de tipus camp de formulari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CampTascaRepository extends JpaRepository<CampTasca, Long> {
	
	@Query("select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
			"and ct.order=:order")
	public CampTasca getAmbOrdre(@Param("tascaId") Long tascaId, @Param("order") int order);

	@Query("select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
			"order by " +
			"    ct.order")
	public List<CampTasca> findAmbTascaOrdenats(@Param("tascaId") Long tascaId);

	@Query("select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
			"and ct.camp.id=:campId")
	public CampTasca findAmbTascaCamp(@Param("tascaId") Long tascaId, @Param("campId") Long campId);

	@Query("select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
			"and ct.camp.codi=:campCodi")
	public CampTasca findAmbTascaCodi(@Param("tascaId") Long tascaId, @Param("campCodi") String campCodi);

	@Query(	"select count(ct)" +
			"from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id = :tascaId")
	public long countAmbTasca(
			@Param("tascaId") Long tascaId);
	
	/** Consulta els camps de la tasca.
	 * 
	 * @param tascaId Identificador de la tasca.
	 * @param expedientTipusId Si s'informa llavors es filtren els camps que estiguin relacionats amb aquest tipus d'expedient.
	 * @return
	 */
	@Query("from CampTasca ct " +
			"where " +
			"    ct.tasca.id = :tascaId " +
					// Propis
			"   and ( (ct.expedientTipus.id = :expedientTipusId or (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"       ) " +
			"order by " +
			"    ct.order")
	public List<CampTasca> findAmbTascaIdOrdenats(
			@Param("tascaId") Long tascaId,
			@Param("expedientTipusId") Long expedientTipusId);

	
	@Query(	"from CampTasca ct " +
			"where " +
			"   ct.tasca.id = :tascaId " +
					// Propis
			"   and ( (ct.expedientTipus.id = :expedientTipusId or (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"       ) " +
			"	and (:esNullFiltre = true or lower(ct.camp.codi) like lower('%'||:filtre||'%')) ")
	public Page<CampTasca> findByFiltrePaginat(
			@Param("tascaId") Long tascaId,
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);

	@Query("select ct " +
			"from CampTasca ct " +
			"where ct.tasca.id = :tascaId " +
			"order by ct.order asc ")
	List<CampTasca> findCampsTasca(
			@Param("tascaId") Long tascaId);
	
	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( ct.order), -1) + 1 " +
			"from CampTasca ct " +
			"where " +
			"    ct.tasca.id = :tascaId " )
	public Integer getNextOrdre(@Param("tascaId") Long tascaId);

	/** Troba tots els camps relacionats amb l'expedient tipus. */
	public List<CampTasca> findAllByExpedientTipus(ExpedientTipus expedientTipus);
		
}
