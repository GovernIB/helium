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

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un camp que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CampRepository extends JpaRepository<Camp, Long> {

	Camp findByDefinicioProcesAndCodi(
			DefinicioProces definicioProces,
			String codi);
	
	Camp findByExpedientTipusAndCodi(
			ExpedientTipus expedientTipus, 
			String codi);
	
	List<Camp> findByDefinicioProcesOrderByCodiAsc(DefinicioProces definicioProces);

	Camp findById(Long registreEsborrarId);
	
	@Query(	"from Camp c " +
			"where " +
			"   (c.expedientTipus.id = :expedientTipusId or c.expedientTipus.id is null) " +
			"   and (c.definicioProces.id = :definicioProcesId or c.definicioProces.id is null) " +
			"	and ((:totes = true) or (:esNullAgrupacioId = true and c.agrupacio.id = null) or (:esNullAgrupacioId = false and c.agrupacio.id = :agrupacioId)) " +
			"	and (:esNullFiltre = true or lower(c.codi) like lower('%'||:filtre||'%') or lower(c.etiqueta) like lower('%'||:filtre||'%')) ")
	Page<Camp> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("totes") boolean totes,
			@Param("esNullAgrupacioId") boolean esNullAgrupacioId,
			@Param("agrupacioId") Long agrupacioId,		
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	/** Consulta el següent valor per a ordre dins d'una agrupació. */
	@Query(	"select coalesce( max( c.ordre), -1) + 1 " +
			"from Camp c " +
			"where " +
			"    c.agrupacio.id = :agrupacioId " )
	Integer getNextOrdre(@Param("agrupacioId") Long agrupacioId);
	
	List<Camp> findByAgrupacioIdOrderByOrdreAsc(Long campAgrupacioId);
	
	List<Camp> findByExpedientTipusAndTipus(
			ExpedientTipus expedientTipus,
			TipusCamp estat);
	
	List<Camp> findByExpedientTipusOrderByCodiAsc(
			ExpedientTipus expedientTipus);
	
	List<Camp> findByDefinicioProcesAndTipus(
			DefinicioProces definicioProces,
			TipusCamp estat);
	
	/** Compta el número de validacions per a cada camp passat per la llista d'identificadors. */
	@Query(	"select " +
			"    id, " +
			"    size(validacions) " +
			"from " +
			"   Camp c " +
			"where " +
			"   (c.expedientTipus.id = :expedientTipusId or c.expedientTipus.id is null) " +
			"   and (c.definicioProces.id = :definicioProcesId or c.definicioProces.id is null) " +
			"	and ((:totes = true) or (:esNullAgrupacioId = true and c.agrupacio.id = null) or (:esNullAgrupacioId = false and c.agrupacio.id = :agrupacioId)) " +
			"group by id ")
	List<Object[]> countValidacions(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("totes") boolean totes,
			@Param("esNullAgrupacioId") boolean esNullAgrupacioId, 
			@Param("agrupacioId") Long agrupacioId);

	/** Compta el número de registres per a cada camp passat per la llista d'identificadors. */
	@Query(	"select " +
			"    id, " +
			"    size(registreMembres) " +
			"from " +
			"   Camp c " +
			"where " +
			"   (c.expedientTipus.id = :expedientTipusId or c.expedientTipus.id is null) " +
			"   and (c.definicioProces.id = :definicioProcesId or c.definicioProces.id is null) " +
			"   and c.tipus = net.conselldemallorca.helium.core.model.hibernate.Camp$TipusCamp.REGISTRE " +
			"	and ((:totes = true) or (:esNullAgrupacioId = true and c.agrupacio.id = null) or (:esNullAgrupacioId = false and c.agrupacio.id = :agrupacioId)) " +
			"group by id ")
	List<Object[]> countMembres(
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("totes") boolean totes,
			@Param("esNullAgrupacioId") boolean esNullAgrupacioId, 
			@Param("agrupacioId") Long agrupacioId);
}
