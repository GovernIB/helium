package es.caib.helium.dada.repository;

import es.caib.helium.dada.model.Expedient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpedientRepository extends MongoRepository<Expedient, String>, ExpedientRepositoryCustom {

	@Query("{expedientId: {$in: ?0}}")
	Optional<List<Expedient>> findByExpedients(List<Long> expedients);

	Optional<Expedient> findByExpedientId(Long expedientId);

	Optional<Expedient> findByExpedientIdAndProcesPrincipalId(Long expedientId, String procesPrincipalId);

	Long deleteByExpedientId(Long expedientId);

	Optional<List<Expedient>> findByEntornId(int entornId);

	Optional<Expedient> findByProcesPrincipalId(String procesId);
}
