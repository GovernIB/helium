package es.caib.helium.dada.repository;

import es.caib.helium.dada.model.Dada;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@Slf4j
public class DadaRepositoryCustomImpl implements DadaRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DadaRepositoryCustomImpl(MongoTemplate mongoTemplate) {

        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Long> getExpedientIdByProcesIds(List<String> procesIds) {

        var criteria = new Criteria();
        criteria.and(es.caib.helium.dada.enums.Dada.PROCES_ID.getCamp()).in(procesIds);
        var query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.findDistinct(query, es.caib.helium.dada.enums.Dada.EXPEDIENT_ID.getCamp(), Dada.class, Long.class);
    }

    @Override
    public void updateDada(Dada dada) {

        var query = Query.query(Criteria.where("id_").is(dada.getId()));
        var result = mongoTemplate.updateFirst(query, Update.update("valor", dada.getValor()), Dada.class);
        log.info("Resultat " + result);
    }

}
