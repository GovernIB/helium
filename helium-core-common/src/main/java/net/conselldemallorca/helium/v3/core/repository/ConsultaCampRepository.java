/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un camp que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConsultaCampRepository extends JpaRepository<ConsultaCamp, Long> {

	@Query("select cc " +
			"from ConsultaCamp cc " +
			"where cc.consulta.id = :consultaId " +
			"and cc.tipus = :tipus " +
			"order by cc.ordre asc ")
	List<ConsultaCamp> findCampsConsulta(
			@Param("consultaId") Long consultaId, 
			@Param("tipus") TipusConsultaCamp tipus);
}
