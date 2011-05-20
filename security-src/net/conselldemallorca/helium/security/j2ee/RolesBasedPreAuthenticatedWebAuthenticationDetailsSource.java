/**
 * 
 */
package net.conselldemallorca.helium.security.j2ee;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authoritymapping.MappableAttributesRetriever;
import org.springframework.security.ui.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;

/**
 * Proveeix els detalls de preautenticació per a Spring Security
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class RolesBasedPreAuthenticatedWebAuthenticationDetailsSource extends J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource {

	MappableAttributesRetriever mappableAttributesRetriever;

	public RolesBasedPreAuthenticatedWebAuthenticationDetailsSource() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String[] getUserRoles(Object context, String[] mappableRoles) {
		String[] rolesBons = mappableAttributesRetriever.getMappableAttributes();
        ArrayList j2eeUserRolesList = new ArrayList();
        for (int i = 0; i < rolesBons.length; i++) {
            if (((HttpServletRequest)context).isUserInRole(rolesBons[i])) {
                j2eeUserRolesList.add(rolesBons[i]);
            }
        }
        return (String[]) j2eeUserRolesList.toArray(new String[j2eeUserRolesList.size()]);
    }
	@Override
	public void setMappableRolesRetriever(MappableAttributesRetriever mappableAttributesRetriever) {
        this.mappableAttributesRetriever = mappableAttributesRetriever;
    }

}
