package es.caib.helium.domini.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Classe de configuració del servei de discovery (Eureka)
 *
 * Configuració per a execució amb spring-cloud i amb docker compose
 */
@Profile(value = {"spring-cloud", "compose"})
@EnableDiscoveryClient
@Configuration
public class LocalDiscoveryConfig {
}
