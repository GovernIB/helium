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
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Termini;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un termini que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiRepository extends JpaRepository<Termini, Long> {

	Termini findByDefinicioProcesAndCodi(
			DefinicioProces definicioProces,
			String codi);
	
	@Query(	"select t from " +
			"    Termini t " +
			"where " +
			"    t.definicioProces.id = :definicioProcesId")
	List<Termini> findByDefinicioProcesId(@Param("definicioProcesId") Long definicioProcesId);

	@Query(	"select t from " +
			"    Termini t " +
			"where " +
			"	t.expedientTipus.id = :expedientTipusId " +
			"order by t.codi asc ")
	List<Termini> findByExpedientTipus(@Param("expedientTipusId") Long expedientTipusId);
	
	@Query(	"select t from " +
			"    Termini t " +
			"where " +
			"	(t.id not in ( " + 
						// Llistat de sobreescrits
			"			select ts.id " +
			"			from Termini ta " +
			"				join ta.expedientTipus et with et.id = :expedientTipusId, " +
			"				Termini ts " +
			"			where " +
			"				ts.codi = ta.codi " +
			"			 	and ts.expedientTipus.id = et.expedientTipusPare.id " +
			"		) " +
			"	) " +
			"   and (t.expedientTipus.id = :expedientTipusId " + 
						// Heretats
			"			or (t.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) )) " +
			"order by t.codi asc ")
	List<Termini> findByExpedientTipusAmbHerencia(@Param("expedientTipusId") Long expedientTipusId);

	Termini findByExpedientTipusAndCodi(ExpedientTipus expedientTipus, String codi);

	@Query(	"from Termini t " +
			"where " +
			"	(:herencia = false " +
			"		or t.id not in ( " + 
						// Llistat de sobreescrits
			"			select ts.id " +
			"			from Termini ta " +
			"				join ta.expedientTipus et with et.id = :expedientTipusId, " +
			"				Termini ts " +
			"			where " +
			"				ts.codi = ta.codi " +
			"			 	and ts.expedientTipus.id = et.expedientTipusPare.id " +
			"		) " +
			"	) " +
			"   and (t.expedientTipus.id = :expedientTipusId " +
						// Heretats
			"			or (:herencia = true " +
			"					and t.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) " + 
			"			or  t.expedientTipus.id is null) " +
			"   and (t.definicioProces.id = :definicioProcesId or t.definicioProces.id is null) " +
			"	and (:esNullFiltre = true or lower(t.codi) like lower('%'||:filtre||'%') or lower(t.nom) like lower('%'||:filtre||'%')) ")
	Page<Termini> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			@Param("herencia") boolean herencia,
			Pageable pageable);	
	
	/** Recupera la informació de tots els registres sobreescrits.*/
	@Query( "select ts " +
			"from Termini t " +
			"	join t.expedientTipus et with et.id = :expedientTipusId, " +
			"	Termini ts " +
			"where " +
			"	ts.codi = t.codi " +
			" 	and ts.expedientTipus.id = et.expedientTipusPare.id ")
	List<Termini> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);
}
