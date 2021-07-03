/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.Registre;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir
 * i modificar la informació relativa a una persona que
 * està emmagatzemada a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreRepository extends JpaRepository<Registre, Long> {

	@Query(	"from Registre r " +
			"where r.expedientId=:expedientId " +
			"order by r.data")
	public List<Registre> findByExpedientId(
			@Param("expedientId") Long expedientId);

}
