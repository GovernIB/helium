package es.caib.helium.monitor.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.jms.cua.Cua;
import es.caib.helium.monitor.domini.MessageEvent;
import es.caib.helium.monitor.exception.MonitorIntegracionsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuaListener extends ServiceBase {

	@Autowired
	private BddService bddService;

	@Transactional
	@JmsListener(destination = Cua.CUA_INTEGRACIONS)
	public void listen(MessageEvent msgEvent) throws MonitorIntegracionsException {

		if (msgEvent == null) {
			throwException("No es pot escoltar l'event", null);
		}

		if (msgEvent.getEvent() == null) {
			throwException("Missatge sense event", null);
		}

		var event = msgEvent.getEvent();
		log.info("Escoltant " + event.toString());
		try {
			event.setData(event.getData() != null ? event.getData() : new Date());
			var acc = bddService.save(event);
			log.info("Event guardat a la bdd " + acc.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			throwException("Error guardant l'event escoltat " + event.toString(), ex);
		}
	}
}
