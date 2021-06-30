package es.caib.helium.dada.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.caib.helium.dada.domain.Dada;

public interface DadaRepository extends MongoRepository<Dada, String> {
	
	public Optional<List<Dada>> findByExpedientId(Long expedientId);
	
	public Optional<List<Dada>> findByExpedientIdAndProcesId(Long expedientId, Long procesId);
	
	public Optional<Dada> findByExpedientIdAndCodi(Long expedientId, String codi);
	
	public Optional<Dada> findByExpedientIdAndProcesIdAndCodi(Long expedientId, Long procesId, String codi);

	public Optional<Dada> findByProcesIdAndCodi(Long procesId, String codi);

	public Optional<List<Dada>> findByProcesId(Long procesId);
}
