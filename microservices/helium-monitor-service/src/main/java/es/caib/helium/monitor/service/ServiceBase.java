package es.caib.helium.monitor.service;

import com.netflix.servo.util.Strings;

import es.caib.helium.monitor.exception.MonitorIntegracionsException;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *Clase abstracta que encapsula metodes comuns per totes les classes tipus Service
 *
 */

@Slf4j
public abstract class ServiceBase {

	public void throwException(String msg, Exception ex) throws MonitorIntegracionsException {
		
		msg = Strings.isNullOrEmpty(msg)  ? "Error sense missatge" : msg;
		log.error(msg, ex);
		if (ex == null ) {
			throw new MonitorIntegracionsException(msg);
		}
		throw new MonitorIntegracionsException(msg, ex);
	}
}
