package es.caib.helium.base.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("spring-cloud")
@EnableDiscoveryClient
@Configuration
public class LocalDiscovery {
}
