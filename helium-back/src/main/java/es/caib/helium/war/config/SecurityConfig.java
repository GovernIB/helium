/**
 * 
 */
package es.caib.helium.war.config;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.KeycloakPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.Attributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import es.caib.helium.logic.intf.dto.CarrecJbpmIdDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PermisRolDto;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.CarrecService;
import es.caib.helium.logic.intf.service.PermisService;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuració de seguretat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String ROLE_PREFIX = "";

	@Resource
	private AplicacioService aplicacioService;

	@Resource
	private PermisService permisService;

	@Resource
	private CarrecService carrecService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.
			authenticationProvider(preauthAuthProvider()).
			jee().j2eePreAuthenticatedProcessingFilter(preAuthenticatedProcessingFilter());
		http.logout().
			addLogoutHandler(getLogoutHandler()).
			logoutRequestMatcher(new AntPathRequestMatcher("/logout")).
			invalidateHttpSession(true).
			logoutSuccessUrl("/").
			permitAll(false);
        http.authorizeRequests().
			antMatchers("/js/**").permitAll().
			antMatchers("/css/**").permitAll().
			antMatchers("/fonts/**").permitAll().
			antMatchers("/webjars/**").permitAll().
			antMatchers("/img/**").permitAll().
			antMatchers("/extensions/**").permitAll().
			antMatchers("/**/datatable/**").permitAll().
			antMatchers("/**/selection/**").permitAll().
			anyRequest().authenticated();

		http.cors();
		http.csrf().disable();
		http.headers().frameOptions().disable();
	}
	

	@Bean
	public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
		PreAuthenticatedAuthenticationProvider preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
		preauthAuthProvider.setPreAuthenticatedUserDetailsService(
				preAuthenticatedGrantedAuthoritiesUserDetailsService());
		return preauthAuthProvider;
	}

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(ROLE_PREFIX);
	}

	@Bean
	public PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedGrantedAuthoritiesUserDetailsService() {
		return new PreAuthenticatedGrantedAuthoritiesUserDetailsService() {
			protected UserDetails createUserDetails(
					Authentication token,
					Collection<? extends GrantedAuthority> authorities) {
				if (token.getDetails() instanceof KeycloakWebAuthenticationDetails) {
					KeycloakWebAuthenticationDetails keycloakWebAuthenticationDetails = (KeycloakWebAuthenticationDetails)token.getDetails();
					return new PreauthKeycloakUserDetails(
							token.getName(),
							"N/A",
							true,
							true,
							true,
							true,
							authorities,
							keycloakWebAuthenticationDetails.getKeycloakPrincipal());
				} else {
					return new User(token.getName(), "N/A", true, true, true, true, authorities);
				}
			}
		};
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
	public AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource() {
		J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
			@Override
			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				Collection<String> j2eeUserRoles = getUserRoles(context);
				Collection<? extends GrantedAuthority> userGas = j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(
						j2eeUserRoles);
				if (logger.isDebugEnabled()) {
					logger.debug("J2EE roles [" + j2eeUserRoles + "] mapped to Granted Authorities: [" + userGas + "]");
				}
				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
				if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
					KeycloakPrincipal<?> keycloakPrincipal = ((KeycloakPrincipal<?>)context.getUserPrincipal());
					Set<String> roles = keycloakPrincipal.getKeycloakSecurityContext().getToken().getResourceAccess(
							keycloakPrincipal.getKeycloakSecurityContext().getToken().getIssuedFor()).getRoles();
					result = new KeycloakWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles),
							keycloakPrincipal);
				} else {
					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(context, userGas);
				}
				return result;
			}
			
			/** Sobreescriu el mètode per retornar tots els rols en el cas d'autenticació keycloak. */
			@Override
			protected Collection<String> getUserRoles(HttpServletRequest request) {
				Collection<String> rols = null;
				try {
					if (request.getUserPrincipal() != null) {
						if (request.getUserPrincipal() instanceof KeycloakPrincipal) {
							rols = new ArrayList<String>();
							KeycloakPrincipal<?> keycloakPrincipal = ((KeycloakPrincipal<?>)request.getUserPrincipal());
							Set<String> roles = keycloakPrincipal.getKeycloakSecurityContext().getToken().getResourceAccess(
									keycloakPrincipal.getKeycloakSecurityContext().getToken().getIssuedFor()).getRoles();
							for (String rol : roles) {
								rols.add(rol);
							}
						} else {
							rols = super.getUserRoles(request);
						}
					}
				} catch(Exception e) {
					log.error("Error obtenint els rols de l'usuari autenticat: " + e.getMessage());
				}
				return rols;
			}
		};
		// Llista de rols permesos a l'apliació
		authenticationDetailsSource.setMappableRolesRetriever(mappableRolesRetriever());
		// Mapeig de rols
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(mapBasedAttributes2GrantedAuthoritiesMapper());
		
		return authenticationDetailsSource;
	}
	
	/** Construeix el ben que consulta tots els rols mapejables definits a Helium.
	 * 
	 * @return
	 */
	@Bean
	public MappableAttributesRetriever mappableRolesRetriever() {
		SimpleMappableAttributesRetriever mappableRolesRetriever = new SimpleMappableAttributesRetriever();
		Set<String> rols = new HashSet<String>();
		
		// Rols per defecte
		rols.add("ROLE_ADMIN");
		rols.add("ROLE_USER");
		rols.add("HEL_ADMIN");
		rols.add("HEL_USER");
		rols.add("tothom");
		rols.add("TOTHOM");
		
		// Rols d'aplicació o assignats en el motor
		String source = aplicacioService.getGlobalProperties().getProperty("app.jbpm.identity.source");
		if ("helium".equalsIgnoreCase(source)) {
			for (PermisRolDto permis: permisService.findAll()) {
				String codi = permis.getCodi();
				if (!rols.contains(codi))
					rols.add(codi);
			}
		} else {
			PaginacioParamsDto paginacioTots = new PaginacioParamsDto();
			paginacioTots.setPaginaNum(0);
			paginacioTots.setPaginaTamany(Integer.MAX_VALUE);
			for (CarrecJbpmIdDto group: carrecService.findConfigurats(paginacioTots)) {
				if (group != null && !rols.contains(group.getCodi()))
					rols.add(group.getCodi());
			}
		}
		mappableRolesRetriever.setMappableAttributes(rols);

		return mappableRolesRetriever;
	}
	
	/** Bean per definir el mapeig de rols.
	 * 
	 * @return
	 */
	@Bean 
	public Attributes2GrantedAuthoritiesMapper mapBasedAttributes2GrantedAuthoritiesMapper() {
		
		return new HeliumMapBasedAttributes2GrantedAuthoritiesMapper();
	}

	@Bean
	public LogoutHandler getLogoutHandler() {
		return new LogoutHandler() {
			@Override
			public void logout(
					HttpServletRequest request,
					HttpServletResponse response,
					Authentication authentication) {
				try {
					request.logout();
				} catch (ServletException ex) {
					log.error("Error al sortir de l'aplicació", ex);
				}
			}
		};
	}

	@SuppressWarnings("serial")
	public static class KeycloakWebAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
		private KeycloakPrincipal<?> keycloakPrincipal;
		public KeycloakWebAuthenticationDetails(
				HttpServletRequest request,
				Collection<? extends GrantedAuthority> authorities,
				KeycloakPrincipal<?> keycloakPrincipal) {
			super(request, authorities);
			this.keycloakPrincipal = keycloakPrincipal;
		}
		public KeycloakPrincipal<?> getKeycloakPrincipal() {
			return keycloakPrincipal;
		}
	}
	@SuppressWarnings("serial")
	public static class PreauthKeycloakUserDetails extends User implements es.caib.helium.logic.intf.keycloak.KeycloakUserDetails {
		private KeycloakPrincipal<?> keycloakPrincipal;
		public PreauthKeycloakUserDetails(
				String username,
				String password,
				boolean enabled,
				boolean accountNonExpired,
				boolean credentialsNonExpired,
				boolean accountNonLocked,
				Collection<? extends GrantedAuthority> authorities,
				KeycloakPrincipal<?> keycloakPrincipal) {
			super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			this.keycloakPrincipal = keycloakPrincipal;
		}
		public KeycloakPrincipal<?> getKeycloakPrincipal() {
			return keycloakPrincipal;
		}
		public String getGivenName() {
			if (keycloakPrincipal instanceof KeycloakPrincipal) {
				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getGivenName();
			} else {
				return null;
			}
		}
		public String getFamilyName() {
			if (keycloakPrincipal instanceof KeycloakPrincipal) {
				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getFamilyName();
			} else {
				return null;
			}
		}
		public String getFullName() {
			if (keycloakPrincipal instanceof KeycloakPrincipal) {
				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getName();
			} else {
				return null;
			}
		}
		public String getEmail() {
			if (keycloakPrincipal instanceof KeycloakPrincipal) {
				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getEmail();
			} else {
				return null;
			}
		}
	}

}
