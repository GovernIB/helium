package es.caib.helium.dada.repository;

import java.util.Date;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import es.caib.helium.dada.domain.Expedient;

@ActiveProfiles("test")
@DataMongoTest
public class MongoRepositoryTest {

	@Autowired
	private ExpedientRepository expedientRepository;
	
	@Before
    public void setUp() {

        var expedient = new Expedient();
        expedient.setExpedientId(1l);
        expedient.setEntornId(1l);
        expedient.setDataInici(new Date());
        expedient.setProcesPrincipalId(1l);
        expedientRepository.save(expedient);
    }

    @Test
    public void findByExpedientId() {
    	var expedient = expedientRepository.findByExpedientId(1l);
    	System.out.println("test");
//        assertThat(result).hasSize(1).extracting("name").contains("test");
    }

}
