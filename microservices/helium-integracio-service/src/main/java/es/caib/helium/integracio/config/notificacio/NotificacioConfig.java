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
				retorn.crearClient(
						env.getRequiredProperty("es.caib.helium.integracio.notificacio.url"),
						env.getRequiredProperty("es.caib.helium.integracio.notificacio.username"),
						env.getRequiredProperty("es.caib.helium.integracio.notificacio.password"),
						env.getProperty("es.caib.helium.integracio.notificacio.basicAuth", Boolean.class, false),
						env.getProperty("es.caib.helium.integracio.notificacio.connectTimeout", Integer.class, 20000),
						env.getProperty("es.caib.helium.integracio.notificacio.readTimeout", Integer.class, 120000));
			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de la NotificacioService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
}
