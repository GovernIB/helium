/**
 * 
 */
package es.caib.helium.config;

import javax.annotation.Resource;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.CarrecService;
import es.caib.helium.logic.intf.service.PermisService;

/**
 * @author limit
 *
 */
@KeycloakConfiguration
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

	@Resource
	private AplicacioService aplicacioService;

	@Resource
	private PermisService permisService;

	@Resource
	private CarrecService carrecService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		http.logout().
			logoutRequestMatcher(new AntPathRequestMatcher("/logout")).
			invalidateHttpSession(true).
			logoutSuccessUrl("/").
			permitAll(false);
		http.authorizeRequests().
			anyRequest().authenticated();
		http.cors();
		http.csrf().disable();
		http.headers().frameOptions().disable();		
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		HeliumGrantedAuthoritiesMapper authorityMapper = new HeliumGrantedAuthoritiesMapper();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(authorityMapper);
		auth.authenticationProvider(keycloakAuthenticationProvider);
	}

	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	@Bean
	public KeycloakConfigResolver KeycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}

	
	
	
	
	
	
	
//	@Bean
//	public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
//		PreAuthenticatedAuthenticationProvider preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
//		preauthAuthProvider.setPreAuthenticatedUserDetailsService(
//				preAuthenticatedGrantedAuthoritiesUserDetailsService());
//		return preauthAuthProvider;
//	}
//	
//	@Bean
//	public PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedGrantedAuthoritiesUserDetailsService() {
//		return new PreAuthenticatedGrantedAuthoritiesUserDetailsService() {
//			
//			protected UserDetails createUserDetails(
//					Authentication token, 
//					Collection<? extends GrantedAuthority> authorities) {
//				
//				if (token.getDetails() instanceof KeycloakWebAuthenticationDetails) {
//					KeycloakWebAuthenticationDetails keycloakWebAuthenticationDetails = (KeycloakWebAuthenticationDetails)token.getDetails();
//					return new PreauthKeycloakUserDetails(
//							token.getName(),
//							"N/A",
//							true,
//							true,
//							true,
//							true,
//							authorities,
//							keycloakWebAuthenticationDetails.getKeycloakPrincipal());
//				} else {
//					return super.createUserDetails(token, authorities);
//					//return new User(token.getName(), "N/A", true, true, true, true, authorities);
//				}
//			}
//		};
//	}
//
//	@Bean
//	public J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() throws Exception {
//		J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter = new J2eePreAuthenticatedProcessingFilter();
//		preAuthenticatedProcessingFilter.setAuthenticationDetailsSource(authenticationDetailsSource());
//		preAuthenticatedProcessingFilter.setAuthenticationManager(authenticationManager());
//		preAuthenticatedProcessingFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
//		return preAuthenticatedProcessingFilter;
//	}
//	
//	@Bean
//	public AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource() {
//
//		J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
//			@Override
//			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
//				Collection<String> j2eeUserRoles = getUserRoles(context);
//				Collection<? extends GrantedAuthority> userGas = j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(
//						j2eeUserRoles);
//				if (logger.isDebugEnabled()) {
//					logger.debug("J2EE roles [" + j2eeUserRoles + "] mapped to Granted Authorities: [" + userGas + "]");
//				}
//				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
//				if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
//					KeycloakPrincipal<?> keycloakPrincipal = ((KeycloakPrincipal<?>)context.getUserPrincipal());
//					Set<String> roles = keycloakPrincipal.getKeycloakSecurityContext().getToken().getResourceAccess(
//							keycloakPrincipal.getKeycloakSecurityContext().getToken().getIssuedFor()).getRoles();
//					result = new KeycloakWebAuthenticationDetails(
//							context,
//							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles),
//							keycloakPrincipal);
//				} else {
//					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(context, userGas);
//				}
//				return result;
//			}
//		};
//
//		// mappableRolesRetriever - net.conselldemallorca.helium.webapp.v3.security.RolesBasedMappableAttributesRetriever  
//		// o recuperar la llista de rols com es fa a la classe en qüestió
//
//		// ! No sembla filtrar bé
//		SimpleMappableAttributesRetriever mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
//		Set<String> rols = new HashSet<String>();
//		rols.add("ROLE_ADMIN");
//		rols.add("ROLE_USER");
////		rols.add("HEL_ADMIN");
////		rols.add("HEL_USER");
////		rols.add("tothom");
//		
//		String source = aplicacioService.getGlobalProperties().getProperty("app.jbpm.identity.source");
//		if ("helium".equalsIgnoreCase(source)) {
//			for (PermisRolDto permis: permisService.findAll()) {
//				String codi = permis.getCodi();
//				if (!rols.contains(codi))
//					rols.add(codi);
//			}
//		} else {
//			PaginacioParamsDto paginacioTots = new PaginacioParamsDto();
//			paginacioTots.setPaginaNum(0);
//			paginacioTots.setPaginaTamany(Integer.MAX_VALUE);
//			for (CarrecJbpmIdDto group: carrecService.findConfigurats(paginacioTots)) {
//				if (group != null && !rols.contains(group.getCodi()))
//					rols.add(group.getCodi());
//			}
//		}
//		mappableAttributesRetriever.setMappableAttributes(rols);
//		authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
//		
//		// userRoles2GrantedAuthoritiesMapper - net.conselldemallorca.helium.webapp.v3.security.RolesBasedAttributes2GrantedAuthoritiesMapper
//
////		SimpleAttributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();		
////		attributes2GrantedAuthoritiesMapper.setAttributePrefix(ROLE_PREFIX);
////		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
//		
//		// ! no sembla aplicar-se bé el mapeig
//		MapBasedAttributes2GrantedAuthoritiesMapper mapBasedAttributes2GrantedAuthoritiesMapper = new MapBasedAttributes2GrantedAuthoritiesMapper();
//		Map<String,String> roleMapping = new HashMap<String, String>();
//		roleMapping.put("HEL_ADMIN", "ROLE_ADMIN");
//		roleMapping.put("HEL_USER", "ROLE_USER");
//		roleMapping.put("tothom", "ROLE_USER");
//		mapBasedAttributes2GrantedAuthoritiesMapper.setAttributes2grantedAuthoritiesMap(roleMapping);
//
//		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(mapBasedAttributes2GrantedAuthoritiesMapper);
//
//		return authenticationDetailsSource;
//	}
//	
//	
////	/**
////	 * Aconsegueix els rols que seran rellevants per a l'aplicació.
////	 * 
////	 * @author Limit Tecnologies <limit@limit.es>
////	 */
////	@Bean
////	public MappableAttributesRetriever RolesBasedMappableAttributesRetriever() {
////		return null;
////	}
//	
//	@SuppressWarnings("serial")
//	public static class KeycloakWebAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
//		private KeycloakPrincipal<?> keycloakPrincipal;
//		public KeycloakWebAuthenticationDetails(
//				HttpServletRequest request,
//				Collection<? extends GrantedAuthority> authorities,
//				KeycloakPrincipal<?> keycloakPrincipal) {
//			super(request, authorities);
//			this.keycloakPrincipal = keycloakPrincipal;
//		}
//		public KeycloakPrincipal<?> getKeycloakPrincipal() {
//			return keycloakPrincipal;
//		}
//	}	
//	
//	
//	@SuppressWarnings("serial")
//	public static class PreauthKeycloakUserDetails extends User implements es.caib.helium.logic.intf.keycloak.KeycloakUserDetails {
//		private KeycloakPrincipal<?> keycloakPrincipal;
//		public PreauthKeycloakUserDetails(
//				String username,
//				String password,
//				boolean enabled,
//				boolean accountNonExpired,
//				boolean credentialsNonExpired,
//				boolean accountNonLocked,
//				Collection<? extends GrantedAuthority> authorities,
//				KeycloakPrincipal<?> keycloakPrincipal) {
//			super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
//			this.keycloakPrincipal = keycloakPrincipal;
//		}
//		public KeycloakPrincipal<?> getKeycloakPrincipal() {
//			return keycloakPrincipal;
//		}
//		public String getGivenName() {
//			if (keycloakPrincipal instanceof KeycloakPrincipal) {
//				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getGivenName();
//			} else {
//				return null;
//			}
//		}
//		public String getFamilyName() {
//			if (keycloakPrincipal instanceof KeycloakPrincipal) {
//				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getFamilyName();
//			} else {
//				return null;
//			}
//		}
//		public String getFullName() {
//			if (keycloakPrincipal instanceof KeycloakPrincipal) {
//				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getName();
//			} else {
//				return null;
//			}
//		}
//		public String getEmail() {
//			if (keycloakPrincipal instanceof KeycloakPrincipal) {
//				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getEmail();
//			} else {
//				return null;
//			}
//		}
//	}	
}
