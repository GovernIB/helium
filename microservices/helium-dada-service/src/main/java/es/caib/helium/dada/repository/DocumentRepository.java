package es.caib.helium.dada.repository;

import es.caib.helium.dada.model.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends MongoRepository<Document, String> {

    Optional<List<Document>> findByProcesId(String procesId);

    Optional<Document> findByProcesIdAndCodi(String procesId, String codi);

    Long deleteByProcesIdAndCodi(String procesId, String codi);
}
