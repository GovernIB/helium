/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.ExpedientLog;

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

	Optional<ExpedientLog> findById(Long expedientLogId);


	@Query(	"select l.id, l.estat, l.targetId " +
			" from " +
			" ExpedientLog l " +
			" where " +
			" l.targetId in (:tasquesIds) " +
			" and l.id = " +
			" (select min(id) "+
			" from ExpedientLog "+
			" where targetId = l.targetId) " +
			" order by l.targetId")
	List<Object> findLogIdTasquesById(@Param("tasquesIds") Collection<String> tasquesIds);
}
