/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;

/**
 * Implementació dels mètodes de AplicacioService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AplicacioServiceImpl implements AplicacioService {

	@Autowired
	private MetricRegistry metricRegistry;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String metrics() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(
					new MetricsModule(
							TimeUnit.SECONDS,
							TimeUnit.MILLISECONDS,
							false));
			return mapper.writeValueAsString(metricRegistry);
		} catch (Exception ex) {
			logger.error("Error al generar les mètriques de l'aplicació", ex);
			return "ERR";
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);

}
