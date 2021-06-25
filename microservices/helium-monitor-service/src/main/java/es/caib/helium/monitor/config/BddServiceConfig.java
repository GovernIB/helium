package es.caib.helium.monitor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.helium.monitor.exception.MonitorIntegracionsException;
import es.caib.helium.monitor.service.BddService;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.monitor")
@ComponentScan("es.caib.helium.monitor.service")
public class BddServiceConfig {

	@Autowired
	private Environment env;
	
	@Bean(name = "bddService")
	public BddService instanciarService() throws MonitorIntegracionsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.monitor.service.class");
		try {
			return (BddService) Class.forName(pluginClass).getConstructor().newInstance();
//			return new MongoBddService();
		} catch (Exception ex) {
			throw new MonitorIntegracionsException("Error al crear la instància de BddService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
}