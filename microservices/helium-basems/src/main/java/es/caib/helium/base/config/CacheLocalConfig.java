package es.caib.helium.base.config;

import com.hazelcast.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Classe de configuració de la caché distribuïda
 *
 * Configuració per a execució local
 */
@Profile(value = {"!spring-cloud & !compose"})
@Configuration
public class CacheLocalConfig {

    @Bean
    public Config hazelcastConfig() {
        Config config = new Config();
        config.setClusterName("HeliumMS");
        return config;
    }


}
