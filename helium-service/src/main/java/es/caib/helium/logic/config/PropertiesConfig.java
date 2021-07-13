package es.caib.helium.logic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class PropertiesConfig {

    @ConfigurationProperties(prefix = "")
    @Bean
    public Properties getGlobalProperties() {
        return new Properties();
    }

//    @ConfigurationProperties(prefix = "es.caib.helium.persones.plugin")
//    @Bean
//    public Properties getPersonesProperties() {
//        return new Properties();
//    }
}
