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

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Interessat;

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
			"  and i.es_representant = false " +
			"  and   (:esNullFiltre = true " + 
			" 			or (lower(i.nom) like lower('%'||:filtre||'%')) " +
			" 			or (lower(i.llinatge1) like lower('%'||:filtre||'%')) " +
			" 			or (lower(i.llinatge2) like lower('%'||:filtre||'%')) " +
			" 			or (lower(i.documentIdent) like lower('%'||:filtre||'%')) " +
			" 			or (lower(i.dir3Codi) like lower('%'||:filtre||'%')) " +
			" 			or (lower(i.codi) like lower('%'||:filtre||'%'))) ")
	Page<ExpedientTipus> findByFiltrePaginat(
			@Param("expedient") Expedient expedient,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	
	List<Interessat> findByExpedient(
			Expedient expedient);
	
	Interessat findByCodi(String codi);
	
	Interessat findByCodiAndExpedient(
			String codi,
			Expedient expedient
			);
	
	List<Interessat> findByRepresentat(
			Interessat representat
			);
	
	List<Interessat> findByRepresentant(
			Interessat representant
			);
	
	@Query(	"from Interessat i " +
			"where " +
			"   i.expedient = :expedient " +
			"  and i.es_representant = true ") 
	List<Interessat> findRepresentantsByExpedient(
			@Param("expedient") Expedient expedient);
}
