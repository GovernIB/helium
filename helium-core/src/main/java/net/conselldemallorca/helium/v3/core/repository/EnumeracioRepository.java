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

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una enumeració que està emmagatzemat a dins la
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EnumeracioRepository extends JpaRepository<Enumeracio, Long> {

	/** Consulta per entorn, expedient tipus id i el codi. No té en compte l'herència. */
	Enumeracio findByEntornAndExpedientTipusAndCodi(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			String codi);

	/** Consulta per expedient tipus id i el codi. No té en compte l'herència. */
	Enumeracio findByExpedientTipusAndCodi(
			ExpedientTipus expedientTipus, 
			String codi);
	
	
	@Query(	"from " +
			"    Enumeracio e " +
			"where " +
			"    e.entorn.id = :entornId " +
			"and e.expedientTipus is null ")
	List<Enumeracio> findGlobals(@Param("entornId") Long entornId);
	
	@Query(	"from " +
			"    Enumeracio enu " +
			"where " +
			"    enu.entorn = :entorn " +
			"and enu.codi = :codi " +
			"and enu.expedientTipus is null ")
	Enumeracio findByEntornAndCodi(
			@Param("entorn") Entorn entorn,
			@Param("codi") String codi);
	
	@Query("select en from " +
			"    Enumeracio en " +
			"where " +
			"    en.expedientTipus.id=:expedientTipusId " +
			"order by " +
			"    nom")
	List<Enumeracio> findAmbExpedientTipus(
			@Param("expedientTipusId") Long expedientTipusId);	

	@Query(	"from Enumeracio e " +
			"where " +
			"	(:herencia = false " +
			"		or e.id not in ( " + 
						// Llistat de sobreescrits
			"			select es.id " +
			"			from Enumeracio ea " +
			"				join ea.expedientTipus et with et.id = :expedientTipusId, " +
			"				Enumeracio es " +
			"			where " +
			"				es.codi = ea.codi " +
			"			 	and es.expedientTipus.id = et.expedientTipusPare.id " +
			"		) " +
			"	) " +
			"   and e.entorn.id = :entornId " +
			"	and ((e.expedientTipus.id = :expedientTipusId) " +
						// Heretats
			"			or (:herencia = true " +
			"					and e.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId)) " +
			"			or (e.expedientTipus is null and (:esNullExpedientTipusId = true or :incloureGlobals = true))) " +
			"	and (:esNullFiltre = true or lower(e.codi) like lower('%'||:filtre||'%') or lower(e.nom) like lower('%'||:filtre||'%')) ")
	public Page<Enumeracio> findByFiltrePaginat(
			@Param("entornId") Long entornId,
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("incloureGlobals") boolean incloureGlobals,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			@Param("herencia") boolean herencia, 
			Pageable pageable);
	
	/** Troba les enumeracions per a un tipus d'expedient i també les globals de l'entorn i les ordena per nom.*/
	@Query(	"from Enumeracio e " +
			"where " +
			"   e.entorn.id = (select entorn.id from ExpedientTipus ex where ex.id = :expedientTipusId) " +
			"	and ((e.expedientTipus.id = :expedientTipusId) or (e.expedientTipus is null )) " +
			"order by " +
			"	nom")
	List<Enumeracio> findAmbExpedientTipusIGlobals(
			@Param("expedientTipusId") Long expedientTipusId);

	@Query( "select es " +
			"from Enumeracio e " +
			"	join e.expedientTipus et with et.id = :expedientTipusId, " +
			"	Enumeracio es " +
			"where " +
			"	es.codi = e.codi " +
			" 	and es.expedientTipus.id = et.expedientTipusPare.id ")
	List<Enumeracio> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);
}