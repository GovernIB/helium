/**
 * 
 */
package net.conselldemallorca.helium.core.security;

import org.springframework.security.acls.domain.DefaultPermissionFactory;

/**
 * PermissionFactory per als permisos addicionals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtendedPermissionFactory extends DefaultPermissionFactory {

	public ExtendedPermissionFactory() {
		super();
		registerPublicPermissions(ExtendedPermission.class);
	}

}
