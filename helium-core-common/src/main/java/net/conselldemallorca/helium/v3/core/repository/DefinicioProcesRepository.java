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

	/** Mètode per trobar directament una definició de procés a partir del seu codi JBPM i la versió
	 * 
	 * @param jbpmKey
	 * @param versio
	 * @return
	 */
	DefinicioProces findByJbpmKeyAndVersio(
			String jbpmKey,
			int versio);
	
	/** Troba la darrera versió de la definició de procés per codi jbpm per a un tipus d'expedient sense tenir en compte
	 * l'herència.
	 * @param expedientTipusId
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
			"       	and ((:esNullExpedientTipusId = true and dps.expedientTipus is null) or dps.expedientTipus.id = :expedientTipusId)" +
			"    		and dps.jbpmKey = :jbpmKey) ")
	DefinicioProces findDarreraVersioAmbEntornTipusIJbpmKey(
		@Param("entornId") Long entornId,
		@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
		@Param("expedientTipusId") long expedientTipusId,		
		@Param("jbpmKey") String jbpmKey);
	
	/** Troba la darrera versió de la definició de procés per codi jbpm per a un tipus d'expedient sense tenir en compte
	 * l'herència.
	 * @param expedientTipusId
	 * @param jbpmProcessDefinitionKey
	 * @return
	 */
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.expedientTipus.id = :expedientTipusId" +
			"	 and dp.jbpmKey = :jbpmKey " +
			" 	 and dp.versio = (" +
			"    	select " +
			"       	 max(dps.versio) " +
			"    	from " +
			"        	DefinicioProces dps " +
			"    	where " +
			"       	dps.expedientTipus.id=:expedientTipusId" +
			"    		and dps.jbpmKey = :jbpmKey) ")
	DefinicioProces findDarreraVersioAmbTipusExpedientIJbpmKey(
		@Param("expedientTipusId") Long expedientTipusId,
		@Param("jbpmKey") String jbpmKey);	
	
	/** Troba la darrera versió de la definició de procés global per codi jbpm per a un entorn.
	 * Les definicions de procés globals no tenen cap expedient tipus associat.
	 * @param expedientTipusId
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
			"       	and dps.expedientTipus is null " +
			"    		and dps.jbpmKey = :jbpmKey) ")
	DefinicioProces findDarreraVersioGlobalAmbJbpmKey(
		@Param("entornId") Long entornId,
		@Param("jbpmKey") String jbpmKey);	
	

	/** Troba el llistat de darreres versions de la definició de procés filtrant per entorn, tipus d'expedient si s'escau
	 * i amb la possibilitat d'incloure les definicions de procés globals.
	 * @param entornId
	 * @param isNullExpedientTipusId
	 * @param expedientTipusId
	 * @param incloureGlobals
	 * @return
	 */
	@Query(	"from DefinicioProces dp " +
			"where " +
			"   dp.entorn.id=:entornId " +
			"	and (:isNullExpedientTipusId = true or dp.expedientTipus.id = :expedientTipusId or (:incloureGlobals = true and dp.expedientTipus is null)) " + 
			"	and dp.versio = (" +
			"    select " +
			"        max(dps.versio) " +
			"    from " +
			"        DefinicioProces dps " +
			"    where " +
			"        dps.entorn.id=:entornId " +
			"		and (:isNullExpedientTipusId = true or dps.expedientTipus.id = :expedientTipusId or (:incloureGlobals = true and dps.expedientTipus is null)) " + 
			"    	and dps.jbpmKey=dp.jbpmKey) ")
	List<DefinicioProces> findListDarreraVersioByAll(
			@Param("entornId") Long entornId,
			@Param("isNullExpedientTipusId") boolean isNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("incloureGlobals") boolean incloureGlobals);
	
	
	/** Troba totes les definicions de procés relacionades amb un tipus d'expedient. */
	@Query("from DefinicioProces dp " +
			"where dp.expedientTipus.id = :expedientTipusId " +
			"order by dp.etiqueta")
	List<DefinicioProces> findAmbExpedientTipus(
			@Param("expedientTipusId") Long expedientTipusId);	
	
	/** Troba totes les versions de definicions de procés filtrant per codi jbpm i ordenant per versió descendent 
	 * sense tenir en compte l'herència.
	 * @param expedientTipusId
	 * @param jbpmKey
	 * @return
	 */
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.expedientTipus.id = :expedientTipusId" +
			"	 and dp.jbpmKey = :jbpmKey " +
			"order by dp.versio desc")
	public List<DefinicioProces> findByExpedientTipusIJpbmKey(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("jbpmKey") String jbpmKey);
	
	/** Troba totes les versions globals de la definició de procés jbpmKey en un entorn.
	 *  
	 * @param id
	 * @param jbpmKey
	 * @return
	 */
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.expedientTipus is null " +
			"    and dp.entorn.id = :entornId " +
			"	 and dp.jbpmKey = :jbpmKey " +
			"order by dp.versio desc")
	List<DefinicioProces> findByEntornIJbpmKey(
			@Param("entornId") Long entornId,
			@Param("jbpmKey") String jbpmKey);

	/** Mètode per consultar els identificadors de les darreres definicions de procés per a un tipus
	 * d'expedient. Té en compte l'herència i la sobreescriptura de definicions de procés.
	 * @param entornId
	 * @param expedientTipusId
	 * @return
	 */
	@Query(	"select dp.id from " +
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
	"		 and (dps.expedientTipus.id=:expedientTipusId " +
				  // Heretats
	"			  or (dps.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId ))" +
	"			 	  and dps.id not in (" +
							// Sobreescrits
	" 						select dpss.id " +			
	" 						from DefinicioProces dpa " +			
	"							join dpa.expedientTipus dpt with dpt.id = :expedientTipusId, " +
	"							DefinicioProces dpss " +
	"						where " +
	"							dpss.jbpmKey = dpa.jbpmKey " +
	"			 				and dpss.expedientTipus.id = dpt.expedientTipusPare.id " +
	"					  )" + 
	"			  )" +
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
	 * @param ambHerencia
	 * @param pageable
	 * @return
	 */
	@Query(	"from DefinicioProces dp " +
			"where " +
			"   dp.entorn.id = :entornId " +
			"	and (:esNullExpedientTipusId = true " +
			"			or (dp.expedientTipus.id = :expedientTipusId) " + 
						// Heretats
			"			or (:ambHerencia = true " +
			"					and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"			or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"	and (:ambHerencia = false " +
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
			"					or (:ambHerencia = true " +
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
			@Param("ambHerencia") boolean ambHerencia,
			Pageable pageable);	
	
	/** Consulta de les claus jbpmKeys de les definicions de procés per entorn 
	 * o tipus d'expedient que pot retornar o no les definicions
	 * de procés globals.
	 * 
	 * @param entornId
	 * @param esNullExpedientTipusId
	 * @param expedientTipusId
	 * @param incloureGlobals
	 * @param esNullFiltre
	 * @param filtre
	 * @param ambHerencia
	 * @return
	 */
	@Query(	"select dp.jbpmKey " +
			"from DefinicioProces dp " +
			"where " +
			"   dp.entorn.id = :entornId " +
			"	and (:esNullExpedientTipusId = true " +
			"			or (dp.expedientTipus.id = :expedientTipusId) " + 
						// Heretats
			"			or (:ambHerencia = true " +
			"					and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"			or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"	and (:ambHerencia = false " +
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
			"	and dp.versio = (" +
			"  		select max(dps.versio) " +
			"    	from DefinicioProces dps " +
			"    	where " +
			"       	dps.entorn.id = :entornId " +
			"			and (:esNullExpedientTipusId = true " +
			"					or (dp.expedientTipus.id = :expedientTipusId) " +
			"					or (:ambHerencia = true " +
			"							and dps.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"					or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"		    and dps.jbpmKey= dp.jbpmKey" +
			"			and (dps.expedientTipus.id = dp.expedientTipus.id or dps.expedientTipus is null and dp.expedientTipus is null) "  +
			"	) ")
	List<String> findJbpmKeys(
			@Param("entornId") Long entornId,
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("incloureGlobals") boolean incloureGlobals,
			@Param("ambHerencia") boolean ambHerencia);	
	
	@Query(	"from DefinicioProces dp " +
	"where " +
	"   dp.entorn.id=:entornId " +
	"	and ((:isNullExpedientTipusId = true or dp.expedientTipus.id = :expedientTipusId) " + 
	"			or (:incloureGlobals = true and dp.expedientTipus is null)) " + 
	"	and dp.versio = (" +
	"    select " +
	"        max(dps.versio) " +
	"    from " +
	"        DefinicioProces dps " +
	"    where " +
	"        dps.entorn.id=:entornId " +
	"    	and dps.jbpmKey=dp.jbpmKey) ")
	List<DefinicioProces> findDarreresVersionsBy(
		@Param("entornId") Long entornId,
		@Param("isNullExpedientTipusId") boolean isNullExpedientTipusId,
		@Param("expedientTipusId") Long expedientTipusId,
		@Param("incloureGlobals") boolean incloureGlobals);	
	
	/** Consulta de les darreres versions de defincions de procés per entorn 
	 * o tipus d'expedient que pot retornar o no les definicions
	 * de procés globals.
	 * 
	 * @param entornId
	 * @param esNullExpedientTipusId
	 * @param expedientTipusId
	 * @param incloureGlobals
	 * @param esNullFiltre
	 * @param filtre
	 * @param ambHerencia
	 * @return
	 */
	@Query(	"select dp " +
			"from DefinicioProces dp " +
			"where " +
			"   dp.entorn.id = :entornId " +
			"	and (:esNullExpedientTipusId = true " +
			"			or (dp.expedientTipus.id = :expedientTipusId) " + 
						// Heretats
			"			or (:ambHerencia = true " +
			"					and dp.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"			or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"	and (:ambHerencia = false " +
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
			"	and dp.versio = (" +
			"  		select max(dps.versio) " +
			"    	from DefinicioProces dps " +
			"    	where " +
			"       	dps.entorn.id = :entornId " +
			"			and (:esNullExpedientTipusId = true " +
			"					or (dp.expedientTipus.id = :expedientTipusId) " +
			"					or (:ambHerencia = true " +
			"							and dps.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"					or (:incloureGlobals = true and dp.expedientTipus is null)) " +
			"		    and dps.jbpmKey= dp.jbpmKey" +
			"			and (dps.expedientTipus.id = dp.expedientTipus.id or dps.expedientTipus is null and dp.expedientTipus is null) "  +
			"	) ")
	List<DefinicioProces> findJbfindDarreresVersionsAmbHerencia(
			@Param("entornId") Long entornId,
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("incloureGlobals") boolean incloureGlobals,
			@Param("ambHerencia") boolean ambHerencia);
	
	/** Recupera la informació de tots els registres sobreescrits.*/
	@Query( "select dps " +
			"from DefinicioProces dp " +
			"	join dp.expedientTipus dpt with dpt.id = :expedientTipusId, " +
			"	DefinicioProces dps " +
			"where " +
			"	dps.jbpmKey = dp.jbpmKey " +
			" 	and dps.expedientTipus.id = dpt.expedientTipusPare.id ")
	List<DefinicioProces> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);	

	/** Mètode per consultar quantes versions hi ha per definició de procés en un entorn.
	 * Aquesta consulta s'utilitza en el datatable de definicions de procés.
	 * Retorna una llista amb els valors <[jbpmKey, expedientTipusId, count]>*/
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
	
	/// MÈTODES PER ELIMINAR LES DEFINICIONS DE PROCÉS NO UTILITZADES

	/** Mètode per obtenir d'una sola vegada totes les definicions de procés l'identificador
	 * jbpm de les quals estigui en la llista passada com a paràmetre. La consulta és paginada.
	 * @param expedientTipusId
	 * @param jbpmIds
	 * @return
	 */
	@Query("from DefinicioProces dp " +
			"where " +
			"		dp.expedientTipus.id = :expedientTipusId " +
			"  	and dp.jbpmId in (:jbpmIds) ")
	Page<DefinicioProces> findAmbExpedientTipusIJbpmIds(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("jbpmIds") List<String> jbpmIds,
			Pageable pageable);

	/** Mètode per obtenir d'una sola vegada tots els identificadors de definicions de procés l'identificador
	 * jbpm de les quals estigui en la llista passada com a paràmetre. Aquest mètode es podria eliminiar si es
	 * consulta la definició de procés per cada identificador.
	 * @param expedientTipusId
	 * @param jbpmIds
	 * @return
	 */
	@Query("select dp.id from DefinicioProces dp " +
			"where " +
			"		dp.expedientTipus.id = :expedientTipusId " +
			"  	and dp.jbpmId in (:jbpmIds) ")
	List<Long> findIdsAmbExpedientTipusIJbpmIds(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("jbpmIds") List<String> jbpmIds);
}
