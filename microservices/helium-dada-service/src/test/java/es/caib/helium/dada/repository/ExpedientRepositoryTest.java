package es.caib.helium.dada.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import es.caib.helium.dada.domain.Expedient;

@ActiveProfiles("test")
@DataMongoTest
public class ExpedientRepositoryTest {

	@Autowired
	private ExpedientRepository expedientRepository;
	@Mock
	private Expedient expedientMock;
	private List<Long> expedientIds;
	private List<Expedient> expedients;
	private final Long zeroLong = 0l;
	private final Long unLong = 1l;
	
	@BeforeEach
    public void beforeEach() {

		expedientIds = new ArrayList<>();
		expedients = new ArrayList<>();
		for (var foo=1;foo<1000;foo++) {
			var expedient = new Expedient();
        	expedient.setExpedientId(Long.parseLong(foo + ""));
        	expedient.setTipusId(unLong);
        	expedient.setEntornId(unLong);
        	expedient.setDataInici(new Date());
        	expedient.setProcesPrincipalId(unLong);
        	expedientRepository.save(expedient);
        	expedientIds.add(Long.parseLong(foo + ""));
        	expedients.add(expedient);
        	if (foo == 1) {
        		expedientMock = expedient;
        	}
		}
    }
	
	@AfterEach
	public void afterEach() {
		
		expedientRepository.deleteAll();
	}

    @Test
    @DisplayName("test_findByExpedientId")
    public void test_findByExpedientId_success() throws Exception {
    	
    	var expedient = expedientRepository.findByExpedientId(unLong);
    	assertThat(expedient).isNotNull();
    	assertExpedient(expedientMock, expedient.get());
    }

    @Test
    @DisplayName("test_findByExpedientId - Not found")
    public void test_findByExpedientId_notFound() throws Exception {
    	
    	var expedient = expedientRepository.findByExpedientId(zeroLong);
    	assertThat(expedient).isNotNull().isEmpty();
    }
    
    //-------------------
    
    @Test
    @DisplayName("test_findByExpedients")
    public void test_findByExpedients_success() throws Exception {
    	
    	var exps = expedientRepository.findByExpedients(expedientIds);
    	assertThat(exps).isNotEmpty();
    	assertArrayEquals(exps.get().toArray(), expedients.toArray());
   }

    @Test
    @DisplayName("test_findByExpedients - Not found")
    public void test_findByExpedients_notFound() throws Exception {
    	
    	expedientIds = new ArrayList<>();
    	assertThat(expedientRepository.findByExpedients(expedientIds)).isNotEmpty().get().asList().isEmpty();
    }
    
    //------------------

    @Test
    @DisplayName("test_deleteByExpedientId")
    public void test_deleteByExpedientId() throws Exception {
    	
    	assertThat(expedientRepository.deleteByExpedientId(unLong)).isEqualTo(1l);
    	assertThat(expedientRepository.findByExpedientId(unLong)).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("test_deleteByExpedientId - Not found")
    public void test_deleteByExpedientId_notFound() throws Exception {

    	assertThat(expedientRepository.deleteByExpedientId(zeroLong)).isEqualTo(0l);
    }
    
    //------------------
    
    @Test
    @DisplayName("test_findByEntornId")
    public void test_findByEntornId() throws Exception {
    	
    	var exps = expedientRepository.findByEntornId(unLong.intValue());
    	assertThat(exps).isNotNull().isNotEmpty();
    	assertArrayEquals(exps.get().toArray(), expedients.toArray());
    }

    @Test
    @DisplayName("test_findByEntornId")
    public void test_findByEntornId_notFound() throws Exception {
    	
    	var exp = expedientRepository.findByEntornId(zeroLong.intValue());
    	assertThat(exp).isNotNull().get().asList().isEmpty();
    }

    //------------------
    
    private void assertExpedient(Expedient expedient, Expedient trobat) throws Exception {
    	
        assertAll("Comprovar dades de la capçalera de l'expedient",
                () -> assertEquals(expedient.getExpedientId(), trobat.getExpedientId(), "id incorrecte"),
                () -> assertEquals(expedient.getEntornId(), trobat.getEntornId(), "entornId incorrecte"),
                () -> assertEquals(expedient.getTipusId(), trobat.getTipusId(), "tipusId incorrecte"),
                () -> assertEquals(expedient.getNumero(), trobat.getNumero(), "numero incorrecte"),
                () -> assertEquals(expedient.getTitol(), trobat.getTitol(), "titol incorrecte"),
                () -> assertEquals(expedient.getProcesPrincipalId(), trobat.getProcesPrincipalId(), "procesPrincipalId incorrecte"),
                () -> assertEquals(expedient.getEstatId(), trobat.getEstatId(), "estatId incorrecte"),
                () -> assertEquals(expedient.getDataInici(), trobat.getDataInici(), "dataInici incorrecte"),
                () -> assertEquals(expedient.getDataFi(), trobat.getDataFi(), "dataFi incorrecte")
        );
    }
}
