package es.caib.helium.camunda.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SecurityConfig {
//    extends WebSecurityConfigurerAdapter {
//
//    private static final String ROLE_PREFIX = "";
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authenticationProvider(preauthAuthProvider()).
//                jee().j2eePreAuthenticatedProcessingFilter(preAuthenticatedProcessingFilter());
//        http.logout().
//                addLogoutHandler(getLogoutHandler()).
//                logoutRequestMatcher(new AntPathRequestMatcher("/logout")).
//                invalidateHttpSession(true).
//                logoutSuccessUrl("/").
//                permitAll(false);
//        http.authorizeRequests().
//                //antMatchers("/test").hasRole("tothom").
//                //antMatchers("/api/**/*").permitAll().
//                //anyRequest().permitAll();
//                        anyRequest().authenticated();
//        http.cors();
//        http.csrf().disable();
//        http.headers().frameOptions().disable();
//    }
//
//    @Bean
//    public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
//        PreAuthenticatedAuthenticationProvider preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
//        preauthAuthProvider.setPreAuthenticatedUserDetailsService(
//                preAuthenticatedGrantedAuthoritiesUserDetailsService());
//        return preauthAuthProvider;
//    }
//
//    @Bean
//    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
//        return new GrantedAuthorityDefaults(ROLE_PREFIX);
//    }
//
//    @Bean
//    public PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedGrantedAuthoritiesUserDetailsService() {
//        return new PreAuthenticatedGrantedAuthoritiesUserDetailsService() {
//            protected UserDetails createUserDetails(
//                    Authentication token,
//                    Collection<? extends GrantedAuthority> authorities) {
//                if (token.getDetails() instanceof KeycloakWebAuthenticationDetails) {
//                    KeycloakWebAuthenticationDetails keycloakWebAuthenticationDetails = (KeycloakWebAuthenticationDetails)token.getDetails();
//                    return new PreauthKeycloakUserDetails(
//                            token.getName(),
//                            "N/A",
//                            true,
//                            true,
//                            true,
//                            true,
//                            authorities,
//                            keycloakWebAuthenticationDetails.getKeycloakPrincipal());
//                } else {
//                    return new User(token.getName(), "N/A", true, true, true, true, authorities);
//                }
//            }
//        };
//    }
//
//    @Bean
//    public J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() throws Exception {
//        J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter = new J2eePreAuthenticatedProcessingFilter();
//        preAuthenticatedProcessingFilter.setAuthenticationDetailsSource(authenticationDetailsSource());
//        preAuthenticatedProcessingFilter.setAuthenticationManager(authenticationManager());
//        preAuthenticatedProcessingFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
//        return preAuthenticatedProcessingFilter;
//    }
//
//    @Bean
//    public AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource() {
//        J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
//            @Override
//            public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
//                Collection<String> j2eeUserRoles = getUserRoles(context);
//                Collection<? extends GrantedAuthority> userGas = j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(
//                        j2eeUserRoles);
//                if (logger.isDebugEnabled()) {
//                    logger.debug("J2EE roles [" + j2eeUserRoles + "] mapped to Granted Authorities: [" + userGas + "]");
//                }
//                PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
//                if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
//                    KeycloakPrincipal<?> keycloakPrincipal = ((KeycloakPrincipal<?>)context.getUserPrincipal());
//                    Set<String> roles = keycloakPrincipal.getKeycloakSecurityContext().getToken().getResourceAccess(
//                            keycloakPrincipal.getKeycloakSecurityContext().getToken().getIssuedFor()).getRoles();
//                    result = new KeycloakWebAuthenticationDetails(
//                            context,
//                            j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles),
//                            keycloakPrincipal);
//                } else {
//                    result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(context, userGas);
//                }
//                return result;
//            }
//        };
//        SimpleMappableAttributesRetriever mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
//        mappableAttributesRetriever.setMappableAttributes(new HashSet<>());
//        authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
//        SimpleAttributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();
//        attributes2GrantedAuthoritiesMapper.setAttributePrefix(ROLE_PREFIX);
//        authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
//        return authenticationDetailsSource;
//    }
//
//    @Bean
//    public LogoutHandler getLogoutHandler() {
//        return new LogoutHandler() {
//            @Override
//            public void logout(
//                    HttpServletRequest request,
//                    HttpServletResponse response,
//                    Authentication authentication) {
//                try {
//                    request.logout();
//                } catch (ServletException ex) {
//                    log.error("Error al sortir de l'aplicació", ex);
//                }
//            }
//        };
//    }
//
//    @SuppressWarnings("serial")
//    public static class KeycloakWebAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
//        private KeycloakPrincipal<?> keycloakPrincipal;
//        public KeycloakWebAuthenticationDetails(
//                HttpServletRequest request,
//                Collection<? extends GrantedAuthority> authorities,
//                KeycloakPrincipal<?> keycloakPrincipal) {
//            super(request, authorities);
//            this.keycloakPrincipal = keycloakPrincipal;
//        }
//        public KeycloakPrincipal<?> getKeycloakPrincipal() {
//            return keycloakPrincipal;
//        }
//    }
//    @SuppressWarnings("serial")
//    public static class PreauthKeycloakUserDetails extends User implements KeycloakUserDetails {
//        private KeycloakPrincipal<?> keycloakPrincipal;
//        public PreauthKeycloakUserDetails(
//                String username,
//                String password,
//                boolean enabled,
//                boolean accountNonExpired,
//                boolean credentialsNonExpired,
//                boolean accountNonLocked,
//                Collection<? extends GrantedAuthority> authorities,
//                KeycloakPrincipal<?> keycloakPrincipal) {
//            super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
//            this.keycloakPrincipal = keycloakPrincipal;
//        }
//        public KeycloakPrincipal<?> getKeycloakPrincipal() {
//            return keycloakPrincipal;
//        }
//        public String getGivenName() {
//            if (keycloakPrincipal instanceof KeycloakPrincipal) {
//                return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getGivenName();
//            } else {
//                return null;
//            }
//        }
//        public String getFamilyName() {
//            if (keycloakPrincipal instanceof KeycloakPrincipal) {
//                return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getFamilyName();
//            } else {
//                return null;
//            }
//        }
//        public String getFullName() {
//            if (keycloakPrincipal instanceof KeycloakPrincipal) {
//                return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getName();
//            } else {
//                return null;
//            }
//        }
//        public String getEmail() {
//            if (keycloakPrincipal instanceof KeycloakPrincipal) {
//                return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getEmail();
//            } else {
//                return null;
//            }
//        }
//    }

}
