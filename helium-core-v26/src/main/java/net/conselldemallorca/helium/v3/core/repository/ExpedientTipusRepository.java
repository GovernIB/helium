/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un tipus d'expedient que està emmagatzemat a dins
 * la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTipusRepository extends JpaRepository<ExpedientTipus, Long> {

	List<ExpedientTipus> findByEntorn(Entorn entorn);
	
	List<ExpedientTipus> findByEntornOrderByCodiAsc(Entorn entorn);

	ExpedientTipus findByEntornAndCodi(Entorn entorn, String codi);

	ExpedientTipus findById(Long expedientTipusId);

	/*Entitat findByCif(String cif);

	@Query(	"select " +
			"    eu " +
			"from " +
			"    EntitatUsuari eu " +
			"where " +
			"    eu.entitat.id = ?1 " +
			"and eu.usuari.nif = ?2")
	EntitatUsuari findUsuariAmbNif(Long id, String nif);*/

}
