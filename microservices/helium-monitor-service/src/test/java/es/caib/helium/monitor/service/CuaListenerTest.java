package es.caib.helium.monitor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.monitor.domini.Consulta;
import es.caib.helium.monitor.domini.IntegracioEvent;
import es.caib.helium.monitor.domini.MessageEvent;
import es.caib.helium.monitor.repository.IntegracioEventRepository;

@ExtendWith(MockitoExtension.class) 
public class CuaListenerTest {

	
	@Mock
	private CuaListener listener;
	@Mock
	private MessageEvent msgMock;
	@Mock
	private IntegracioEventRepository repository;
	@Mock
	private IntegracioEvent eventMock;
	@Mock
	private Consulta consultaMock;
	
	private List<IntegracioEvent> events;
	
	@BeforeEach
	private void beforeEach() {
		
		eventMock = new IntegracioEvent();
		eventMock.setCodi(CodiIntegracio.CUSTODIA);
		eventMock.setData(new Date());
		eventMock.setDescripcio("Event MOCK");
		eventMock.setEntornId(1l);
		eventMock.setEstat(EstatAccio.OK);
		
		msgMock = new MessageEvent();
		msgMock.setEvent(eventMock);
		
		events = new ArrayList<>();
		events.add(eventMock);
	}
	
	@Test
	@DisplayName("test_escoltarMissatge")
	public void test_escoltarMissatge() throws Exception {
		
		given(repository.findByFiltres(any(Consulta.class))).willReturn(events);
		listener.listen(msgMock);
		assertThat(repository.findByFiltres(consultaMock)).isNotEmpty().hasSize(1).element(0).isEqualTo(eventMock);
	}

//	@Test
//	@DisplayName("test_escoltarMissatge_missatgeNull")
//	public void test_escoltarMissatge_missatgeNull() throws Exception {
//		
//		assertThrows(MonitorIntegracionsException.class, () -> listener.listen(null));
//	}
//
//	@Test
//	@DisplayName("test_escoltarMissatge_eventNull")
//	public void test_escoltarMissatge_eventNull() throws Exception {
//		
//		msgMock.setEvent(null);
//		assertThrows(MonitorIntegracionsException.class, () -> listener.listen(msgMock));
//	}
}