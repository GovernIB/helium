package es.caib.helium.integracio.config.unitat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.unitat.UnitatsOrganiquesService;
import es.caib.helium.integracio.service.unitat.UnitatsOrganiquesServiceDir3Impl;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class UnitatsOrganiquesConfig {
	
	@Autowired
	private Environment env;
	
	@Bean(name = "unitatsService")
	public UnitatsOrganiquesService instanciarService() throws ServeisExternsException {
		
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.unitats.organiques.service.class");
		try { // TODO DONA PROBLEMES DE CONNEXIO QUAN ESTAVA FUNCIONANT BÉ
			var service = (UnitatsOrganiquesService) Class.forName(pluginClass).getConstructor().newInstance();
//			if (service instanceof UnitatsOrganiquesServiceDir3Impl) {
//				var retorn = (UnitatsOrganiquesServiceDir3Impl) service;
//				retorn.crearClient(
//						env.getRequiredProperty("es.caib.helium.integracio.unitats.organiques.dir3.service.url"),
//						env.getRequiredProperty("es.caib.helium.integracio.unitats.organiques.dir3.service.username"),
//						env.getRequiredProperty("es.caib.helium.integracio.unitats.organiques.dir3.service.password"),
//						env.getRequiredProperty("es.caib.helium.integracio.unitats.organiques.dir3.service.timeout", Integer.class),
//						env.getRequiredProperty("es.caib.helium.integracio.unitats.organiques.dir3.service.log.actiu", Boolean.class));
//			}
			return service;
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància d'UnitatsOrganiquesService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}
}
