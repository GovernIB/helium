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
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.Termini;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatRepository extends JpaRepository<Interessat, Long> {


	public Page<Interessat> findAll(
			Pageable pageable);
	
	
	@Query(	"from Interessat i " +
			"where " +
			"   i.expedient = :expedient " +
			"  and   (:esNullFiltre = true or lower(i.nom) like lower('%'||:filtre||'%') or lower(i.codi) like lower('%'||:filtre||'%')) ")
	Page<ExpedientTipus> findByFiltrePaginat(
			@Param("expedient") Expedient expedient,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	
	List<Interessat> findByExpedient(
			Expedient expedient);
	
	
	Interessat findByCodiAndExpedient(
			String codi,
			Expedient expedient
			);

}
