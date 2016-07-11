package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

public interface AccioRepository extends JpaRepository<Accio, Long> {

	@Query("select a "
			+ "from Accio a "
			+ "where a.definicioProces = :definicioProces "
			+ "and a.oculta = false "
			+ "order by a.nom")
	public List<Accio> findAmbDefinicioProcesAndOcultaFalse(
			@Param("definicioProces") DefinicioProces definicioProces);

	public List<Accio> findByDefinicioProcesAndId(
			DefinicioProces definicioProces,
			Long id);
	
	Accio findByExpedientTipusAndCodi(ExpedientTipus expedientTipus, String codi);
	
	@Query(	"from Accio a " +
			"where " +
			"   a.expedientTipus.id = :expedientTipusId " +
			"	and (:esNullFiltre = true or lower(a.codi) like lower('%'||:filtre||'%') or lower(a.nom) like lower('%'||:filtre||'%')) ")
	Page<ExpedientTipus> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);	

}
