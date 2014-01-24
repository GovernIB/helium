/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;

import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una reassignació que està emmagatzemat a dins la
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ReassignacioRepository extends JpaRepository<Reassignacio, Long> {

	@Query("select re from Reassignacio re where re.usuariOrigen = ?1 and re.dataInici <= ?2 and re.dataFi >= ?2")
	public Reassignacio findActivesByUsuariOrigenAndData(
			String usuariOrigen,
			Date ara);

}
