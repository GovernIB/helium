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

import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un domini que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DominiRepository extends JpaRepository<Domini, Long> {

	Domini findByExpedientTipusAndCodi(
			ExpedientTipus expedientTipus,
			String codi);

	@Query(	"from " +
			"    Domini d " +
			"where " +
			"    d.entorn = :entorn " +
			"and d.codi = :codi " +
			"and d.expedientTipus is null ")
	Domini findByEntornAndCodi(
			@Param("entorn") Entorn entorn,
			@Param("codi") String codi);
	
	List<Domini> findByEntorn(
			Entorn entorn);

	@Query("select e from " +
			"    Domini e " +
			"where " +
			"    e.expedientTipus.id = :expedientTipusId " +
			"order by " +
			"    nom")
	List<Domini> findAmbExpedientTipus(
			@Param("expedientTipusId") Long expedientTipusId);

	List<Domini> findByExpedientTipusId(Long expedientTipusId, Pageable springDataPageable);

	@Query(	"from Domini d " +
			"where " +
			"   d.entorn.id = :entornId " +
			"	and ((d.expedientTipus.id = :expedientTipusId) or (d.expedientTipus is null and (:esNullExpedientTipusId = true or :incloureGlobals = true))) " +
			"	and (:esNullFiltre = true or lower(d.codi) like lower('%'||:filtre||'%') or lower(d.nom) like lower('%'||:filtre||'%')) ")
	Page<Domini> findByFiltrePaginat(
			@Param("entornId") Long entornId,
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("incloureGlobals") boolean incloureGlobals,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	/** Troba les enumeracions per a un tipus d'expedient i també les globals de l'entorn i les ordena per nom.*/
	@Query(	"from Domini d " +
			"where " +
			"   d.entorn.id = (select entorn.id from ExpedientTipus ex where ex.id = :expedientTipusId) " +
			"	and ((d.expedientTipus.id = :expedientTipusId) or (d.expedientTipus is null )) " +
			"order by " +
			"	nom")
	List<Domini> findAmbExpedientTipusIGlobals(
			@Param("expedientTipusId") Long expedientTipusId);
	
	/** Troba per entorn i codi global amb expedient tipus null. */
	@Query(	"from " +
			"    Domini d " +
			"where " +
			"    d.entorn = :entorn " +
			"and d.codi = :codi " +
			"and d.expedientTipus is null ")
	Domini findByEntornAndCodiAndExpedientTipusNull(
			@Param("entorn") Entorn entorn,
			@Param("codi") String codi);
	
	/** Troba per entorn i codi global amb expedient tipus null. */
	@Query(	"from " +
			"    Domini d " +
			"where " +
			"    d.entorn.id = :dominiId " +
			"and d.expedientTipus is null ")
	List<Domini> findGlobals(
			@Param("dominiId") Long dominiId);
}
