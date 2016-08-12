/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una àrea que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AreaRepository extends JpaRepository<Area, Long> {

	Area findByEntornAndCodi(
			Entorn entorn,
			String codi);

	List<Area> findByEntornAndPareCodi(
			Entorn entorn,
			String pareCodi);

}
