package es.caib.helium.integracio.config.tramitacio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.tramitacio.TramitacioService;
import es.caib.helium.integracio.service.tramitacio.TramitacioServiceSistraImpl;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class TramitacioConfig {

	@Autowired
	private Environment env;
	
	@Bean(name = "tramitacioService")
	public TramitacioService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.tramitacio.service.class");
		try {
			var service = (TramitacioService) Class.forName(pluginClass).getConstructor().newInstance();
			if (service instanceof TramitacioServiceSistraImpl) {
				var retorn = (TramitacioServiceSistraImpl) service;
				retorn.crearClientBantel(
						env.getRequiredProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.url"),
						env.getRequiredProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.username"),
						env.getRequiredProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.password"),
						env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.authType", String.class, null),
						env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientGenerateTimestamp", Boolean.class, false),
						env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientLogCalls", Boolean.class, false),
						env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientDisableCnCheck", Boolean.class, false),
						env.getProperty("es.caib.helium.integracio.tramitacio.bantel.sistra.wsClientDisableChunked", Boolean.class, false));
			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de TramitacioService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
}
