package es.caib.helium.integracio.config.firma;

import java.util.Properties;

import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaEnServidorSimpleJersey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.firma.FirmaService;
import es.caib.helium.integracio.service.firma.FirmaServicePortaFibImpl;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class FirmaConfig {

	@Autowired
	private Environment env;
	
	private final String PROPERTIES_BASE = "es.caib.helium.integracio.firma.portafib";
	
	@Bean(name = "firmaService")
	public FirmaService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.firma.service.class");
		try {
			var service = (FirmaService) Class.forName(pluginClass).getConstructor().newInstance();
			if (service instanceof FirmaServicePortaFibImpl) {
				var retorn = (FirmaServicePortaFibImpl) service;
				crearClient(retorn);
			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de FirmaService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}

	private void crearClient(FirmaServicePortaFibImpl servei) {
		
		Properties p = new Properties();
		p.put("es.caib.helium.integracio.firma.portafib.username", env.getRequiredProperty("es.caib.helium.integracio.firma.portafib.username"));
		p.put("es.caib.helium.integracio.firma.portafib.location", env.getRequiredProperty("es.caib.helium.integracio.firma.portafib.location"));
		p.put("es.caib.helium.integracio.firma.portafib.signer.email", env.getRequiredProperty("es.caib.helium.integracio.firma.portafib.signer.email"));
		p.put("es.caib.helium.integracio.firma.portafibplugins.signatureserver.portafib.api_passarela_url", 
				env.getRequiredProperty("es.caib.helium.integracio.firma.portafib.plugins.signatureserver.portafib.api_passarela_url"));
		p.put("es.caib.helium.integracio.firma.portafibplugins.signatureserver.portafib.api_passarela_username", 
				env.getRequiredProperty("es.caib.helium.integracio.firma.portafib.plugins.signatureserver.portafib.api_passarela_username"));
		p.put("es.caib.helium.integracio.firma.portafibplugins.signatureserver.portafib.api_passarela_password", 
				env.getRequiredProperty("es.caib.helium.integracio.firma.portafib.plugins.signatureserver.portafib.api_passarela_password"));
		
		servei.setUsername(env.getRequiredProperty("es.caib.helium.integracio.firma.portafib.username"));
		servei.setLocation(env.getRequiredProperty("es.caib.helium.integracio.firma.portafib.location"));
		servei.setEmail(env.getRequiredProperty("es.caib.helium.integracio.firma.portafib.signer.email"));
		servei.setPlugin(new ApiFirmaEnServidorSimpleJersey(
				getPropertyApiEndpoint(), 
				getPropertyApiUsername(),
				getPropertyApiPassword()));
		
	}
	
	private String getPropertyApiEndpoint() {
		return env.getProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_url");
	}
	
	private String getPropertyApiUsername() {
		return env.getProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_username");
	}
	
	private String getPropertyApiPassword() {
		return env.getProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_password");
	}

}
