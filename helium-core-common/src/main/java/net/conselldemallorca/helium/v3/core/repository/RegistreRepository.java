/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Registre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
