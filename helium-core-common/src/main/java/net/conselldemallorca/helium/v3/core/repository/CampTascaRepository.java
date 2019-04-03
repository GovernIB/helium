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

	/** Troba els camps de tasca per a un id de tasca ordenats per ordre. Els camps són heretats (expedientTipusId null) o
	 * del tipus d'expedient.
	 * 
	 * @param tascaId
	 * @param expedientTipusId
	 * @return
	 */
	@Query("select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
					// Propis
			"   and ( (ct.expedientTipus.id = :expedientTipusId or (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"       ) " +
			"order by " +
			"    ct.order "
			)
	public List<CampTasca> findAmbTascaOrdenats(@Param("tascaId") Long tascaId, @Param("expedientTipusId") Long expedientTipusId);


	/** Troba el camp tasca per codi de la variable. El camp és heretat (expedientTipusId null) o 
	 * del tipus d'expedient.
	 * 
	 * @param tascaId
	 * @param campCodi
	 * @param expedientTipusId
	 * @return
	 */
	@Query("select ct from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id=:tascaId " +
			"	and ct.camp.codi=:campCodi " +
				// Propis
			"   and ( (ct.expedientTipus.id = :expedientTipusId or (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"       ) ")
	public CampTasca findAmbTascaCodi(@Param("tascaId") Long tascaId, @Param("campCodi") String campCodi, @Param("expedientTipusId") Long expedientTipusId);

	/** Compta els camps de la tasca. Els camps són heretats (expedientTipusId null) o
	 * del tipus d'expedient.
	 * 
	 * @param tascaId
	 * @param expedientTipusId
	 * @return
	 */
	@Query(	"select count(ct)" +
			"from " +
			"    CampTasca ct " +
			"where " +
			"    ct.tasca.id = :tascaId " +
					// Propis
			"   and ( (ct.expedientTipus.id = :expedientTipusId or (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " + 
			"       ) ")
	public long countAmbTasca(@Param("tascaId") Long tascaId, @Param("expedientTipusId") Long expedientTipusId);
	
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

	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( ct.order), -1) + 1 " +
			"from CampTasca ct " +
			"where " +
			"    ct.tasca.id = :tascaId "  +
					// Propis
			"   and ( (ct.expedientTipus.id = :expedientTipusId or (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = :expedientTipusId)  ) " +
			"			or " +
					// Heretats
			" 		  (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " + 
			"       ) ")
	public Integer getNextOrdre(@Param("tascaId") Long tascaId, @Param("expedientTipusId") Long expedientTipusId);

	/** Troba tots els camps relacionats amb l'expedient tipus. */
	@Query(	"from CampTasca ct " +
			"where " +
					// Propis
			"   	( (ct.expedientTipus = :expedientTipus or (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus = :expedientTipus)  ) " +
			"			or " +
					// Heretats
			" 		  (ct.expedientTipus is null and ct.tasca.definicioProces.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp = :expedientTipus)) " +
			"       ) ")
	public List<CampTasca> findAllByExpedientTipus(@Param("expedientTipus") ExpedientTipus expedientTipus);
		
}
