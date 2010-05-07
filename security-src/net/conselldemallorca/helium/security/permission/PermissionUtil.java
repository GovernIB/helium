/**
 * 
 */
package net.conselldemallorca.helium.security.permission;

import java.util.HashMap;
import java.util.Map;


import org.springframework.security.acls.Permission;

/**
 * Utilitats pels permisos.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class PermissionUtil {

	public static final Map<String, Permission> permissionMap;

	static {
		permissionMap = new HashMap<String,Permission>();
		permissionMap.put("READ", ExtendedPermission.READ);
		permissionMap.put("WRITE", ExtendedPermission.WRITE);
		permissionMap.put("CREATE", ExtendedPermission.CREATE);
		permissionMap.put("DELETE", ExtendedPermission.DELETE);
		permissionMap.put("ADMINISTRATION", ExtendedPermission.ADMINISTRATION);
		permissionMap.put("DESIGN", ExtendedPermission.DESIGN);
		permissionMap.put("ORGANIZATION", ExtendedPermission.ORGANIZATION);
		permissionMap.put("SUPERVISION", ExtendedPermission.SUPERVISION);
	}

	public static Permission getPermissionForName(String name) {
		return permissionMap.get(name);
	}

	public static String getNameForPermission(Permission permission) {
		for (String key: permissionMap.keySet()) {
			Permission perm = permissionMap.get(key);
			if (perm.equals(permission))
				return key;
		}
		return null;
	}

}
