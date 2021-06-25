package es.caib.helium.integracio.service.monitor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;

@ExtendWith(MockitoExtension.class) 
public class MonitorIntegracionsServiceTest {

	@Mock
	private MonitorIntegracionsService service;
	@Mock
	private IntegracioEvent eventMock;
	
	@BeforeEach
	public void beforeEach() {
		
		eventMock = new IntegracioEvent();
		eventMock.setCodi(CodiIntegracio.CUSTODIA);
		eventMock.setData(new Date());
		eventMock.setDescripcio("Event MOCK");
		eventMock.setEntornId(1l);
		eventMock.setTipus(TipusAccio.ENVIAMENT);
		eventMock.setEstat(EstatAccio.OK);
	}
	
	@Test
	@DisplayName("test_enviarEvent")
	public void test_enviarEvent() throws Exception {
		
		assertThat(service.enviarEvent(eventMock)).isEqualTo(true);
	}
}
