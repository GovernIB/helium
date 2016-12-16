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

import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un camp que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

	Consulta findById(Long id);
	
	List<Consulta> findByEntorn(Entorn entorn);
	
	@Query("select c from Consulta c, Expedient e where c.id = e.numero and e.id = :expedientId")
	Consulta findByIdExpedient(@Param("expedientId") Long id);

	@Query("select c from Consulta c "
			+ "where entorn.id = :entornId "
			+ "and expedientTipus.id = :expedientTipusId "
			+ "order by ordre")
	List<Consulta> findConsultesAmbEntornIExpedientTipusOrdenat(
			@Param("entornId") Long entornId, 
			@Param("expedientTipusId") Long expedientTipusId);
	
	@Query(	"select c from Consulta c " +
			"where entorn.id = :entornId " +
			"and expedientTipus.id = :expedientTipusId " +
			"and c.ocultarActiu = false " +
			"order by ordre")
	List<Consulta> findConsultesActivesAmbEntornIExpedientTipusOrdenat(
			@Param("entornId") Long entornId, 
			@Param("expedientTipusId") Long expedientTipusId);

	@Query("select c from " +
			"    Consulta c " +
			"where " +
			"    c.expedientTipus.id = :expedientTipusId " +
			"order by " +
			"    ordre")
	List<Consulta> findAmbExpedientTipus(
			@Param("expedientTipusId") Long expedientTipusId);	
	
	Consulta findByExpedientTipusAndCodi(ExpedientTipus expedientTipus, String codi);

	@Query(	"from Consulta c " +
			"where " +
			"	c.entorn.id = :entornId " +
			"   and (:esNullExpedientTipusId = true or c.expedientTipus.id = :expedientTipusId) " +
			"	and (:esNullFiltre = true or (lower(c.codi) like lower('%'||:filtre||'%') or lower(c.nom) like lower('%'||:filtre||'%') or lower(c.expedientTipus.nom) like lower('%'||:filtre||'%'))) ")
	Page<Consulta> findByFiltrePaginat(
			@Param("entornId") Long entornId,
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( c.ordre), -1) + 1 " +
			"from Consulta c " +
			"where " +
			"    c.expedientTipus.id = :expedientTipusId " )
	Integer getNextOrdre(@Param("expedientTipusId") Long expedientTipusId);

	/** Mètode per consultar quants camps hi ha de cada tipus per al datatable de consultes. Retorna una llista
	 * amb les valors <[consultaId, tipus, count]>*/
	@Query(	"select c.consulta.id as consulta_id, " +
			"		c.tipus as tipus," +
			" 		count(c) " +
			"from ConsultaCamp c " +
			"where c.consulta.id in (:consultaIds) " +
			"group by c.consulta.id, c.tipus")
	List<Object[]> countCamps( @Param("consultaIds") List<Long> consultaIds);
	
}
