package es.caib.helium.dada.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.caib.helium.dada.domain.ExpedientCapcalera;

public interface ExpedientRepository extends MongoRepository<ExpedientCapcalera, String> {

	public Optional<ExpedientCapcalera> findByExpedientId(Long expedientId);

	public void deleteByExpedientId(Long expedientId);
	
	public Optional<ExpedientCapcalera> findByEntornId(int entornId);

	public Optional<ExpedientCapcalera> findByIdAndProcesPrincipalId(String expedientId, Long procesId);
}
