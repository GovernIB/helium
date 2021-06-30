/**
 * 
 */
package es.caib.helium.back.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuració de CORS.
 * 
 * @author Limit Tecnologies
 */
@Configuration
public class CorsConfig {

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
		corsConfiguration.addAllowedOrigin("http://10.35.3.111:3000");
		corsConfiguration.setAllowedMethods(Arrays.asList("*"));
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

}
