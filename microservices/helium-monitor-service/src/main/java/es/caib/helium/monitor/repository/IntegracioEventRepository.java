package es.caib.helium.monitor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.caib.helium.monitor.domini.IntegracioEvent;

@Repository
public interface IntegracioEventRepository extends MongoRepository<IntegracioEvent, String>, IntegracioEventRepositoryCustom {
	
}
