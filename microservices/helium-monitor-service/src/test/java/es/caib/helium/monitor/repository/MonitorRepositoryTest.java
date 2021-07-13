package es.caib.helium.monitor.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.monitor.domini.Consulta;
import es.caib.helium.monitor.domini.IntegracioEvent;
import io.netty.util.internal.ThreadLocalRandom;

@ActiveProfiles("test")
@DataMongoTest
public class MonitorRepositoryTest {

	@Autowired
	private IntegracioEventRepository repository;
	
	private Consulta consultaMock;
	private List<IntegracioEvent> eventsMock;
	private IntegracioEvent eventMock;
	private int size;
	
	@BeforeEach 
	public void beforeEach() throws ParseException {
		
		var events = new ArrayList<>();
		var codis = CodiIntegracio.values();
		for (var foo = 0; foo < 10000; foo++) {
			var event = new IntegracioEvent();
			
			if (foo%10 == 0) {
				event.setCodi(CodiIntegracio.ARXIU);
				var date = new GregorianCalendar(2020, Calendar.NOVEMBER, 11).getTime();
				event.setData(date);
				event.setDescripcio("Event Mock");
				event.setEntornId(1l);
				event.setEstat(EstatAccio.ERROR);
				eventMock = event;
				repository.save(event);
				events.add(event);
				size++;
				continue;
			}
			
			event.setCodi(codis[ThreadLocalRandom.current().nextInt(0, codis.length -1)]);
			event.setData(new Date());
			event.setDescripcio("Event");
			event.setEntornId(2l);
			event.setEstat(EstatAccio.ERROR);
			repository.save(event);
		}
		
		crearConsulta();
	}
	
	private void crearConsulta() {

		consultaMock = new Consulta();
		consultaMock.setCodi(CodiIntegracio.ARXIU);
		var date = new GregorianCalendar(2020, Calendar.OCTOBER, 11).getTime();
		consultaMock.setDataInicial(date);
		date = new GregorianCalendar(2020, Calendar.DECEMBER, 31).getTime();
		consultaMock.setDataFinal(date);
		consultaMock.setDescripcio("Mock");
		consultaMock.setEntornId(1l);
		consultaMock.setError(true);
	}
	
	@AfterEach
	public void afterEach() {
		
		repository.deleteAll();
	}
	
	@Test
	@DisplayName("test_findByFiltres")
	public void testFindByFiltres() throws Exception {
		
		var events = repository.findByFiltres(consultaMock);
		assertThat(events).isNotNull().isNotEmpty().hasSize(size);
		for (var event : events) {
			Assert.assertTrue(new ReflectionEquals(event, "id").matches(eventMock));
		}
	}

	@Test
	@DisplayName("testFindByFiltres_paginat")
	public void testFindByFiltres_paginat() throws Exception {
		
		consultaMock.setPage(1);
		consultaMock.setSize(10);
		var events = repository.findByFiltres(consultaMock);
		assertThat(events).isNotNull().isNotEmpty().hasSize(10);
		for (var event : events) {
			Assert.assertTrue(new ReflectionEquals(event, "id").matches(eventMock));
		}
	}

	@Test
	@DisplayName("test_findByFiltres_noResultats")
	public void testFindByFiltres_noResultats() throws Exception {
		
		consultaMock.setEntornId(10l);
		var events = repository.findByFiltres(consultaMock);
		assertThat(events).isNotNull().isEmpty();
	}

	@Test
	@DisplayName("test_findByFiltres_consultaErronia")
	public void testFindByFiltres_consultaErronia() throws Exception {
		
		consultaMock.setEntornId(null);
		assertThrows(Exception.class, () -> repository.findByFiltres(consultaMock));
	}

	@Test
	@DisplayName("test_findByFiltres_consultaNull")
	public void testFindByFiltres_consultaNull() throws Exception {
		
		consultaMock = null;
		var events = repository.findByFiltres(consultaMock);
		assertThat(events).isNotNull().isEmpty();
	}
}
