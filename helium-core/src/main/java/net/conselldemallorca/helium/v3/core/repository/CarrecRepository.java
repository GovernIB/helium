/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.Carrec;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un càrrec que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CarrecRepository extends JpaRepository<Carrec, Long> {

	Carrec findByEntornAndCodi(
			Entorn entorn,
			String codi);

	Carrec findByEntornAndAreaAndCodi(
			Entorn entorn,
			Area area,
			String codi);
	Carrec findByEntornAndAreaCodiAndCodi(
			Entorn entorn,
			String areaCodi,
			String codi);

	List<Carrec> findByEntornAndPersonaCodi(
			Entorn entorn,
			String personaCodi);

}
