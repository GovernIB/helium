/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;

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
        return j2eeUserRolesList;
    }
	@Override
	public void setMappableRolesRetriever(MappableAttributesRetriever mappableAttributesRetriever) {
        this.mappableAttributesRetriever = mappableAttributesRetriever;
        this.j2eeMappableRoles = new HashSet<String>();
    }

}
