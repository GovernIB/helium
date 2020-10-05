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

	/** Objecte privat de sincronització */
	private Object syncObj = new Object();

	public RolesBasedPreAuthenticatedWebAuthenticationDetailsSource() {
		super();
	}

	@Override
	protected Collection<String> getUserRoles(HttpServletRequest request) {
        Set<String> j2eeUserRolesList = new HashSet<String>();
        synchronized(this.syncObj) {
    		Set<String> roles = mappableAttributesRetriever.getMappableAttributes();
            for (String role: roles) {
                if (request.isUserInRole(role)) {
                    j2eeUserRolesList.add(role);
                }
            }
        }
        return j2eeUserRolesList;
    }
	@Override
	public void setMappableRolesRetriever(MappableAttributesRetriever mappableAttributesRetriever) {
		synchronized(this.syncObj) {
	        this.mappableAttributesRetriever = mappableAttributesRetriever;
	        this.j2eeMappableRoles = new HashSet<String>();
		}
    }

}
