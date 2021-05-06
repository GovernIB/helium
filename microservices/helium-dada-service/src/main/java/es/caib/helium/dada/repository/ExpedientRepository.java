package es.caib.helium.dada.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import es.caib.helium.dada.domain.Expedient;

@Repository
public interface ExpedientRepository extends MongoRepository<Expedient, String>, ExpedientRepositoryCustom {

	@Query("{expedientId: {$in: ?0}}")
	public Optional<List<Expedient>> findByExpedients(List<Long> expedients);

	public Optional<Expedient> findByExpedientId(Long expedientId);

	public Long deleteByExpedientId(Long expedientId);

	public Optional<List<Expedient>> findByEntornId(int entornId);
}
