/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Termini;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	
	@Query(	"from Termini ter " +
			"where ter.definicioProces.id=:definicioProcesId ")
	List<Termini> findByDefinicioProcesId(
			@Param("definicioProcesId") Long definicioProcesId);
}
