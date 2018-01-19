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

import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un estat que està emmagatzemat a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EstatRepository extends JpaRepository<Estat, Long> {

	public List<Estat> findByExpedientTipusOrderByOrdreAsc(
			ExpedientTipus expedientTipus);

	/** Consulta per expedient tipus i l'identificador. Té en compte l'herència. */
	@Query(	"from Estat e " +
			"where " +
			"  	e.id = :id " +
			"  	and (e.expedientTipus.id = :expedientTipusId " +
						// Heretats
			"			or e.expedientTipus.id = ( " + 
			"				select et.expedientTipusPare.id " + 
			"				from ExpedientTipus et " + 
			"				where et.id = :expedientTipusId)) ")
	public Estat findByExpedientTipusAndIdAmbHerencia(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("id") Long id);
	
	/** Consulta per expedient tipus id i el codi. No té en compte l'herència. */
	public Estat findByExpedientTipusIdAndCodi(
			Long expedientTipusId,
			String codi);
	
	/** Consulta per expedient tipus i el codi. Té en compte l'herència. */
	@Query(	"from Estat e " +
			"where " +
			"  	e.codi = :codi " +
			"	and e.id not in ( " + 
						// Llistat de sobreescrits
			"			select es.id " +
			"			from Estat ea " +
			"				join ea.expedientTipus et with et.id = :expedientTipusId, " +
			"				Estat es " +
			"			where " +
			"				es.codi = ea.codi " +
			"			 	and es.expedientTipus.id = et.expedientTipusPare.id) " +
			"  	and (e.expedientTipus.id = :expedientTipusId " +
						// Heretats
			"			or e.expedientTipus.id = ( " + 
			"				select et.expedientTipusPare.id " + 
			"				from ExpedientTipus et " + 
			"				where et.id = :expedientTipusId)) ")
	Estat findByExpedientTipusAndCodiAmbHerencia(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("codi") String codi);
	
	@Query("select max(e.ordre) "
			+ "from Estat e "
			+ "where e.expedientTipus.id=:expedientTipusId")
	public Integer getSeguentOrdre(@Param("expedientTipusId") Long expedientTipusId);

	@Query(	"from Estat e " +
			"where " +
			"	(:herencia = false " +
			"		or e.id not in ( " + 
						// Llistat de sobreescrits
			"			select es.id " +
			"			from Estat ea " +
			"				join ea.expedientTipus et with et.id = :expedientTipusId, " +
			"				Estat es " +
			"			where " +
			"				es.codi = ea.codi " +
			"			 	and es.expedientTipus.id = et.expedientTipusPare.id " +
			"		) " +
			"	) " +
			"  	and (e.expedientTipus.id = :expedientTipusId " +
						// Heretats
			"			or (:herencia = true " +
			"					and e.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) ) " +
			"	and (:esNullFiltre = true or lower(e.codi) like lower('%'||:filtre||'%') or lower(e.nom) like lower('%'||:filtre||'%')) " +
			"order by e.expedientTipus.id, e.ordre asc")
	Page<Estat> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			@Param("herencia") boolean herencia, 
			Pageable pageable);
	
	/** Troba tots els estats donat un tipus d'expedient sense tenir en compte l'herència. */
	@Query(	"from Estat e " +
			"where " +
			"	e.expedientTipus.id = :expedientTipusId " +
			"order by e.ordre asc")
	public List<Estat> findAll(
			@Param("expedientTipusId") Long expedientTipusId);	
	
	/** Troba tots els estats donat un tipus d'expedient, incloent els del tipus pare i excloent els ids passats per paràmetre.*/
	@Query(	"from Estat e " +
			"where " +
			"	e.id not in ( " + 
					// Llistat de sobreescrits
			"		select es.id " +
			"		from Estat ea " +
			"			join ea.expedientTipus et with et.id = :expedientTipusId, " +
			"			Estat es " +
			"		where " +
			"			es.codi = ea.codi " +
			"		 	and es.expedientTipus.id = et.expedientTipusPare.id " +
			"	) " +
			"  	and (e.expedientTipus.id = :expedientTipusId " +
						// Heretats
			"			or (e.expedientTipus.id = (select etp.expedientTipusPare.id from ExpedientTipus etp where etp.id = :expedientTipusId) ) ) " +
			"order by e.ordre asc")
	public List<Estat> findAllAmbHerencia(
			@Param("expedientTipusId") Long expedientTipusId);
	
	/** Recupera la informació de tots els registres sobreescrits.*/
	@Query( "select es " +
			"from Estat e " +
			"	join e.expedientTipus et with et.id = :expedientTipusId, " +
			"	Estat es " +
			"where " +
			"	es.codi = e.codi " +
			" 	and es.expedientTipus.id = et.expedientTipusPare.id ")
	List<Estat> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);	
}
