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

	DefinicioProces findByIdAndEntornId(
			Long id,
			Long entornId);
	
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
			"	and (:esNullExpedientTipusId = true " +
			"			or (dp.expedientTipus.id = :expedientTipusId) " + 
						// Heretats
			"			or (:herencia = true " +
			"					and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"			or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"	and (:esNullFiltre = true or lower(dp.jbpmKey) like lower('%'||:filtre||'%')) " +
			"	and dp.versio = (" +
			"  		select max(dps.versio) " +
			"    	from DefinicioProces dps " +
			"    	where " +
			"       	dps.entorn.id = :entornId " +
			"			and (:esNullExpedientTipusId = true " +
			"					or (dp.expedientTipus.id = :expedientTipusId) " +
			"					or (:herencia = true " +
			"							and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"					or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"		    and dps.jbpmKey= dp.jbpmKey) ")
	Page<DefinicioProces> findByFiltrePaginat(
			@Param("entornId") Long entornId,
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("incloureGlobals") boolean incloureGlobals,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			@Param("herencia") boolean herencia,
			Pageable pageable);

	@Query(	"select dp.jbpmKey " + 
			"from DefinicioProces dp " +
			"where " +
			"   dp.entorn.id = :entornId " +
			"	and (dp.expedientTipus.id = :expedientTipusId " + 
						// Heretats
			"			or (:herencia = true " +
			"					and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
						// Globals
			"			or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"	and dp.versio = (" +
			"  		select max(dps.versio) " +
			"    	from DefinicioProces dps " +
			"    	where " +
			"       	dps.entorn.id = :entornId " +
			"			and (dps.expedientTipus.id = :expedientTipusId " + 
						// Heretats
			"			or (:herencia = true " +
			"					and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
						// Globals
			"				or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"		    and dps.jbpmKey= dp.jbpmKey) ")
	List<String> findJbpmKeys(
			@Param("entornId") Long entornId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("herencia") boolean herencia,
			@Param("incloureGlobals") boolean incloureGlobals);

	/** Mètode per consultar quantes versions hi ha per definició de procés en un entorn.
	 * Aquesta consulta s'utilitza en el datatable de definicions de procés.
	 * Retorna una llista amb els valors <[jbpmKey, count]>*/
	@Query(	"select d.jbpmKey as jbpmKey, " +
			"		count(d) as nversions " +
			"from DefinicioProces d " +
			"where d.entorn.id = :entornId " + 
			"		and d.jbpmKey in (:consultaJbpmKeys) " +
			"group by d.jbpmKey")
	List<Object[]> countVersions(
			@Param("entornId") Long entornId,
			@Param("consultaJbpmKeys") List<String> consultaJbpmKeys);	
	
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

	@Query("from DefinicioProces dp " +
			"where dp.expedientTipus.id = :expedientTipusId " +
			"order by dp.etiqueta")
	List<DefinicioProces> findAmbExpedientTipus(
			@Param("expedientTipusId") Long expedientTipusId);	
	
	@Query(	"from DefinicioProces dp " +
			"where " +
			"   dp.entorn.id=:entornId " +
			"	and ((:isNullExpedientTipusId = true) or ( dp.expedientTipus.id = :expedientTipusId) or (:incloureGlobals = true and dp.expedientTipus is null)) " + 
			"	and dp.versio = (" +
			"    select " +
			"        max(dps.versio) " +
			"    from " +
			"        DefinicioProces dps " +
			"    where " +
			"        dps.entorn.id=:entornId " +
			"    	and dps.jbpmKey=dp.jbpmKey) ")
	List<DefinicioProces> findByAll(
			@Param("entornId") Long entornId,
			@Param("isNullExpedientTipusId") boolean isNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("incloureGlobals") boolean incloureGlobals);
}
