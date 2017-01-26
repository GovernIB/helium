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

	Enumeracio findByEntornAndExpedientTipusAndCodi(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			String codi);

	Enumeracio findByEntornAndCodi(
			Entorn entorn,
			String codi);
	
	List<Enumeracio> findByEntorn(Entorn entorn);
	
	@Query(	"from " +
			"    Enumeracio enu " +
			"where " +
			"    enu.entorn = :entorn " +
			"and enu.codi = :codi " +
			"and enu.expedientTipus is null ")
	Enumeracio findByEntornAndCodiAndExpedientTipusNull(
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
			"   e.entorn.id = :entornId " +
			"	and ((e.expedientTipus.id = :expedientTipusId) or (e.expedientTipus is null and (:esNullExpedientTipusId = true or :incloureGlobals = true))) " +
			"	and (:esNullFiltre = true or lower(e.codi) like lower('%'||:filtre||'%') or lower(e.nom) like lower('%'||:filtre||'%')) ")
	public Page<Enumeracio> findByFiltrePaginat(
			@Param("entornId") Long entornId,
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("incloureGlobals") boolean incloureGlobals,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	public Enumeracio findByExpedientTipusAndCodi(ExpedientTipus expedientTipus, String codi);

	/** Troba les enumeracions per a un tipus d'expedient i també les globals de l'entorn i les ordena per nom.*/
	@Query(	"from Enumeracio e " +
			"where " +
			"   e.entorn.id = (select entorn.id from ExpedientTipus ex where ex.id = :expedientTipusId) " +
			"	and ((e.expedientTipus.id = :expedientTipusId) or (e.expedientTipus is null )) " +
			"order by " +
			"	nom")
	List<Enumeracio> findAmbExpedientTipusIGlobals(
			@Param("expedientTipusId") Long expedientTipusId);
}
