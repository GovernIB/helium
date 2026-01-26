/**
 * 
 */
package es.caib.helium.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Proveeix els detalls de preautenticació per a Spring Security
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedPreAuthenticatedWebAuthenticationDetailsSource extends J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource {

	MappableAttributesRetriever mappableAttributesRetriever;

	public RolesBasedPreAuthenticatedWebAuthenticationDetailsSource() {
		super();
	}

	@Override
	protected Collection<String> getUserRoles(HttpServletRequest request) {
		Set<String> roles = mappableAttributesRetriever.getMappableAttributes();
		Set<String> j2eeUserRolesList = new HashSet<String>();
		for (String role: roles) {
			if (request.isUserInRole(role)) {
				j2eeUserRolesList.add(role);
			}
		}
		LOGGER.debug(
				"Rols SEYCON de l'usuari " + request.getUserPrincipal().getName() + ": " +
				Arrays.toString(j2eeUserRolesList.toArray(new String[j2eeUserRolesList.size()])));
		return j2eeUserRolesList;
	}

	@Override
	public void setMappableRolesRetriever(MappableAttributesRetriever mappableAttributesRetriever) {
		this.mappableAttributesRetriever = mappableAttributesRetriever;
		this.j2eeMappableRoles = new HashSet<String>();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolesBasedPreAuthenticatedWebAuthenticationDetailsSource.class);

}
