/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.presentacio.mvc.util.ModelTypeEditor;
import net.conselldemallorca.helium.security.permission.PermissionUtil;

import org.springframework.security.acls.Permission;

/**
 * TypeEditor per als permisos
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class PermissionTypeEditor extends ModelTypeEditor<Permission> {

	@Override
	public String stringFromValue() {
		return PermissionUtil.getNameForPermission((Permission)getValue());
	}
	@Override
	public Permission valueFromString(String text) {
		return PermissionUtil.getPermissionForName(text);
	}

}
