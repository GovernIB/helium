/**
 * 
 */
package net.conselldemallorca.helium.core.security.permission;

import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.BasePermission;

/**
 * Permisos addicionals pel suport d'ACLs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtendedPermission extends BasePermission {

	private static final long serialVersionUID = 1L;

	public static final Permission READ = new ExtendedPermission(1 << 0, 'R'); // 1
    public static final Permission WRITE = new ExtendedPermission(1 << 1, 'W'); // 2
    public static final Permission CREATE = new ExtendedPermission(1 << 2, 'C'); // 4
    public static final Permission DELETE = new ExtendedPermission(1 << 3, 'D'); // 8
    public static final Permission ADMINISTRATION = new ExtendedPermission(1 << 4, 'A'); // 16
	public static final Permission DESIGN = new ExtendedPermission(1 << 5, 'G'); // 32
	public static final Permission ORGANIZATION = new ExtendedPermission(1 << 6, 'O'); // 64
	public static final Permission SUPERVISION = new ExtendedPermission(1 << 7, 'S'); // 128
	public static final Permission MANAGE = new ExtendedPermission(1 << 8, 'M'); // 256

	/**
	 * Registers the public static permissions defined on this class. This is
	 * mandatory so that the static methods will operate correctly. (copied from
	 * super class)
	 */
	static {
		registerPermissionsFor(ExtendedPermission.class);
	}

	private ExtendedPermission(int mask, char code) {
		super(mask, code);
	}

}
