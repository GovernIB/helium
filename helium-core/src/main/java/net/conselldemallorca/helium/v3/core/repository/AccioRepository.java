package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Accio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccioRepository extends JpaRepository<Accio, Long> {
	@Query("select a "
			+ "from Accio a "
			+ "where a.definicioProces.id=:definicioProcesId "
			+ "and a.oculta = false "
			+ "order by a.nom")
	public List<Accio> findVisiblesAmbDefinicioProces(
			@Param("definicioProcesId") Long definicioProcesId
	);
}
