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

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

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

	ExpedientTipus findByEntornAndCodi(Entorn entorn, String codi);

	ExpedientTipus findByEntornAndId(Entorn entorn, Long id);

	ExpedientTipus findById(Long expedientTipusId);

	/*Entitat findByCif(String cif);

	@Query(	"select " +
			"    eu " +
			"from " +
			"    EntitatUsuari eu " +
			"where " +
			"    eu.entitat.id = ?1 " +
			"and eu.usuari.nif = ?2")
	EntitatUsuari findUsuariAmbNif(Long id, String nif);*/
	
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


}
