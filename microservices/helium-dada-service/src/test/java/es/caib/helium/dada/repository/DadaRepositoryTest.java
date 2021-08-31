package es.caib.helium.dada.repository;

import es.caib.helium.dada.enums.Tipus;
import es.caib.helium.dada.model.Dada;
import es.caib.helium.dada.model.Valor;
import es.caib.helium.dada.model.ValorRegistre;
import es.caib.helium.dada.model.ValorSimple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;

@ActiveProfiles("test")
@DataMongoTest
public class DadaRepositoryTest {
	
	@Autowired
	private DadaRepository dadaRepository;
	@Mock
	private Dada dadaMock;
	private List<Long> dadaIds;
	private List<Dada> dadesMock;
	private String codiMock;

	private final String codi = "codi";
	private final Long zeroLong = 0l;
	private final Long unLong = 1l;
	
	@BeforeEach
    public void beforeEach() {

		dadaIds = new ArrayList<>();
		dadesMock = new ArrayList<>();
		for (var foo=1;foo<2;foo++) {
			var dada = new Dada();
        	dada.setCodi(codi + foo);
        	dada.setExpedientId(unLong);
			dada.setMultiple(false);
			dada.setProcesId(unLong + "");
			dada.setTipus(Tipus.STRING);
        	if (foo == 1) {
        		dadaMock = dada;
        		codiMock = codi + foo;
        		for (var bar=0;bar<10;bar++) {
        			
        			List<Valor> valors = new ArrayList<>();
        			if (bar == 0) {
        				var valor = new ValorRegistre();
        				List<Dada> dades = new ArrayList<>();
        				var d = new Dada();
        				d.setCodi(codi + bar);
        				dades.add(d);
        				valor.setCamps(dades);
        			}
        			
        			var valor = new ValorSimple();
        			valor.setValor("valor" + bar);
        			valor.setValorText("valor" + bar);
        			valors.add(valor);
        			dada.setValor(valors);
        		}
        	}
        	dadaRepository.save(dada);
        	dadaIds.add(Long.parseLong(foo + ""));
        	dadesMock.add(dada);
		}
    }
	
	@AfterEach
	public void afterEach() {
		
		dadaRepository.deleteAll();
	}
	
	@Test
	@DisplayName("test_findByExpedientId")
	public void test_findByExpedientId() throws Exception {
		
		var dades = dadaRepository.findByExpedientId(unLong);
    	assertThat(dades).isNotNull().isNotEmpty();
    	assertArrayEquals(dades.get().toArray(), dadesMock.toArray());
	}

	@Test
	@DisplayName("test_findByExpedientId -_Not Found")
	public void test_findByExpedientId_notFound() throws Exception {
		
		var dades = dadaRepository.findByExpedientId(zeroLong);
		assertThat(dades).isNotNull().get().asList().isEmpty();
	}
	
	//-------------------
	
	@Test
	@DisplayName("test_findByExpedientIdAndProcesId")
	public void test_findByExpedientIdAndProcesId() throws Exception {
		
		var dades = dadaRepository.findByExpedientIdAndProcesId(unLong, unLong + "");
    	assertThat(dades).isNotNull().isNotEmpty();
    	assertArrayEquals(dades.get().toArray(), dadesMock.toArray());
	}
	
	@Test
	@DisplayName("test_findByExpedientIdAndProcesId - Not found")
	public void test_findByExpedientIdAndProcesId_notFound() throws Exception {
		
		var dades = dadaRepository.findByExpedientIdAndProcesId(zeroLong, unLong + "");
		assertThat(dades).isNotNull().get().asList().isEmpty();
		dades = dadaRepository.findByExpedientIdAndProcesId(unLong, zeroLong + "");
		assertThat(dades).isNotNull().get().asList().isEmpty();
	}

	//-------------------
	
	@Test
	@DisplayName("test_findByExpedientIdAndCodi")
	public void test_findByExpedientIdAndCodi() throws Exception {
		
		var dada = dadaRepository.findByExpedientIdAndCodi(unLong, codiMock);
		assertThat(dada).isNotNull().isNotEmpty().contains(dadaMock);
		assertArrayEquals(dada.get().getValor().toArray(), dadaMock.getValor().toArray());
	}

	@Test
	@DisplayName("test_findByExpedientIdAndCodi - Not found")
	public void test_findByExpedientIdAndCodi_notFound() throws Exception {
		
		var dada = dadaRepository.findByExpedientIdAndCodi(zeroLong, codiMock);
		assertThat(dada).isNotNull().isEmpty();
		dada = dadaRepository.findByExpedientIdAndCodi(unLong, codiMock + "_test");
		assertThat(dada).isNotNull().isEmpty();
	}
	
	//-------------------
	
	@Test
	@DisplayName("test_findByExpedientIdAndProcesIdAndCodi")
	public void test_findByExpedientIdAndProcesIdAndCodi() throws Exception {
	
		var dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(unLong, unLong + "", codiMock);
		assertThat(dada).isNotNull().isNotEmpty().contains(dadaMock);
		assertArrayEquals(dada.get().getValor().toArray(), dadaMock.getValor().toArray());
	}

	@Test
	@DisplayName("test_findByExpedientIdAndProcesIdAndCodi - Not found")
	public void test_findByExpedientIdAndProcesIdAndCodi_notFound() throws Exception {
		
		var dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(zeroLong, unLong + "", codiMock);
		assertThat(dada).isNotNull().isEmpty();
		dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(unLong, zeroLong + "", codiMock);
		assertThat(dada).isNotNull().isEmpty();
		dada = dadaRepository.findByExpedientIdAndProcesIdAndCodi(unLong, unLong + "", codiMock + "_test");
		assertThat(dada).isNotNull().isEmpty();
	}
}
