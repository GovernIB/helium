package es.caib.helium.integracio.service.monitor;

import org.springframework.stereotype.Service;

import es.caib.helium.jms.events.IntegracioEvent;

@Service
public interface MonitorIntegracionsService {

	public boolean enviarEvent(IntegracioEvent event);
}
