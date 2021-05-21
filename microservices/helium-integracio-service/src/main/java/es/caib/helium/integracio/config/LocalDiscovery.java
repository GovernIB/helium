package es.caib.helium.integracio.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile(value = {"spring-cloud", "compose"})
@EnableDiscoveryClient
@Configuration
public class LocalDiscovery {
}
