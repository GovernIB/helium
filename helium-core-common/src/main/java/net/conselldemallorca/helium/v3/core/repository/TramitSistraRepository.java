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

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.TramitSistra;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una notificació electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TramitSistraRepository extends JpaRepository<TramitSistra, Long> {
	List<TramitSistra> findByExpedientTipus(ExpedientTipus expedientTipus);
	
	List<TramitSistra> findBySistraTramitCodi(String sistraTramitCodi);
	
	@Query(	"from TramitSistra t " +
			"where " +
			"   t.expedientTipus.id = :expedientTipusId " +
			"	and (:esNullFiltre = true or lower(t.sistraTramitCodi) like lower('%'||:filtre||'%')) " +
			"order by t.tipus asc")
	Page<TramitSistra> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
}
