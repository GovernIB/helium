package es.caib.helium.integracio.config.registre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.registre.RegistreService;
import es.caib.helium.integracio.service.registre.RegistreServiceRegWeb3Impl;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class RegistreConfig {
	
	@Autowired
	private Environment env;
	
	@Bean(name = "registreService")
	public RegistreService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.registre.service.rw3.class");
		try {
			var service = (RegistreService) Class.forName(pluginClass).getConstructor().newInstance();
			if (service instanceof RegistreServiceRegWeb3Impl) {
				
				var retorn = (RegistreServiceRegWeb3Impl) service;
				retorn.crearClient(env.getRequiredProperty("es.caib.helium.integracio.registre.service.ws.host"),
								env.getRequiredProperty("es.caib.helium.integracio.registre.service.regweb3.registro.entrada"),
								env.getRequiredProperty("es.caib.helium.integracio.registre.service.ws.usuari"),
								env.getRequiredProperty("es.caib.helium.integracio.registre.service.ws.password"));
			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de RegistreService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
}
