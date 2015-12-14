/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.security.PermissionUtil;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

import org.springframework.security.acls.model.Permission;

/**
 * TypeEditor per als permisos
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
