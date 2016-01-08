/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una enumeració que està emmagatzemat a dins la
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EnumeracioRepository extends JpaRepository<Enumeracio, Long> {

	Enumeracio findByEntornAndExpedientTipusAndCodi(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			String codi);

	Enumeracio findByEntornAndCodi(
			Entorn entorn,
			String codi);

	@Query(	"from " +
			"    Enumeracio enu " +
			"where " +
			"    enu.entorn = :entorn " +
			"and enu.codi = :codi " +
			"and enu.expedientTipus is null ")
	Enumeracio findByEntornAndCodiAndExpedientTipusNull(
			@Param("entorn") Entorn entorn,
			@Param("codi") String codi);

}
