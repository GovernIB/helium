package es.caib.helium.dada.repository;

import es.caib.helium.dada.model.Dada;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DadaRepository extends MongoRepository<Dada, String>, DadaRepositoryCustom {
	
	Optional<List<Dada>> findByExpedientId(Long expedientId);
	
	Optional<List<Dada>> findByExpedientIdAndProcesId(Long expedientId, String procesId);
	
	Optional<Dada> findByExpedientIdAndCodi(Long expedientId, String codi);
	
	Optional<Dada> findByExpedientIdAndProcesIdAndCodi(Long expedientId, String procesId, String codi);

	Optional<Dada> findByProcesIdAndCodi(String procesId, String codi);

	Optional<List<Dada>> findByProcesId(String procesId);

	@Query(value = "{procesId: {$in: ?0}}", fields =  "{expedientId: 1}")
	Optional<List<Dada>> findByProcesIds(List<Long> procesIds);
}
