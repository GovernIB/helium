package es.caib.helium.camunda.config;

public interface KeycloakUserDetails {

    String getGivenName();
    String getFamilyName();
    String getFullName();
    String getEmail();
    Object getKeycloakPrincipal();

}
