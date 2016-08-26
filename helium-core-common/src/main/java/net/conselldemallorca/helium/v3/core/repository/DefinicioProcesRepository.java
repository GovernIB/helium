/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una definició de procés que està emmagatzemada
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DefinicioProcesRepository extends JpaRepository<DefinicioProces, Long> {

	DefinicioProces findById(
			Long Id);

	DefinicioProces findByJbpmId(
			String jbpmId);

	DefinicioProces findByJbpmKeyAndVersio(
			String jbpmKey,
			int versio);

	List<DefinicioProces> findByEntornAndJbpmKeyOrderByVersioDesc(
			Entorn entorn,
			String jbpmKey);

	List<DefinicioProces> findByEntornIdAndJbpmKeyOrderByVersioDesc(
			Long entornId,
			String jbpmKey);

	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.entorn = ?1 " +
			"and dp.jbpmKey = ?2 " +
			"and dp.versio = (" +
			"    select " +
			"        max(dps.versio) " +
			"    from " +
			"        DefinicioProces dps " +
			"    where " +
			"        dps.entorn=?1 " +
			"    and dps.jbpmKey=dp.jbpmKey)")
	DefinicioProces findDarreraVersioByEntornAndJbpmKey(
			Entorn entorn,
			String jbpmKey);

	@Query(	"select dp.id  from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.entorn.id=:entornId " +
			"and dp.jbpmKey = :jbpmKey " +
			"and dp.versio = (" +
			"    select " +
			"        max(dps.versio) " +
			"    from " +
			"        DefinicioProces dps " +
			"    where " +
			"        dps.entorn.id=:entornId " +
			"    and dps.jbpmKey=dp.jbpmKey)")
	List<Long> findIdsDarreraVersioAmbEntornIJbpmKey(@Param("entornId") Long entornId, @Param("jbpmKey") String jbpmKey);

	@Query(	"select dp.id  from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.entorn.id=:entornId " +
			"and dp.versio = (" +
			"    select " +
			"        max(dps.versio) " +
			"    from " +
			"        DefinicioProces dps " +
			"    where " +
			"        dps.entorn.id=:entornId " +
			"    and dps.jbpmKey=dp.jbpmKey)")
	List<Long> findIdsDarreraVersioAmbEntornId(@Param("entornId") Long entornId);

	@Query(	"from " +
				"    DefinicioProces dp " +
				"where " +
				"    dp.entorn.id=:entornId " +
				"and dp.versio = (" +
				"    select " +
				"        max(dps.versio) " +
				"    from " +
				"        DefinicioProces dps " +
				"    where " +
				"        dps.entorn.id=:entornId " +
				"    and dps.jbpmKey=dp.jbpmKey " +
				"    and dps.jbpmKey = :jbpmProcessDefinitionKey) " +
				"order by dp.versio DESC")
	DefinicioProces findDarreraVersioAmbEntornIJbpmKey(
			@Param("entornId") Long entornId,
			@Param("jbpmProcessDefinitionKey") String jbpmProcessDefinitionKey);
	
	@Query(	"select dp.id from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.entorn.id=:entornId " +
			"and dp.expedientTipus.id=:expedientTipusId " +
			"and dp.versio = (" +
			"    select " +
			"        max(dps.versio) " +
			"    from " +
			"        DefinicioProces dps " +
			"    where " +
			"        dps.entorn.id=:entornId " +
			"    and dps.jbpmKey=dp.jbpmKey)")
	List<Long> findIdsDarreraVersioAmbEntornIdIExpedientTipusId(
			@Param("entornId") Long entornId,
			@Param("expedientTipusId") Long expedientTipusId);

	
	@Query(	"from DefinicioProces dp " +
			"where " +
			"   dp.entorn.id = :entornId " +
			"	and (dp.expedientTipus.id = :expedientTipusId or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"	and (:esNullFiltre = true or lower(dp.jbpmKey) like lower('%'||:filtre||'%')) " +
			"	and dp.versio = (" +
			"  		select max(dps.versio) " +
			"    	from DefinicioProces dps " +
			"    	where " +
			"       	dps.entorn.id = :entornId " +
			"			and (dps.expedientTipus.id = :expedientTipusId or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"		    and dps.jbpmKey= dp.jbpmKey) ")
	Page<DefinicioProces> findByFiltrePaginat(
			@Param("entornId") Long entornId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("incloureGlobals") boolean incloureGlobals,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	@Query("from DefinicioProces dp " +
			"where " +
			"		dp.expedientTipus.id = :expedientTipusId " +
			"  	and dp.jbpmId in (:jbpmIds) ")
	Page<DefinicioProces> findAmbExpedientTipusIJbpmIds(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("jbpmIds") Collection<String> jbpmIds,
			Pageable pageable);
	
	@Query("select dp.id from DefinicioProces dp " +
			"where " +
			"		dp.expedientTipus.id = :expedientTipusId " +
			"  	and dp.jbpmId in (:jbpmIds) ")
	List<Long> findIdsAmbExpedientTipusIJbpmIds(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("jbpmIds") Collection<String> jbpmIds);
}
