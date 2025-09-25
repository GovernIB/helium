/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Collection;
import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un tipus d'expedient que està emmagatzemat a dins
 * la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTipusRepository extends JpaRepository<ExpedientTipus, Long> {

	List<ExpedientTipus> findByEntorn(Entorn entorn);
	
	List<Long> findIdByEntornId(Long entornId);
	
	List<ExpedientTipus> findByEntornOrderByNomAsc(Entorn entorn);
	
	List<ExpedientTipus> findByEntornOrderByCodiAsc(Entorn entorn);
	
	List<ExpedientTipus> findByCodi(String codi);

	ExpedientTipus findByEntornAndCodi(Entorn entorn, String codi);

	ExpedientTipus findByEntornAndId(Entorn entorn, Long id);

	ExpedientTipus findById(Long expedientTipusId);

	/** Per consultar l'expedient tipus amb bloqueig de BBDD per actualitzar les seqüències de números
	 * @param expedientTipusId
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from ExpedientTipus where id = :expedientTipusId")
	public ExpedientTipus findByIdAmbBloqueig(@Param("expedientTipusId") Long expedientTipusId);

	@Query(	"from ExpedientTipus e " +
			"where " +
			"    e.entorn = :entorn " +
			"and e.id in (:tipusPermesosIds) " +
			"and (:esNullFiltre = true or lower(e.nom) like lower('%'||:filtre||'%') or lower(e.codi) like lower('%'||:filtre||'%')) ")
	Page<ExpedientTipus> findByFiltreGeneralPaginat(
			@Param("entorn") Entorn entorn,
			@Param("tipusPermesosIds") Collection<Long> tipusPermesosIds,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	@Query(	"from ExpedientTipus e " +
			"where " +
			"    e.entorn = :entorn " +
			"	and e.heretable = true " +
			"order by e.nom asc ")
	List<ExpedientTipus> findHeretablesByEntorn( @Param("entorn") Entorn entorn);
	
	List<ExpedientTipus> findByExpedientTipusPareIdOrderByCodiAsc(Long expedientTipusPareId);

	/** Mètode per cercar un tipus d'expedient activat per distribuir anotacions de registre i configurat segons el codi
	 * de procediment o el codi del tipus d'assumpte.
	 * 
	 * @param codiProcediment
	 * @param codiAssumpte
	 * @return
	 */
	@Query(	"from " +
			"    ExpedientTipus et " +
			"where " +
			"	et.distribucioActiu = true " + 
			"and ((et.distribucioCodiProcediment is null) or (et.distribucioCodiProcediment = :codiProcediment)) " + 
			"and ((et.distribucioCodiAssumpte is null) or (et.distribucioCodiAssumpte = :codiAssumpte)) " +
			"order by " + 
			"	et.distribucioCodiProcediment asc NULLS LAST, " +
			"	et.distribucioCodiAssumpte asc NULLS LAST ")
	List<ExpedientTipus> findPerDistribuir(
			@Param("codiProcediment") String codiProcediment, 
			@Param("codiAssumpte") String codiAssumpte);
	
	/** Mètode per cercar un tipus d'expedient per validar la configuració de distribució segons els valors
	 * del codi de procediment i el codi d'assumpte.
	 * 
	 * @param codiProcediment
	 * @param esNullCodiProcediment
	 * @param codiAssumpte
	 * @param esNullCodiAssumpte
	 * @return
	 */
	@Query(	"from " +
			"    ExpedientTipus et " +
			"where " +
			"	et.distribucioActiu = true " + 
			"and ((:esNullCodiProcediment = true and et.distribucioCodiProcediment is null) or (et.distribucioCodiProcediment like :codiProcediment)) " + 
			"and ((:esNullCodiAssumpte = true and et.distribucioCodiAssumpte is null) or (et.distribucioCodiAssumpte like :codiAssumpte)) ")
	List<ExpedientTipus> findPerDistribuirValidacio(
			@Param("esNullCodiProcediment") boolean esNullCodiProcediment, 
			@Param("codiProcediment") String codiProcediment,
			@Param("esNullCodiAssumpte") boolean esNullCodiAssumpte,
			@Param("codiAssumpte") String codiAssumpte);
    
    @Query(    " select new net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto( "
            + "     et.id, "
            + "     et.codi, "
            + "     et.nom, "
            + "     count(*), "
            + "     to_char(e.dataInici, 'YYYY'))"
            + "    	from Expedient e "
            + "        inner join e.tipus as et  "
            + "    	where "
            + "     	et.entorn = :entorn"
            + "    		and (:isNullExpedientTipus = true or et = :expedientTipus)"
            + "			and (:isNullAnulat = true or e.anulat = :anulat) "
			+ "			and (:isNullDataIniciFinal = true or year(e.dataInici) <= :anyFinal) "
			+ "			and (:isNullDataIniciInicial = true or year(e.dataInici) >= :anyInicial) "
			+ "			and (:isNullNumero = true or lower(e.numero) like lower('%'||:numero||'%') ) "
			+ "			and (:isNullTitol = true or lower(e.titol) like lower('%'||:titol||'%') ) "
			+ "			and (:nomesIniciats = false or e.dataFi is null) "
			+ "			and (:nomesFinalitzats = false or e.dataFi is not null) "
            + "			and (:isNullAturat = true or (:aturat = true and e.infoAturat is not null) or (:aturat = false and e.infoAturat is null)) "
            + "			and (:isEstatIdNull = true or e.estat.id = :estatId) "
            + " group by "
            + "        et.id, "
            + "        et.codi, "
            + "        et.nom, "
            + "        to_char(e.dataInici, 'YYYY')")
    List<ExpedientTipusEstadisticaDto> findEstadisticaByFiltre(
            @Param("isNullDataIniciInicial") boolean isNullDataIniciInicial,
            @Param("anyInicial") Integer anyInicial,
            @Param("isNullDataIniciFinal") boolean isNullDataIniciFinal,
            @Param("anyFinal") Integer anyFinal,
            @Param("entorn") Entorn entorn,
            @Param("isNullExpedientTipus") boolean isNullExpedientTipus,
            @Param("expedientTipus") ExpedientTipus expedientTipus,
            @Param("isNullAnulat") boolean isNullAnulat,
            @Param("anulat") Boolean anulat,
            @Param("isNullNumero") boolean isNullNumero,
            @Param("numero") String numero, 
            @Param("isNullTitol") boolean isNullTitol,
            @Param("titol") String titol, 
            @Param("nomesIniciats") boolean nomesIniciats,
			@Param("nomesFinalitzats") boolean nomesFinalitzats,
			@Param("isEstatIdNull") boolean isEstatIdNull,
			@Param("estatId") Long estatId,
            @Param("isNullAturat") boolean isNullAturat,
            @Param("aturat") Boolean aturat
            );
    
    @Query(   " select new net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEstadisticaDto( "
			+ "		et.id, "
			+ "		et.codi, "
			+ "		et.nom) "
			+ "	from ExpedientTipus et "
			+ "	where "
			+ "		et.entorn = :entorn "
			+ "		and not exists elements(et.expedients) ")
	List<ExpedientTipusEstadisticaDto> findEstadisticaBySenseExpedients(@Param("entorn") Entorn entorn);

	/**
	  * Mètode per cercar els tipus d'expedient per cercador de tipologia tant pel seu codi, com pel nom, 
	     *  com per codiSia (nti_clasificacion) i per número de registre associat a l'anotació o a algún expedient.
	  * @param isNullCodi
	  * @param codi
	  * @param isNullNom
	  * @param nom
	  * @param isNullNtiClasificacion
	  * @param ntiClasificacion
	  * @param registre_num
	  * @return
	  */
   @Query(	 "select distinct et.id "
   		 	+"from " 
			+"    ExpedientTipus et " 
			+"where " 
	   		+" 		et.id in ( "
	   		+"  		select e.tipus.id "
	   		+"				from Expedient e "
	   		+"				where (lower(e.registreNumero) like lower('%'||:registre_num||'%')  ) )"
	   		+" 		or et.id in ( "
	   		+" 			select a.expedient.tipus.id "
	   		+"           	from Anotacio a "
	   		+"           	where (lower(a.identificador) like lower('%'||:registre_num||'%')  ) ) "
				)
   List<Long> findIdsByNumeroRegistre(@Param("registre_num") String registre_num);
   
	 /**
	  * Mètode per cercar els tipus d'expedient per cercador de tipologia tant pel seu codi, com pel nom, 
	     *  com per codiSia (nti_clasificacion) i per número de registre associat a l'anotació o a algún expedient.
	  * @param isNullCodi
	  * @param codi
	  * @param isNullNom
	  * @param nom
	  * @param isNullNtiClasificacion
	  * @param ntiClasificacion
	  * @param registre_num
	  * @return
	  */
    @Query(	 "from " 
			+"    ExpedientTipus et " 
			+"where " 
			+" 	 (:isNullEntornId = true or et.entorn.id = :entornId) "
			+"  and "
			+" 	 (:isNullCodi = true or lower(et.codi) like lower('%'||:codi||'%')) "
			+"  and "
			+" 	 (:isNullNom = true or lower(et.nom) like lower('%'||:nom||'%')) "
			+" 	and ( "
			+"	 (:isNullNtiClasificacion = true or lower(et.ntiClasificacion) like lower('%'||:ntiClasificacion||'%'))" 
			+"    or (:isNullNtiClasificacion = true or lower(et.notibCodiProcediment) like lower('%'||:ntiClasificacion||'%'))"
			+"    or (:isNullNtiClasificacion = true or lower(et.distribucioCodiProcediment) like lower('%'||:ntiClasificacion||'%'))" 
			+"	 ) "
			+"	and  "
			+	"(:isNullRegistre_num = true or "
    		+" 		et.id in (:expedientsTipusIds)) "
			)
    Page<ExpedientTipus> findByTipologia(
			@Param("isNullEntornId") boolean isNullEntornId,
			@Param("entornId") Long entornId,
			@Param("isNullCodi") boolean isNullCodi,
			@Param("codi") String codi,
			@Param("isNullNom") boolean isNullNom,
			@Param("nom") String nom,
			@Param("isNullNtiClasificacion") boolean isNullNtiClasificacion,
			@Param("ntiClasificacion") String ntiClasificacion,
			@Param("isNullRegistre_num") boolean isNullRegistre_num,
			@Param("expedientsTipusIds") List<Long> expedientsTipusIds,		
			Pageable pageable
			);
    

}
