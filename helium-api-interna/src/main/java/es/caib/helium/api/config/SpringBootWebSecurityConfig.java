/**
 *
 */
package es.caib.helium.api.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import es.caib.helium.commons.config.PropertyConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Configuració de Spring Security per a executar l'aplicació amb Spring Boot.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
@EnableWebSecurity
@ConditionalOnNotWarDeployment
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SpringBootWebSecurityConfig extends BaseWebSecurityConfig {

	@Value("${spring.security.oauth2.client.provider.external.issuer-uri:#{null}}")
	private String keycloakUrl;
	@Value("${spring.security.oauth2.client.registration.external.client-id:goib-ws}")
	private String keycloakClientId;
	@Value("${spring.security.oauth2.client.registration.external.client-secret:#{null}}")
	private String keycloakClientSecret;
	@Value("${" + PropertyConfig.PROP_SECURITY_RESOURCEACCESS_API_INTERNA + ":#{null}}")
	private String resourceAccess;

	private RestTemplate restTemplate;

	@Bean
	public SecurityFilterChain basicAuthSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests().
			requestMatchers(publicRequestMatchers()).permitAll().
			anyRequest().authenticated();
		http.httpBasic();
		http.headers().frameOptions().sameOrigin();
		http.csrf().disable();
		http.cors();
		http.authenticationProvider(new AuthenticationProvider() {
			@Override
			@SneakyThrows
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				String username = authentication.getName();
				String password = authentication.getCredentials().toString();
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
				map.add("grant_type", "password");
				map.add("client_id", keycloakClientId);
				map.add("client_secret", keycloakClientSecret);
				map.add("username", username);
				map.add("password", password);
				try {
					JsonNode tokenResponse = getRestTemplate().postForObject(
						keycloakUrl + "/protocol/openid-connect/token",
						new HttpEntity<MultiValueMap<String, String>>(map, headers),
						JsonNode.class);
					@SuppressWarnings("unchecked")
					Map<String, Object> tokenResponseMap = new ObjectMapper().convertValue(
						tokenResponse,
						Map.class);
					List<GrantedAuthority> authorities = new ArrayList<>();
					String accessToken = (String)tokenResponseMap.get("access_token");
					JWT jwt = JWTParser.parse(accessToken);
					if (getResourceAccess() == null) {
						// Rols a nivell de realm
						JSONObject realmAccess = (JSONObject)jwt.getJWTClaimsSet().getClaim("realm_access");
						if (realmAccess != null) {
							JSONArray roles = (JSONArray)realmAccess.get("roles");
							if (roles != null) {
								roles.stream().forEach(r -> authorities.add(new SimpleGrantedAuthority((String)r)));
							}
							authorities.add(new SimpleGrantedAuthority("tothom"));
						}
					} else {
						// Rols a nivell de client
						JSONObject resourceAccess = (JSONObject)jwt.getJWTClaimsSet().getClaim("resource_access");
						if (resourceAccess != null) {
							JSONObject client = (JSONObject)resourceAccess.get(getResourceAccess());
							if (client != null) {
								JSONArray roles = (JSONArray)client.get("roles");
								if (roles != null) {
									roles.stream().forEach(r -> authorities.add(new SimpleGrantedAuthority((String)r)));
								}
								authorities.add(new SimpleGrantedAuthority("tothom"));
							}
						}
					}
					return new UsernamePasswordAuthenticationToken(
						username,
						"N/A",
						authorities);
				} catch (HttpClientErrorException ex) {
					if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
						throw new BadCredentialsException(ex.getMessage());
					} else {
						throw new DisabledException(ex.getMessage());
					}
				}
			}
			@Override
			public boolean supports(Class<?> authentication) {
				return authentication.equals(UsernamePasswordAuthenticationToken.class);
			}
		});
		return http.build();
	}

	protected RestTemplate getRestTemplate() {
		if (restTemplate == null) {
			restTemplate = new RestTemplateBuilder().build();
		}
		return restTemplate;
	}

	protected String getResourceAccess() {
		return resourceAccess;
	}

}
