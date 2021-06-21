package es.caib.helium.integracio.service.monitor;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class MonitorIntegracionsServiceImpl implements MonitorIntegracionsService {

	@Autowired
	private CuaSender cuaSender;
	private String url;
	
	@Override
	public void enviarEvent(IntegracioEvent event) {
		
		try {
			log.info("Enviant acció al monitor d'integracions: " + url + " " + event.toString());
			cuaSender.sendEvent(event);
		} catch (Exception ex) {
			log.error("Error al enviar la acció al motor d'integracions: ", ex);
			ex.printStackTrace();
		}
	}
}
