package es.caib.helium.service.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricRegistry;

/**
 * Configuració de metriques de dropwizard.metrics.
 * 
 * @author Limit Tecnologies
 */
@Configuration
public class MetricsConfig {
	
	@Bean
	public MetricRegistry metricRegistry(CacheManager springCacheManager) {
		return new MetricRegistry();
	}

}
