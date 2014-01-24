/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Consulta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un camp que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

	Consulta findById(Long id);
	
	@Query("select c from Consulta c, Expedient e where c.id = e.numero and e.id = :expedientId")
	Consulta findByIdExpedient(@Param("expedientId") Long id);

	@Query("select c from Consulta c where entorn.id = :entornId and expedientTipus.id = :expedientTipusId order by ordre")
	List<Consulta> findConsultesAmbEntornIExpedientTipusOrdenat(
			@Param("entornId") Long entornId, 
			@Param("expedientTipusId") Long expedientTipusId);
}
