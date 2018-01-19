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

import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;

/**
 * Dao pels objectes de tipus CampAgrupacio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CampAgrupacioRepository extends JpaRepository<CampAgrupacio, Long> {
	
	@Query("select ca from " +
			"    CampAgrupacio ca " +
			"where " +
			"    ca.definicioProces.id=:definicioProcesId " +
			"order by " +
			"    ordre")
	List<CampAgrupacio> findAmbDefinicioProcesOrdenats(
			@Param("definicioProcesId") Long definicioProcesId);

	@Query("select ca from " +
			"    CampAgrupacio ca " +
			"where " +
			"    (ca.expedientTipus.id=:expedientTipusId " +
						// Heretats
			"			or (:herencia = true " +
			"					and ca.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) ) " +
			"order by " +
			"    ca.expedientTipus.id, ordre")
	List<CampAgrupacio> findAmbExpedientTipusOrdenats(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("herencia") boolean herencia);

	@Query("select ca from " +
			"    CampAgrupacio ca " +
			"where " +
			"    ca.definicioProces.id=:definicioProcesId " +
			"and ca.codi=:codi")
	CampAgrupacio findAmbDefinicioProcesICodi(@Param("definicioProcesId") Long definicioProcesId, @Param("codi") String codi);
	

	@Query("select " +
			"	 max(ca.ordre) " +
			"from " +
			"    CampAgrupacio ca " +
			"where " +
			"    ca.definicioProces.id=:definicioProcesId ")
	int getNextOrder(@Param("definicioProcesId") Long definicioProcesId);

	@Query("select ca from " +
			"    CampAgrupacio ca " +
			"where " +
			"    ca.definicioProces.id=:definicioProcesId " +
			"and ca.ordre=:ordre")
	CampAgrupacio getAmbOrdre(@Param("definicioProcesId") Long definicioProcesId, 
			@Param("ordre") int ordre);

	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( ca.ordre), -1) + 1 " +
			"from CampAgrupacio ca " +
			"where " +
			"   (ca.expedientTipus.id = :expedientTipusId or ca.expedientTipus.id is null) " +
			"   and (ca.definicioProces.id = :definicioProcesId or ca.definicioProces.id is null)")
	Integer getNextOrdre(@Param("expedientTipusId") Long expedientTipusId,
						 @Param("definicioProcesId") Long definicioProcesId);

	/** Per trobar codis repetits. */
	CampAgrupacio findByExpedientTipusIdAndCodi(Long expedientTipusId, String codi);	
	CampAgrupacio findByDefinicioProcesIdAndCodi(Long definicioProcesId, String codi);	
		
	@Query(	"from CampAgrupacio a " +
			"where " +
			"   (a.expedientTipus.id = :expedientTipusId or a.expedientTipus.id is null) " +
			"   and (a.definicioProces.id = :definicioProcesId or a.definicioProces.id is null) " +
			"	and (:esNullFiltre = true " +
			"			or lower(a.codi) like lower('%'||:filtre||'%') " +
			"			or lower(a.nom) like lower('%'||:filtre||'%')) ")
	Page<CampAgrupacio> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	/** Recupera la informació de tots els registres sobreescrits.*/
	@Query( "select cas " +
			"from CampAgrupacio ca " +
			"	join ca.expedientTipus et with et.id = :expedientTipusId, " +
			"	CampAgrupacio cas " +
			"where " +
			"	cas.codi = ca.codi " +
			" 	and cas.expedientTipus.id = et.expedientTipusPare.id ")
	List<CampAgrupacio> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);	
}
