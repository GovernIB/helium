/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
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
//	List<Termini> findByDefinicioProcesId(Long definicioProcesId);
	List<Termini> findByExpedientTipusId(Long expedientTipusId, Pageable pageable);
}
