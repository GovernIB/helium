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

	//TODO: eliminar aquest mètode que només serveix per evitar un error a l'hora de trobar el subprocés
	DefinicioProces findByJbpmKeyAndVersio(
			String jbpmKey,
			int versio);
	
	//TODO: comprovar que filtra bé per tipus d'expedient o bé s'ha de posar isNull
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where ((:expedientTipusId is null and dp.expedientTipus is null) or dp.expedientTipus.id = :expedientTipusId) " +
			"	and dp.jbpmKey = :jbpmKey " +
			"	and dp.versio = :versio ")
	DefinicioProces findByJbpmKeyAndVersio(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("jbpmKey") String jbpmKey,
			@Param("versio") int versio);

	//TODO : adaptar
	List<DefinicioProces> findByEntornIdAndJbpmKeyOrderByVersioDesc(
			Long entornId,
			String jbpmKey);

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

	
	/** Consulta de les definicions de procés per entorn o tipus d'expedient que pot retornar o no les definicions
	 * de procés globals.
	 * 
	 * @param entornId
	 * @param esNullExpedientTipusId
	 * @param expedientTipusId
	 * @param incloureGlobals
	 * @param esNullFiltre
	 * @param filtre
	 * @param herencia
	 * @param pageable
	 * @return
	 */
	@Query(	"from DefinicioProces dp " +
			"where " +
			"   dp.entorn.id = :entornId " +
			"	and (:esNullExpedientTipusId = true " +
			"			or (dp.expedientTipus.id = :expedientTipusId) " + 
						// Heretats
			"			or (:herencia = true " +
			"					and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"			or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"	and (:herencia = false " +
			"		or dp.id not in ( " + 
						// Llistat de sobreescrits
			"			select dps.id " +
			"			from DefinicioProces dpa " +
			"				join dpa.expedientTipus dpt with dpt.id = :expedientTipusId, " +
			"				DefinicioProces dps " +
			"			where " +
			"				dps.jbpmKey = dpa.jbpmKey " +
			"			 	and dps.expedientTipus.id = dpt.expedientTipusPare.id " +
			"		) " +
			"	) " +
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
			"		    and dps.jbpmKey= dp.jbpmKey" +
/* nou!*/	"			and (dps.expedientTipus.id = dp.expedientTipus.id or dps.expedientTipus is null and dp.expedientTipus is null) "  +
			"	) ")
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

	// TODO: adaptar amb expedientTipusId
	/** Mètode per consultar quantes versions hi ha per definició de procés en un entorn.
	 * Aquesta consulta s'utilitza en el datatable de definicions de procés.
	 * Retorna una llista amb els valors <[jbpmKey, count]>*/
	@Query(	"select d.jbpmKey as jbpmKey, " +
			"		d.expedientTipus.id, " +
			"		count(d) as nversions " +
			"from DefinicioProces d " +
			"where d.entorn.id = :entornId " + 
			"		and d.jbpmKey in (:consultaJbpmKeys) " +
			"group by d.jbpmKey, d.expedientTipus.id ")
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

	/** Recupera la informació de tots els registres sobreescrits.*/
	@Query( "select dps " +
			"from DefinicioProces dp " +
			"	join dp.expedientTipus dpt with dpt.id = :expedientTipusId, " +
			"	DefinicioProces dps " +
			"where " +
			"	dps.jbpmKey = dp.jbpmKey " +
			" 	and dps.expedientTipus.id = dpt.expedientTipusPare.id ")
	List<DefinicioProces> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);	
}
