/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Remesa;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una notificació electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RemesaRepository extends JpaRepository<Remesa, Long> {
	
	Remesa findByCodiAndExpedientTipus(String codi, ExpedientTipus expedientTipus);
	List<Remesa> findByEstat(DocumentEnviamentEstatEnumDto estat);
	
	@Query(	"select distinct n.expedient.id " +
			"from Notificacio n " +
			"where " +
			"   n.remesa = :remesa")
	List<Long> findExpedientIdsByRemesa(
			@Param("remesa") Remesa remesa);
}
