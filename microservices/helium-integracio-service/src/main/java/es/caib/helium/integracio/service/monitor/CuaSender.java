package es.caib.helium.integracio.service.monitor;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import es.caib.helium.jms.cua.Cua;
import es.caib.helium.jms.domini.MessageEvent;
import es.caib.helium.jms.events.IntegracioEvent;
import es.caib.helium.monitor.exception.MonitorIntegracionsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuaSender {

	private final JmsTemplate jmsTemplate;

    public void sendEvent(IntegracioEvent event) throws MonitorIntegracionsException {
    	try {
    		log.info("Enviant l'event " + event.toString() );
    		jmsTemplate.convertAndSend(Cua.CUA_INTEGRACIONS, new MessageEvent(event));
    	} catch (Exception ex) {
    		var error = "Error enviant l'event " + event.toString();
    		log.error(error, ex);
    		throw new MonitorIntegracionsException(error, ex);
    	}
    }
}
