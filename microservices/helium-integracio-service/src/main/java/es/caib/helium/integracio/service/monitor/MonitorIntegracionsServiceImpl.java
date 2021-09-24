package es.caib.helium.integracio.service.monitor;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.jms.events.IntegracioEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class MonitorIntegracionsServiceImpl implements MonitorIntegracionsService {

	@Autowired
	private CuaSender cuaSender;
	private String url; // TODO MS: EMPLENAR AMB LA URL DEL MS-MONITOR
	
	@Override
	public boolean enviarEvent(IntegracioEvent event) {
		
		try {
			log.info("Enviant acció al monitor d'integracions: " + url + " " + event.toString());
			cuaSender.sendEvent(event);
			return true;
		} catch (Exception ex) {
			log.error("Error al enviar la acció al motor d'integracions: ", ex);
			ex.printStackTrace();
			return false;
		}
	}
}
