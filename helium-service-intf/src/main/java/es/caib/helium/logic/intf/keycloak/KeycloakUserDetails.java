/**
 * 
 */
package es.caib.helium.logic.intf.keycloak;

/**
 * Mètodes per a obtenir informació de l'usuari de keycloak.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface KeycloakUserDetails {

	public String getGivenName();
	public String getFamilyName();
	public String getFullName();
	public String getEmail();
	public Object getKeycloakPrincipal();

}
