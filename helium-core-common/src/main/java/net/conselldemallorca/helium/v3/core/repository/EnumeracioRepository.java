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
			"   e.expedientTipus.id = :expedientTipusId " +
			"	and (:esNullFiltre = true or lower(e.codi) like lower('%'||:filtre||'%') or lower(e.nom) like lower('%'||:filtre||'%')) ")
	public Page<Enumeracio> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	public Enumeracio findByExpedientTipusAndCodi(ExpedientTipus expedientTipus, String codi);
}
