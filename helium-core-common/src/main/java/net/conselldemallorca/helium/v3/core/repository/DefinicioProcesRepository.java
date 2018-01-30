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

	/** Troba la definició de procés pel seu identificador de la taula de definicions de procés. */
	DefinicioProces findById(
			Long Id);

	/** Troba la definició de procés pel seu identificador jbpm de la taula de definicions de procés. */
	DefinicioProces findByJbpmId(
			String jbpmId);

	/** Troba la darrera versió de la definició de procés per codi jbpm per a un tipus d'expedient sense tenir en compte
	 * l'herència.
	 * @param tipusExpedientId
	 * @param jbpmProcessDefinitionKey
	 * @return
	 */
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.entorn.id = :entornId" +
			"	 and dp.jbpmKey = :jbpmKey " +
			" 	 and dp.versio = (" +
			"    	select " +
			"       	 max(dps.versio) " +
			"    	from " +
			"        	DefinicioProces dps " +
			"    	where " +
			"       	dps.entorn.id = :entornId" +
			"    		and dps.jbpmKey = dp.jbpmKey " +
			"    		and dps.jbpmKey = :jbpmKey) ")
	DefinicioProces findDarreraVersioAmbEntornIJbpmKey(
		@Param("entornId") Long entornId,
		@Param("jbpmKey") String jbpmKey);
	
	/** Troba la darrera versió de la definició de procés per codi jbpm per a un tipus d'expedient sense tenir en compte
	 * l'herència.
	 * @param tipusExpedientId
	 * @param jbpmProcessDefinitionKey
	 * @return
	 */
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.expedientTipus.id = :tipusExpedientId" +
			"	 and dp.jbpmKey = :jbpmKey " +
			" 	 and dp.versio = (" +
			"    	select " +
			"       	 max(dps.versio) " +
			"    	from " +
			"        	DefinicioProces dps " +
			"    	where " +
			"       	dps.expedientTipus.id=:tipusExpedientId" +
			"    		and dps.jbpmKey = dp.jbpmKey " +
			"    		and dps.jbpmKey = :jbpmKey) ")
	DefinicioProces findDarreraVersioAmbTipusExpedientIJbpmKey(
		@Param("tipusExpedientId") Long tipusExpedientId,
		@Param("jbpmKey") String jbpmKey);
	
	/** Troba la darrera versió de la definició de procés per codi jbpm per a un tipus d'expedient tenint en compte
	 * l'herència o no segons el paràmetre.
	 * 
	 * @param tipusExpedientId
	 * @param jbpmProcessDefinitionKey
	 * @param herencia
	 * @return
	 */
	@Query(	"from " +
	"    DefinicioProces dp " +
	"where " +
	"    ( dp.expedientTipus.id = :tipusExpedientId" +
	"		or (:herencia = true " +
	"				and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
	"	 ) " +
	"	and (:herencia = false " +
	"		or dp.id not in ( " + 
				// Llistat de sobreescrits
	"			select dps.id " +
	"			from DefinicioProces dpa " +
	"				join dpa.expedientTipus et with et.id = :expedientTipusId, " +
	"				DefinicioProces dps " +
	"			where " +
	"				dps.jbpmKey = dpa.jbpmKey " +
	"			 	and dps.expedientTipus.id = et.expedientTipusPare.id " +
	"		) " +
	"	) " +
	" and dp.versio = (" +
	"    select " +
	"        max(dps.versio) " +
	"    from " +
	"        DefinicioProces dps " +
	"    where " +
	"        (dps.expedientTipus.id=:expedientTipusId" +
	"			or (:herencia = true " +
	"					and dps.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
	"		 ) " +
	"    and dps.jbpmKey = dp.jbpmKey " +
	"    and dps.jbpmKey = :jbpmProcessDefinitionKey) " +
	"order by dp.versio DESC")
DefinicioProces findDarreraVersioAmbTipusExpedientIJbpmKey(
		@Param("expedientTipusId") Long expedientTipusId,
		@Param("jbpmProcessDefinitionKey") String jbpmProcessDefinitionKey,		
		@Param("herencia") boolean herencia);	
	
