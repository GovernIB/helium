package es.caib.helium.base.helper;

/**
 * Helper per a accedir a informació de l'usuari provinent de Keycloak.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class KeycloakHelper {

//    public static String getCurrentUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
//            return auth.getName();
//        } else {
//            return null;
//        }
//    }
//
//    public static String getCurrentUserFullName() {
//        KeycloakPrincipal<?> keycloakPrincipal = getKeycloakPrincipal();
//        if (keycloakPrincipal != null) {
//            AccessToken accessToken = keycloakPrincipal.getKeycloakSecurityContext().getToken();
//            return accessToken.getName();
//        } else {
//            return null;
//        }
//    }
//
//    public static String getCurrentUserEmail() {
//        KeycloakPrincipal<?> keycloakPrincipal = getKeycloakPrincipal();
//        if (keycloakPrincipal != null) {
//            AccessToken accessToken = keycloakPrincipal.getKeycloakSecurityContext().getToken();
//            return accessToken.getEmail();
//        } else {
//            return null;
//        }
//    }
//
//    public static String getTokenString() {
//        KeycloakPrincipal<?> keycloakPrincipal = getKeycloakPrincipal();
//        if (keycloakPrincipal != null) {
//            return keycloakPrincipal.getKeycloakSecurityContext().getTokenString();
//        } else {
//            return null;
//        }
//    }
//
//    public static KeycloakPrincipal<?> getKeycloakPrincipal() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
//            if (auth.getPrincipal() instanceof KeycloakPrincipal) {
//                return (KeycloakPrincipal<?>)auth.getPrincipal();
//            } else if (auth.getPrincipal() instanceof KeycloakUserDetails) {
//                return (KeycloakPrincipal<?>)((KeycloakUserDetails)auth.getPrincipal()).getKeycloakPrincipal();
//            }
//        }
//        return null;
//    }

}
