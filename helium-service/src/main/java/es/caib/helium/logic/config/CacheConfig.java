package es.caib.helium.logic.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuració de cache.
 *
 * @author Limit Tecnologies
 */
@Configuration
@EnableCaching
public class CacheConfig {
	public static final String ACL_CACHE_NAME = "aclCache";
}
