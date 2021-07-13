package es.caib.helium.monitor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.monitor.domini.Consulta;
import es.caib.helium.monitor.domini.IntegracioEvent;
import es.caib.helium.monitor.domini.PagedList;
import es.caib.helium.monitor.exception.MonitorIntegracionsException;
import es.caib.helium.monitor.repository.IntegracioEventRepository;
import io.netty.util.internal.ThreadLocalRandom;

@ExtendWith(MockitoExtension.class)
public class MongoBddServiceTest {

	@Mock
	private MongoBddService mongoBddService;
	@Mock
	private IntegracioEventRepository repository;
	@Mock
	private Consulta consultaMock;
	private List<IntegracioEvent> events;
	private List<IntegracioEvent> eventsFiltrats;
	private IntegracioEvent eventMock;
	private int size;
	private int sizeTotal;
	
	@BeforeEach
	public void beforeEach() {
		
		size = 0;
		sizeTotal = 0;
		events = new ArrayList<>();
		eventsFiltrats = new ArrayList<>();
		var codis = CodiIntegracio.values();
		for (var foo = 0; foo < 100000; foo++) {
			var event = new IntegracioEvent();
			
			if (foo%10 == 0) {
				event.setCodi(CodiIntegracio.ARXIU);
				var date = new GregorianCalendar(2020, Calendar.NOVEMBER, 11).getTime();
				event.setData(date);
				event.setDescripcio("Event Mock");
				event.setEntornId(1l);
				event.setEstat(EstatAccio.ERROR);
				eventMock = event;
				events.add(event);
				eventsFiltrats.add(event);
				size++;
				sizeTotal++;
				continue;
			}
			
			event.setCodi(codis[ThreadLocalRandom.current().nextInt(0, codis.length -1)]);
			event.setData(new Date());
			event.setDescripcio("Event");
			event.setEntornId(2l);
			event.setEstat(EstatAccio.ERROR);
			events.add(event);
			sizeTotal++;
		}
	}
	
	//-----------------------------------------------------------
	
	@Test
	@DisplayName("test_findByFiltres")
	public void test_findByFiltres() throws Exception {
		
		given(mongoBddService.findByFiltres(any(Consulta.class))).willReturn(eventsFiltrats);
		assertThat(mongoBddService.findByFiltres(consultaMock)).isNotEmpty();
	}

	@Test
	@DisplayName("test_findByFiltres_noResultats")
	public void test_findByFiltres_noResultats() throws Exception {
		
		given(mongoBddService.findByFiltres(any(Consulta.class))).willReturn(new ArrayList<IntegracioEvent>());
		assertThat(mongoBddService.findByFiltres(consultaMock)).isEmpty();
	}
	@Test
	@DisplayName("test_findByFiltres_exception")
	public void test_findByFiltres_exception() throws Exception {
		
		given(mongoBddService.findByFiltres(any(Consulta.class))).willThrow(MonitorIntegracionsException.class);
		assertThrows(MonitorIntegracionsException.class, () -> mongoBddService.findByFiltres(consultaMock));
	}

	//-----------------------------------------------------------

	@Test
	@DisplayName("test_findByFiltres_paginats")
	public void test_findByFiltresPaginats() throws Exception {
		
		var pageable = PageRequest.of(consultaMock.getPage(), eventsFiltrats.size());
		var resultat = new PagedList<IntegracioEvent>(eventsFiltrats, pageable, eventsFiltrats.size());
		
		given(mongoBddService.findByFiltresPaginat(any(Consulta.class))).willReturn(resultat);
		var pagedList = mongoBddService.findByFiltresPaginat(consultaMock);
		assertThat(pagedList).isNotNull().isEqualTo(resultat);
	}

	@Test
	@DisplayName("test_findByFiltres_paginats_noResultat")
	public void test_findByFiltresPaginats_noResultat() throws Exception {
		
		var pageable = PageRequest.of(consultaMock.getPage(), eventsFiltrats.size());
		var resultat = new PagedList<IntegracioEvent>(new ArrayList<IntegracioEvent>(), pageable, eventsFiltrats.size());
		
		given(mongoBddService.findByFiltresPaginat(any(Consulta.class))).willReturn(resultat);
		var pagedList = mongoBddService.findByFiltresPaginat(consultaMock);
		assertThat(pagedList).isNotNull().isEmpty();
	}
	
	@Test
	@DisplayName("test_findByFiltres_exception")
	public void test_findByFiltresPaginats_exception() throws Exception {
		
		given(mongoBddService.findByFiltres(any(Consulta.class))).willThrow(MonitorIntegracionsException.class);
		assertThrows(MonitorIntegracionsException.class, () -> mongoBddService.findByFiltres(consultaMock));
	}
}
