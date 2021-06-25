package es.caib.helium.integracio.config.notificacio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.notificacio.NotificacioService;
import es.caib.helium.integracio.service.notificacio.NotificacioServiceNotibImpl;
import es.caib.notib.client.NotificacioRestClient;
import es.caib.notib.client.NotificacioRestClientFactory;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class NotificacioConfig {

	@Autowired
	private Environment env;
	
	@Bean(name = "notificacioService")
	public NotificacioService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.notificacio.service.class");
		try {
			var service = (NotificacioService) Class.forName(pluginClass).getConstructor().newInstance();
			if (service instanceof NotificacioServiceNotibImpl) {
				var retorn = (NotificacioServiceNotibImpl) service;
				retorn.setClient(crearClient());
			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de la NotificacioService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
	
	private NotificacioRestClient crearClient() {

		var url = env.getRequiredProperty("es.caib.helium.integracio.notificacio.url");
		var username = env.getRequiredProperty("es.caib.helium.integracio.notificacio.username");
		var password = env.getRequiredProperty("es.caib.helium.integracio.notificacio.password");
		var isBasicAuth = env.getProperty("es.caib.helium.integracio.notificacio.basicAuth", Boolean.class, false);
		var connectTimeout = env.getProperty("es.caib.helium.integracio.notificacio.connectTimeout", Integer.class, 20000);
		var readTimeout = env.getProperty("es.caib.helium.integracio.notificacio.readTimeout", Integer.class, 120000);
		
		return NotificacioRestClientFactory.getRestClient(url, username, password, isBasicAuth, connectTimeout, readTimeout);
	}
}
