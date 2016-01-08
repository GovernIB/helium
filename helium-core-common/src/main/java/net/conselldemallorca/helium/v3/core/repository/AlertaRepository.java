/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Alerta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una alerta que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AlertaRepository extends JpaRepository<Alerta, Long> {	
	@Query(	"from " +
			"    Alerta al " +
			"where " +
			"    al.terminiIniciat.id=:id " +
			"and al.dataEliminacio is null")
	List<Alerta> findActivesAmbTerminiIniciatId(@Param("id") Long id);
}
