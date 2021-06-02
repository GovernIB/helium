package es.caib.helium.integracio.config.arxiu;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.arxiu.ArxiuService;
import es.caib.helium.integracio.service.arxiu.ArxiuServiceCaibImpl;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class ArxiuConfig {

	@Autowired
	private Environment env;
	
	@Bean(name = "arxiuService")
	public ArxiuService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.arxiu.service.class");
		try {
			var service = (ArxiuService) Class.forName(pluginClass).getConstructor().newInstance();
			if (service instanceof ArxiuServiceCaibImpl) {
				var retorn = (ArxiuServiceCaibImpl) service;
				retorn.setApi(crearIArxiuPluginApi());
			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de ArxiuService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
	
	private IArxiuPlugin crearIArxiuPluginApi() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.arxiu.plugin.class");
		try {
			var properties = new Properties();
			properties.put("es.caib.helium.integracio.arxiu.caib.base.url", env.getRequiredProperty("es.caib.helium.integracio.arxiu.caib.base.url"));
			properties.put("es.caib.helium.integracio.arxiu.caib.aplicacio.codi", env.getRequiredProperty("es.caib.helium.integracio.arxiu.caib.aplicacio.codi"));
			properties.put("es.caib.helium.integracio.arxiu.caib.usuari", env.getRequiredProperty("es.caib.helium.integracio.arxiu.caib.usuari"));
			properties.put("es.caib.helium.integracio.arxiu.caib.contrasenya", env.getRequiredProperty("es.caib.helium.integracio.arxiu.caib.contrasenya"));

			Class<?> clazz = Class.forName(pluginClass);
			return (IArxiuPlugin)clazz.getDeclaredConstructor(String.class, Properties.class)
					.newInstance("es.caib.helium.integracio.arxiu.", properties);
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància del plugin d'arxiu digital (" + "pluginClass=" + pluginClass + ")",
					ex);
		}
		
	}
}
