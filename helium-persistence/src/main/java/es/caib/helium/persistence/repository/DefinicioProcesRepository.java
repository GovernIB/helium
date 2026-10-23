/**
 *
 */
package es.caib.helium.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persistence.entity.DefinicioProces;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una definició de procés que està emmagatzemada
 * a dins la base de dades.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DefinicioProcesRepository extends JpaRepository<DefinicioProces, Long> {

	/** Troba la definició de procés pel seu identificador de la taula de definicions de procés. */
	Optional<DefinicioProces> findById(
			Long Id);

	/** Troba la definició de procés pel seu identificador bpm de la taula de definicions de procés. */
	DefinicioProces findByBpmId(
			String bpmId);

	/** Mètode per trobar directament una definició de procés a partir del seu codi BPM i la versió
	 *
	 * @param bpmKey
	 * @param versio
	 * @return
	 */
	DefinicioProces findByBpmKeyAndVersio(
			String bpmKey,
			int versio);

	/** Troba la darrera versió de la definició de procés per codi bpm per a un tipus d'expedient sense tenir en compte
	 * l'herència.
	 * @param expedientTipusId
	 * @return
	 */
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.entorn.id = :entornId" +
			"	 and dp.bpmKey = :bpmKey " +
			" 	 and dp.versio = (" +
			"    	select " +
			"       	 max(dps.versio) " +
			"    	from " +
			"        	DefinicioProces dps " +
			"    	where " +
			"       	dps.entorn.id = :entornId" +
			"       	and ((:esNullExpedientTipusId = true and dps.expedientTipus is null) or dps.expedientTipus.id = :expedientTipusId)" +
			"    		and dps.bpmKey = :bpmKey) ")
	DefinicioProces findDarreraVersioAmbEntornTipusIBpmKey(
		@Param("entornId") Long entornId,
		@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
		@Param("expedientTipusId") long expedientTipusId,
		@Param("bpmKey") String bpmKey);

	/** Troba la darrera versió de la definició de procés per codi bpm per a un tipus d'expedient sense tenir en compte
	 * l'herència.
	 * @param expedientTipusId
	 * @return
	 */
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.expedientTipus.id = :expedientTipusId" +
			"	 and dp.bpmKey = :bpmKey " +
			" 	 and dp.versio = (" +
			"    	select " +
			"       	 max(dps.versio) " +
			"    	from " +
			"        	DefinicioProces dps " +
			"    	where " +
			"       	dps.expedientTipus.id=:expedientTipusId" +
			"    		and dps.bpmKey = :bpmKey) ")
	DefinicioProces findDarreraVersioAmbTipusExpedientIBpmKey(
		@Param("expedientTipusId") Long expedientTipusId,
		@Param("bpmKey") String bpmKey);

	/** Troba la darrera versió de la definició de procés global per codi bpm per a un entorn.
	 * Les definicions de procés globals no tenen cap expedient tipus associat.
	 */
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.entorn.id = :entornId" +
			"	 and dp.bpmKey = :bpmKey " +
			" 	 and dp.versio = (" +
			"    	select " +
			"       	 max(dps.versio) " +
			"    	from " +
			"        	DefinicioProces dps " +
			"    	where " +
			"       	dps.entorn.id = :entornId" +
			"       	and dps.expedientTipus is null " +
			"    		and dps.bpmKey = :bpmKey) ")
	DefinicioProces findDarreraVersioGlobalAmbBpmKey(
		@Param("entornId") Long entornId,
		@Param("bpmKey") String bpmKey);


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
			"    	and dps.bpmKey=dp.bpmKey) ")
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

	/** Troba totes les versions de definicions de procés filtrant per codi bpm i ordenant per versió descendent
	 * sense tenir en compte l'herència.
	 * @param expedientTipusId
	 * @param bpmKey
	 * @return
	 */
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.expedientTipus.id = :expedientTipusId" +
			"	 and dp.bpmKey = :bpmKey " +
			"order by dp.versio desc")
	public List<DefinicioProces> findByExpedientTipusIBpmKey(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("bpmKey") String bpmKey);

	/**
	 * Troba totes les versions globals de la definició de procés bpmKey en un entorn.
	 * @param entornId
	 * @param bpmKey
	 * @return
	 */
	@Query(	"from " +
			"    DefinicioProces dp " +
			"where " +
			"    dp.expedientTipus is null " +
			"    and dp.entorn.id = :entornId " +
			"	 and dp.bpmKey = :bpmKey " +
			"order by dp.versio desc")
	List<DefinicioProces> findByEntornIBpmKey(
			@Param("entornId") Long entornId,
			@Param("bpmKey") String bpmKey);

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
	"							dpss.bpmKey = dpa.bpmKey " +
	"			 				and dpss.expedientTipus.id = dpt.expedientTipusPare.id " +
	"					  )" +
	"			  )" +
	"    and dps.bpmKey=dp.bpmKey)")
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
			"				dps.bpmKey = dpa.bpmKey " +
			"			 	and dps.expedientTipus.id = dpt.expedientTipusPare.id " +
			"		) " +
			"	) " +
			"	and (:esNullFiltre = true or lower(dp.bpmKey) like lower('%'||:filtre||'%')) " +
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
			"		    and dps.bpmKey= dp.bpmKey" +
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

	/** Consulta de les claus bpmKeys de les definicions de procés per entorn
	 * o tipus d'expedient que pot retornar o no les definicions
	 * de procés globals.
	 *
	 * @param entornId
	 * @param esNullExpedientTipusId
	 * @param expedientTipusId
	 * @param incloureGlobals
	 * @param ambHerencia
	 * @return
	 */
	@Query(	"select dp.bpmKey " +
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
			"				dps.bpmKey = dpa.bpmKey " +
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
			"		    and dps.bpmKey= dp.bpmKey" +
			"			and (dps.expedientTipus.id = dp.expedientTipus.id or dps.expedientTipus is null and dp.expedientTipus is null) "  +
			"	) ")
	List<String> findBpmKeys(
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
	"    	and dps.bpmKey=dp.bpmKey) ")
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
			"				dps.bpmKey = dpa.bpmKey " +
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
			"		    and dps.bpmKey= dp.bpmKey" +
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
			"	dps.bpmKey = dp.bpmKey " +
			" 	and dps.expedientTipus.id = dpt.expedientTipusPare.id ")
	List<DefinicioProces> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);

	/** Mètode per consultar quantes versions hi ha per definició de procés en un entorn.
	 * Aquesta consulta s'utilitza en el datatable de definicions de procés.
	 * Retorna una llista amb els valors <[bpmKey, expedientTipusId, count]>*/
	@Query(	"select d.bpmKey as bpmKey, " +
			"		d.expedientTipus.id, " +
			"		count(d) as nversions " +
			"from DefinicioProces d " +
			"where d.entorn.id = :entornId " +
			"		and d.bpmKey in (:consultaBpmKeys) " +
			"group by d.bpmKey, d.expedientTipus.id ")
	List<Object[]> countVersions(
			@Param("entornId") Long entornId,
			@Param("consultaBpmKeys") List<String> consultaBpmKeys);

	/// MÈTODES PER ELIMINAR LES DEFINICIONS DE PROCÉS NO UTILITZADES

	/** Mètode per obtenir d'una sola vegada totes les definicions de procés l'identificador
	 * bpm de les quals estigui en la llista passada com a paràmetre. La consulta és paginada.
	 * @param expedientTipusId
	 * @param bpmIds
	 * @return
	 */
	@Query("from DefinicioProces dp " +
			"where " +
			"		dp.expedientTipus.id = :expedientTipusId " +
			"  	and dp.bpmId in (:bpmIds) ")
	Page<DefinicioProces> findAmbExpedientTipusIBpmIds(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("bpmIds") List<String> bpmIds,
			Pageable pageable);

	/** Mètode per obtenir d'una sola vegada tots els identificadors de definicions de procés l'identificador
	 * bpm de les quals estigui en la llista passada com a paràmetre. Aquest mètode es podria eliminiar si es
	 * consulta la definició de procés per cada identificador.
	 * @param expedientTipusId
	 * @param bpmIds
	 * @return
	 */
	@Query("select dp.id from DefinicioProces dp " +
			"where " +
			"		dp.expedientTipus.id = :expedientTipusId " +
			"  	and dp.bpmId in (:bpmIds) ")
	List<Long> findIdsAmbExpedientTipusIBpmIds(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("bpmIds") List<String> bpmIds);
}
