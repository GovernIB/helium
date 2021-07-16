package es.caib.helium.dada.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class DadaRepositoryCustomImpl implements DadaRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DadaRepositoryCustomImpl(MongoTemplate mongoTemplate) {

        this.mongoTemplate = mongoTemplate;
    }

//    @Override
//    public List<Long> getDistinctExpedientIdsByProcesIds(List<String> procesIds) {
//
//        var criteria = new Criteria();
//        criteria.and(es.caib.helium.dada.enums.Dada.PROCES_ID.getCamp()).in(procesIds);
//        var query = new Query();
//        query.addCriteria(criteria);
//        return mongoTemplate.findDistinct(query, es.caib.helium.dada.enums.Dada.PROCES_ID.getCamp(), Dada.class, Long.class);
//    }
}