//	//TODO: eliminar aquest mètode que només serveix per evitar un error a l'hora de trobar el subprocés
//	DefinicioProces findByJbpmKeyAndVersio(
//			String jbpmKey,
//			int versio);
//	
//	//TODO: comprovar que filtra bé per tipus d'expedient o bé s'ha de posar isNull
//	@Query(	"from " +
//			"    DefinicioProces dp " +
//			"where ((:expedientTipusId is null and dp.expedientTipus is null) or dp.expedientTipus.id = :expedientTipusId) " +
//			"	and dp.jbpmKey = :jbpmKey " +
//			"	and dp.versio = :versio ")
//	DefinicioProces findByJbpmKeyAndVersio(
//			@Param("expedientTipusId") Long expedientTipusId,
//			@Param("jbpmKey") String jbpmKey,
//			@Param("versio") int versio);
//
//	// TODO: revisar on es crida aquest mètode pequè es pot substituir pel pegüent distingint per tipus d'expedient
//	List<DefinicioProces> findByEntornIdAndJbpmKeyOrderByVersioDesc(
//			Long entornId,
//			String jbpmKey);
//
//	@Query(	"from " +
//			"    DefinicioProces dp " +
//			"where " +
//			" 	 dp.entorn.id = :entornId " +
//			"    and ((:isNullExpedientTipus = true and dp.expedientTipus is null ) " +
//			"				or (:isNullExpedientTipus = false and dp.expedientTipus.id = :expedientTipusId)) " +
//			"    and dp.jbpmKey = :jbpmKey " +
//			"order by dp.versio DESC")
//	List<DefinicioProces> findByEntornIdAndJbpmKey(
//			@Param("entornId") Long entornId,
//			@Param("jbpmKey") String jbpmKey,
//			@Param("isNullExpedientTipus") boolean isNullExpedientTipus,
//			@Param("expedientTipusId") Long expedientTipusId);
//	
//	@Query(	"from " +
//			"    DefinicioProces dp " +
//			"where " +
//			"    ( dp.expedientTipus.id = :tipusExpedientId" +
//			"		or (:herencia = true " +
//			"				and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :tipusExpedientId) ) " + 
//			"	 ) " +
//			"	and (:herencia = false " +
//			"		or dp.id not in ( " + 
//						// Llistat de sobreescrits
//			"			select dps.id " +
//			"			from DefinicioProces dpa " +
//			"				join dpa.expedientTipus dpt with dpt.id = :tipusExpedientId, " +
//			"				DefinicioProces dps " +
//			"			where " +
//			"				dps.jbpmKey = dpa.jbpmKey " +
//			"			 	and dps.expedientTipus.id = dpt.expedientTipusPare.id " +
//			"		) " +
//			"	) " +
//			" and dp.versio = (" +
//			"    select " +
//			"        max(dps.versio) " +
//			"    from " +
//			"        DefinicioProces dps " +
//			"    where " +
//			"        (dps.expedientTipus.id=:tipusExpedientId" +
//			"			or (:herencia = true " +
//			"					and dps.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :tipusExpedientId) ) " + 
//			"		 ) " +
//			"    and dps.jbpmKey = dp.jbpmKey " +
//			"    and dps.jbpmKey = :jbpmProcessDefinitionKey) " +
//			"order by dp.versio DESC")
//	DefinicioProces findDarreraVersioAmbTipusExpedientIJbpmKey(
//				@Param("tipusExpedientId") Long tipusExpedientId,
//				@Param("jbpmProcessDefinitionKey") String jbpmProcessDefinitionKey,		
//				@Param("herencia") boolean herencia);
//	
//	@Query(	"from " +
//				"    DefinicioProces dp " +
//				"where " +
//				"    dp.entorn.id=:entornId " +
//				"and dp.versio = (" +
//				"    select " +
//				"        max(dps.versio) " +
//				"    from " +
//				"        DefinicioProces dps " +
//				"    where " +
//				"        dps.entorn.id=:entornId " +
//				"    and dps.jbpmKey=dp.jbpmKey " +
//				"    and dps.jbpmKey = :jbpmProcessDefinitionKey) " +
//				"order by dp.versio DESC")
//	DefinicioProces findDarreraVersioAmbEntornIJbpmKey(
//			@Param("entornId") Long entornId,
//			@Param("jbpmProcessDefinitionKey") String jbpmProcessDefinitionKey);
//	
//	@Query(	"select dp.id from " +
//			"    DefinicioProces dp " +
//			"where " +
//			"    dp.entorn.id=:entornId " +
//			"and dp.expedientTipus.id=:expedientTipusId " +
//			"and dp.versio = (" +
//			"    select " +
//			"        max(dps.versio) " +
//			"    from " +
//			"        DefinicioProces dps " +
//			"    where " +
//			"        dps.entorn.id=:entornId " +
//			"    and dps.jbpmKey=dp.jbpmKey)")
//	List<Long> findIdsDarreraVersioAmbEntornIdIExpedientTipusId(
//			@Param("entornId") Long entornId,
//			@Param("expedientTipusId") Long expedientTipusId);
//
//	
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
			"							and dps.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"					or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"		    and dps.jbpmKey= dp.jbpmKey" +
			"			and (dps.expedientTipus.id = dp.expedientTipus.id or dps.expedientTipus is null and dp.expedientTipus is null) "  +
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
//	
//	@Query(	"select dp.jbpmKey " + 
//			"from DefinicioProces dp " +
//			"where " +
//			"   dp.entorn.id = :entornId " +
//			"	and (dp.expedientTipus.id = :expedientTipusId " + 
//						// Heretats
//			"			or (:herencia = true " +
//			"					and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
//						// Globals
//			"			or (:incloureGlobals = true and dp.expedientTipus is null)) " +
//			"	and dp.versio = (" +
//			"  		select max(dps.versio) " +
//			"    	from DefinicioProces dps " +
//			"    	where " +
//			"       	dps.entorn.id = :entornId " +
//			"			and (dps.expedientTipus.id = :expedientTipusId " + 
//						// Heretats
//			"			or (:herencia = true " +
//			"					and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
//						// Globals
//			"				or (:incloureGlobals = true and dp.expedientTipus is null)) " +
//			"		    and dps.jbpmKey= dp.jbpmKey) ")
//	List<String> findJbpmKeys(
//			@Param("entornId") Long entornId,
//			@Param("expedientTipusId") Long expedientTipusId,
//			@Param("herencia") boolean herencia,
//			@Param("incloureGlobals") boolean incloureGlobals);
//
//	// TODO: adaptar amb expedientTipusId
//	/** Mètode per consultar quantes versions hi ha per definició de procés en un entorn.
//	 * Aquesta consulta s'utilitza en el datatable de definicions de procés.
//	 * Retorna una llista amb els valors <[jbpmKey, count]>*/
//	@Query(	"select d.jbpmKey as jbpmKey, " +
//			"		d.expedientTipus.id, " +
//			"		count(d) as nversions " +
//			"from DefinicioProces d " +
//			"where d.entorn.id = :entornId " + 
//			"		and d.jbpmKey in (:consultaJbpmKeys) " +
//			"group by d.jbpmKey, d.expedientTipus.id ")
//	List<Object[]> countVersions(
//			@Param("entornId") Long entornId,
//			@Param("consultaJbpmKeys") List<String> consultaJbpmKeys);	
//	
//	@Query("from DefinicioProces dp " +
//			"where " +
//			"		dp.expedientTipus.id = :expedientTipusId " +
//			"  	and dp.jbpmId in (:jbpmIds) ")
//	Page<DefinicioProces> findAmbExpedientTipusIJbpmIds(
//			@Param("expedientTipusId") Long expedientTipusId,
//			@Param("jbpmIds") Collection<String> jbpmIds,
//			Pageable pageable);
//	
//	@Query("select dp.id from DefinicioProces dp " +
//			"where " +
//			"		dp.expedientTipus.id = :expedientTipusId " +
//			"  	and dp.jbpmId in (:jbpmIds) ")
//	List<Long> findIdsAmbExpedientTipusIJbpmIds(
//			@Param("expedientTipusId") Long expedientTipusId,
//			@Param("jbpmIds") Collection<String> jbpmIds);
//
//	@Query("from DefinicioProces dp " +
//			"where dp.expedientTipus.id = :expedientTipusId " +
//			"order by dp.etiqueta")
//	List<DefinicioProces> findAmbExpedientTipus(
//			@Param("expedientTipusId") Long expedientTipusId);	
//	
//	@Query(	"from DefinicioProces dp " +
//			"where " +
//			"   dp.entorn.id=:entornId " +
//			"	and ((:isNullExpedientTipusId = true) or ( dp.expedientTipus.id = :expedientTipusId) or (:incloureGlobals = true and dp.expedientTipus is null)) " + 
//			"	and dp.versio = (" +
//			"    select " +
//			"        max(dps.versio) " +
//			"    from " +
//			"        DefinicioProces dps " +
//			"    where " +
//			"        dps.entorn.id=:entornId " +
//			"    	and dps.jbpmKey=dp.jbpmKey) ")
//	List<DefinicioProces> findByAll(
//			@Param("entornId") Long entornId,
//			@Param("isNullExpedientTipusId") boolean isNullExpedientTipusId,
//			@Param("expedientTipusId") Long expedientTipusId,
//			@Param("incloureGlobals") boolean incloureGlobals);
//
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
