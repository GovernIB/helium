package es.caib.helium.integracio.config.custodia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.helium.integracio.domini.custodia.ClienteCustodiaCaib;
import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.custodia.CustodiaService;
import es.caib.helium.integracio.service.custodia.CustodiaServiceCaibImpl;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class CustodiaConfig {

	@Autowired
	private Environment env;
	
	@Bean(name = "custodiaService")
	public CustodiaService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.custodia.service.class");
		try {
			var service = (CustodiaService) Class.forName(pluginClass).getConstructor().newInstance();
			if (service instanceof CustodiaServiceCaibImpl) {
				var retorn = (CustodiaServiceCaibImpl) service;
				retorn.setClient(crearClientCustodia());
			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de RegistreService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
	
	public ClienteCustodiaCaib crearClientCustodia() {
		
		var url = env.getRequiredProperty("es.caib.helium.integracio.custodia.caib.url");
		var usuari = env.getRequiredProperty("es.caib.helium.integracio.custodia.caib.usuari");
		var password = env.getRequiredProperty("es.caib.helium.integracio.custodia.caib.password");
		var baseUrl = env.getRequiredProperty("es.caib.helium.integracio.custodia.caib.verificacio.baseurl");
		return new ClienteCustodiaCaib(url, usuari, password, baseUrl);
	}
}
