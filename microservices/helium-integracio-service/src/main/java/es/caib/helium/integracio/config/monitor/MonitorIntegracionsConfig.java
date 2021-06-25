package es.caib.helium.integracio.config.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.integracio.excepcions.ServeisExternsException;
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;

@Configuration
@ConfigurationProperties(prefix = "es.caib.helium.integracio")
@ComponentScan("es.caib.helium.integracio.service")
public class MonitorIntegracionsConfig {
	
	@Autowired
	private Environment env;
	
	@Bean(name = "monitorIntegracionsService")
	public MonitorIntegracionsService instanciarService() throws ServeisExternsException {
	
		String pluginClass = env.getRequiredProperty("es.caib.helium.integracio.monitor.integracions.service.class");
		try {
			return (MonitorIntegracionsService) Class.forName(pluginClass).getConstructor().newInstance();
		} catch (Exception ex) {
			throw new ServeisExternsException("Error al crear la instància de la NotificacioService (" + "pluginClass=" + pluginClass + ")", ex);
		}
	}

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
