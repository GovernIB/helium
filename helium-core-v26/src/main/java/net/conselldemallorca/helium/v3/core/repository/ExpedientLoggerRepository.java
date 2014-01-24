/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un estat que està emmagatzemat a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientLoggerRepository extends JpaRepository<ExpedientLog, Long> {
	@Query("select r from ExpedientLog r where r.expedient.id=:expedientId order by r.data")
	List<ExpedientLog> findAmbExpedientIdOrdenatsPerData(@Param("expedientId") Long expedientId);

	@Query("select r from ExpedientLog r where r.iniciadorRetroces=:iniciadorId order by r.data")
	List<ExpedientLog> findAmbExpedientRetroceditIdOrdenatsPerData(@Param("iniciadorId") Long iniciadorId);
	
	@Query("select l from " +
			"    ExpedientLog l " +
			"where " +
			"    l.targetId=:targetId " +
			"order by data")
	List<ExpedientLog> findLogsTascaByIdOrdenatsPerData(@Param("targetId") String targetId);

	ExpedientLog findById(Long expedientLogId);
}
