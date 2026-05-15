/**
 *
 */
package es.caib.helium.back.config;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken.Access;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import es.caib.helium.commons.config.BaseConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuració de Spring Security per a desplegar l'aplicació sobre JBoss.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
@EnableWebSecurity
@ConditionalOnWarDeployment
public class JBossWebSecurityConfig extends BaseWebSecurityConfig {

	@Value("${es.caib.helium.security.mappableRoles:" + BaseConfig.ROLE_ADMIN + "}")
	private String mappableRoles;
	@Value("${es.caib.helium.security.nameAttributeKey:preferred_username}")
	private String nameAttributeKey;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.addFilterBefore(
				preAuthenticatedProcessingFilter(),
				BasicAuthenticationFilter.class);
		http.authenticationProvider(preauthAuthProvider());
		http.logout((lo) -> lo.addLogoutHandler(getLogoutHandler()).
				logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL)).
				invalidateHttpSession(true).
				logoutSuccessUrl("/").
				permitAll(false));
		http.authorizeHttpRequests().
				requestMatchers(publicRequestMatchers()).permitAll().
				anyRequest().authenticated();
		http.headers().frameOptions().sameOrigin();
		http.csrf().disable();
		http.cors();
		return http.build();
	}

	@Bean
	public J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() throws Exception {
		J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter = new J2eePreAuthenticatedProcessingFilter();
		preAuthenticatedProcessingFilter.setAuthenticationDetailsSource(authenticationDetailsSource());
		preAuthenticatedProcessingFilter.setAuthenticationManager(authenticationManager());
		preAuthenticatedProcessingFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
		return preAuthenticatedProcessingFilter;
	}

	@Bean
	public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
		PreAuthenticatedAuthenticationProvider preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
		preauthAuthProvider.setPreAuthenticatedUserDetailsService(
				preAuthenticatedGrantedAuthoritiesUserDetailsService());
		return preauthAuthProvider;
	}

	@Bean
	public LogoutHandler getLogoutHandler() {
		return (request, response, authentication) -> {
			try {
				request.logout();
			} catch (ServletException ex) {
				log.error("Error al sortir de l'aplicació", ex);
			}
		};
	}

	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		final List<AuthenticationProvider> providers = new ArrayList<>(1);
		providers.add(preauthAuthProvider());
		return new ProviderManager(providers);
	}

	@Bean
	public AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource() {
		J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
			@Override
			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				Collection<String> j2eeUserRoles = getUserRoles(context);
				if (!j2eeUserRoles.contains("tothom")) {
					j2eeUserRoles.add("tothom");
				}
				logger.debug("Roles from ServletRequest for " + context.getUserPrincipal().getName() + ": " + j2eeUserRoles);
				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
				if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
					KeycloakPrincipal<?> keycloakPrincipal = ((KeycloakPrincipal<?>)context.getUserPrincipal());
					keycloakPrincipal.getKeycloakSecurityContext().getIdTokenString();
					Set<String> roles = new HashSet<>();
					roles.addAll(j2eeUserRoles);
					Access realmAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess();
					if (realmAccess != null && realmAccess.getRoles() != null) {
						logger.debug("Keycloak token realm roles: " + realmAccess.getRoles());
						realmAccess.getRoles().stream().map(r -> ROLE_PREFIX + r).forEach(roles::add);
					}
					logger.debug("Creating WebAuthenticationDetails for " + keycloakPrincipal.getName() + " with roles " + roles);
					result = new PreauthOidcWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles),
							keycloakPrincipal.getKeycloakSecurityContext().getIdTokenString());
				} else {
					logger.debug("Creating WebAuthenticationDetails for " + context.getUserPrincipal().getName() + " with roles " + j2eeUserRoles);
					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(j2eeUserRoles));
				}
				return result;
			}
		};
		SimpleMappableAttributesRetriever mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
		mappableAttributesRetriever.setMappableAttributes(new HashSet<>(Arrays.asList(mappableRoles.split(","))));
		authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
		SimpleAttributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();
		attributes2GrantedAuthoritiesMapper.setAttributePrefix(ROLE_PREFIX);
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
		return authenticationDetailsSource;
	}

	@Bean
	public PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedGrantedAuthoritiesUserDetailsService() {
		return new PreAuthenticatedGrantedAuthoritiesUserDetailsService() {
			@SneakyThrows
			protected UserDetails createUserDetails(
					Authentication token,
					Collection<? extends GrantedAuthority> authorities) {
				if (token.getDetails() instanceof PreauthOidcWebAuthenticationDetails) {
					PreauthOidcWebAuthenticationDetails tokenDetails = (PreauthOidcWebAuthenticationDetails)token.getDetails();
					String jwtIdToken = tokenDetails.getJwtIdToken();
					if (jwtIdToken != null) {
						JWT jwt = JWTParser.parse(jwtIdToken);
						return new PreauthOidcUserDetails(
								jwtIdToken,
								token.getName(),
								jwt.getJWTClaimsSet().getIssueTime().toInstant(),
								jwt.getJWTClaimsSet().getExpirationTime().toInstant(),
								jwt.getJWTClaimsSet().getClaims(),
								nameAttributeKey,
								authorities);
					}
				}
				return new User(token.getName(), "N/A", true, true, true, true, authorities);
			}
		};
	}

	@Getter
	public static class PreauthOidcWebAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
		private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
		private final String jwtIdToken;
		public PreauthOidcWebAuthenticationDetails(
				HttpServletRequest request,
				Collection<? extends GrantedAuthority> authorities,
				String jwtIdToken) {
			super(request, authorities);
			this.jwtIdToken = jwtIdToken;
		}
	}

	@Getter
	public static class PreauthOidcUserDetails extends User implements OidcUser {
		private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
		private final OidcIdToken idToken;
		private final OidcUserInfo userInfo;
		private final Map<String, Object> attributes;
		private final Map<String, Object> claims;
		private final String nameAttributeKey;
		public PreauthOidcUserDetails(
				String jwtIdToken,
				String username,
				Instant issueTime,
				Instant expirationTime,
				Map<String, Object> claims,
				String nameAttributeKey,
				Collection<? extends GrantedAuthority> authorities) {
			super(username, "N/A", true, true, true, true, authorities);
			this.idToken = new OidcIdToken(
					jwtIdToken,
					issueTime,
					expirationTime,
					claims);
			this.userInfo = new OidcUserInfo(claims);
			this.attributes = claims;
			this.claims = claims;
			this.nameAttributeKey = nameAttributeKey;
		}
		public String getName() {
			return getUsername();
		}
	}

}
