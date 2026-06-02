/**
 *
 */
package es.caib.helium.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Configuració de Spring Security per a executar l'aplicació amb Spring Boot.
 *
 * @author Limit Tecnologies
 */
public class BaseWebSecurityConfig {

	public static final String ROLE_PREFIX = "";
	public static final String LOGOUT_URL = "/usuari/logout";

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(ROLE_PREFIX);
	}

	@Bean
	public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	protected RequestMatcher[] publicRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher("/v1/salut"),
				new AntPathRequestMatcher("/v1/salut/performance"),
				new AntPathRequestMatcher("/swagger/**"),
				new AntPathRequestMatcher("/"),
				new AntPathRequestMatcher("/index.html"),
				new AntPathRequestMatcher("/swagger-ui"),
				new AntPathRequestMatcher("/swagger-ui/**/*"),
				new AntPathRequestMatcher("/apidocs"),
				new AntPathRequestMatcher("/apidocs/**/*"),
		};
	}

}
