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
import net.conselldemallorca.helium.core.model.hibernate.Tasca;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una tasca que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TascaRepository extends JpaRepository<Tasca, Long> {

	public List<Tasca> findByDefinicioProcesIdOrderByNomAsc(Long definicioProcesId);
	
	@Query(	"select t.jbpmName, t.nom from " +
			"    Tasca t " +
			"where " +
			" t.definicioProces.id in (:ids)" +
			" order by t.nom asc")
	public List<Object[]> findIdNomByDefinicioProcesIdsOrderByNomAsc(
			@Param("ids") List<Long> ids);
	
	@Query(	"select t.jbpmName, '[' || t.definicioProces.expedientTipus.nom || ' ] ' || t.nom from " +
			"    Tasca t " +
			"where " +
			" t.definicioProces.id in (:ids)" +
			" order by t.definicioProces.expedientTipus.nom asc, t.nom asc")
	public List<Object[]> findIdNomByExpedientTipusOrderByExpedientTipusNomAndNomAsc(
			@Param("ids") List<Long> ids);
	
	Tasca findByJbpmNameAndDefinicioProces(
			String jbpmName,
			DefinicioProces definicioProces);

	@Query(	"from " +
			"    Tasca t " +
			"where " +
			"    t.jbpmName = :jbpmName " +
			"and t.definicioProces.jbpmId = :jbpmId")
	Tasca findByJbpmNameAndDefinicioProcesJbpmId(
			@Param("jbpmName") String name,
			@Param("jbpmId") String jbpmId);
	
	@Query(	"from " +
			"    Tasca t " +
			"where " +
			"concat(t.jbpmName,'.',t.definicioProces.jbpmId) in (:jbpmNameDefinicioProcesJbpmIdPairs)")
	List<Tasca> findByJbpmNameAndDefinicioProcesJbpmIdPairs(@Param("jbpmNameDefinicioProcesJbpmIdPairs") List<String> jbpmNameDefinicioProcesJbpmIdPairs);

	@Query(	"from Tasca t " +
			"where " +
			"   t.definicioProces.id = :definicioProcesId " +
			"	and (:esNullFiltre = true or lower(t.jbpmName) like lower('%'||:filtre||'%') or lower(t.nom) like lower('%'||:filtre||'%')) ")
	public Page<Tasca> findByFiltrePaginat(
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	@Query(	"from Tasca t " +
			"where " +
			"   t.id = :id ")
	public Tasca findById(
			@Param("id") Long id);

}
