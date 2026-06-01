/**
 *
 */
package es.caib.helium.api.config;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuració de Spring Security per a executar l'aplicació amb Spring Boot.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
@ConditionalOnNotWarDeployment
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SpringBootWebSecurityConfig extends BaseWebSecurityConfig {

	@Autowired(required = false)
	private ClientRegistrationRepository clientRegistrationRepository;

	@Bean
	public SecurityFilterChain oauth2LoginSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests().
			requestMatchers(publicRequestMatchers()).permitAll().
			anyRequest().authenticated();
		http.oauth2Login().userInfoEndpoint(info -> {
			info.oidcUserService(oidcUserService());
		});
		LogoutHandler deleteCookiesLogoutHandler = (request, response, authentication) -> {
			try {
				log.info("Logout called");
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						Cookie deletedCookie = new Cookie(cookie.getName(), "");
						deletedCookie.setPath(cookie.getPath() != null ? cookie.getPath() : "/");
						deletedCookie.setMaxAge(0);
						deletedCookie.setHttpOnly(cookie.isHttpOnly());
						deletedCookie.setSecure(cookie.getSecure());
						response.addCookie(deletedCookie);
					}
				}
				request.logout();
			} catch (ServletException ex) {
				log.error("Error en el logout", ex);
			}
		};
		OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
			clientRegistrationRepository);
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
		http.logout(lo -> lo.
			addLogoutHandler(deleteCookiesLogoutHandler).
			logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL)).
			invalidateHttpSession(true).
			clearAuthentication(true).
			deleteCookies("OAuth_Token_Request_State", "JSESSIONID").
			logoutSuccessHandler(oidcLogoutSuccessHandler).
			logoutSuccessUrl("/"));
		http.headers().frameOptions().sameOrigin();
		http.csrf().disable();
		http.cors();
		return http.build();
	}

	private OAuth2UserService<OidcUserRequest,OidcUser> oidcUserService() {
		final OidcUserService delegate = new OidcUserService();
		return (userRequest) -> {
			OidcUser oidcUser = delegate.loadUser(userRequest);
			OAuth2AccessToken accessToken = userRequest.getAccessToken();
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			try {
				JWT parsedJwt = JWTParser.parse(accessToken.getTokenValue());
				JSONObject realmAccess = (JSONObject)parsedJwt.getJWTClaimsSet().getClaim("realm_access");
				if (realmAccess != null) {
					JSONArray roles = (JSONArray)realmAccess.get("roles");
					if (roles != null) {
						roles.stream().
						map(r -> new SimpleGrantedAuthority((String)r)).
						forEach(mappedAuthorities::add);
					}
					mappedAuthorities.add(new SimpleGrantedAuthority("tothom"));
				}
			} catch (ParseException ex) {
				log.warn("No s'han pogut obtenir els rols del token JWT", ex);
			}
			return new DefaultOidcUser(
					mappedAuthorities,
					oidcUser.getIdToken(),
					userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
		};
	}

}
