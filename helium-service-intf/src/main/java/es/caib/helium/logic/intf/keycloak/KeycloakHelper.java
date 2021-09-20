/**
 * 
 */
package es.caib.helium.logic.intf.keycloak;

import lombok.Getter;
import lombok.Setter;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * Helper per a accedir a informació de l'usuari provinent de Keycloak.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class KeycloakHelper {

	@Getter @Setter
	private static boolean useResourceRoleMappings;
	@Getter @Setter
	private static String client;

	@Value("${keycloak.use-resource-role-mappings}")
	private String useResourceRole;

	@Value("${keycloak.resource}")
	private String resource;

	public static String getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			return auth.getName();
		} else {
			return null;
		}
	}

	public static String getCurrentUserFullName() {
		KeycloakPrincipal<?> keycloakPrincipal = getKeycloakPrincipal();
		if (keycloakPrincipal != null) {
			AccessToken accessToken = keycloakPrincipal.getKeycloakSecurityContext().getToken();
			return accessToken.getName();
		} else {
			return null;
		}
	}

	public static Set<String> getCurrentUserRols() {
		KeycloakPrincipal<?> keycloakPrincipal = getKeycloakPrincipal();
		if (keycloakPrincipal != null) {
			AccessToken accessToken = keycloakPrincipal.getKeycloakSecurityContext().getToken();
//			var useResourceRoleMappings = "true".equals(System.getProperty("keycloak.use-resource-role-mappings"));
			if (useResourceRoleMappings) {
//				var client = System.getProperty("keycloak.resource");
				return accessToken.getResourceAccess(client).getRoles();
			} else {
				return accessToken.getRealmAccess().getRoles();
			}
		} else {
			return null;
		}
	}

	public static String getCurrentUserEmail() {
		KeycloakPrincipal<?> keycloakPrincipal = getKeycloakPrincipal();
		if (keycloakPrincipal != null) {
			AccessToken accessToken = keycloakPrincipal.getKeycloakSecurityContext().getToken();
			return accessToken.getEmail();
		} else {
			return null;
		}
	}

	public static KeycloakPrincipal<?> getKeycloakPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			if (auth.getPrincipal() instanceof KeycloakPrincipal) {
				return (KeycloakPrincipal<?>)auth.getPrincipal();
			} else if (auth.getPrincipal() instanceof KeycloakUserDetails) {
				return (KeycloakPrincipal<?>)((KeycloakUserDetails)auth.getPrincipal()).getKeycloakPrincipal();
			}
		}
		return null;
	}

	public static String getTokenString() {
		KeycloakPrincipal<?> keycloakPrincipal = getKeycloakPrincipal();
		if (keycloakPrincipal != null) {
			return keycloakPrincipal.getKeycloakSecurityContext().getTokenString();
		} else {
			return null;
		}
	}

	@PostConstruct
	private void postConstruct() {
		KeycloakHelper.setClient(this.resource);
		KeycloakHelper.setUseResourceRoleMappings("true".equals(this.useResourceRole));
	}
}
