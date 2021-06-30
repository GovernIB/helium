package es.caib.helium.base.config;

public interface KeycloakUserDetails {

    String getGivenName();
    String getFamilyName();
    String getFullName();
    String getEmail();
    Object getKeycloakPrincipal();

}
