/**
 * 
 */
package net.conselldemallorca.helium.core.security;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

/**
 * Permisos addicionals pel suport d'ACLs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtendedPermission extends BasePermission {

	private static final long serialVersionUID = 1L;

	/*public static final Permission READ = new ExtendedPermission(1 << 0, 'R'); // 1
	public static final Permission WRITE = new ExtendedPermission(1 << 1, 'W'); // 2
	public static final Permission CREATE = new ExtendedPermission(1 << 2, 'C'); // 4
	public static final Permission DELETE = new ExtendedPermission(1 << 3, 'D'); // 8
	public static final Permission ADMINISTRATION = new ExtendedPermission(1 << 4, 'A'); // 16*/
	public static final Permission DESIGN = new ExtendedPermission(1 << 5, 'G'); // 32
	public static final Permission ORGANIZATION = new ExtendedPermission(1 << 6, 'O'); // 64
	public static final Permission SUPERVISION = new ExtendedPermission(1 << 7, 'S'); // 128
	public static final Permission MANAGE = new ExtendedPermission(1 << 8, 'M'); // 256
	public static final Permission REASSIGNMENT = new ExtendedPermission(1 << 9, 'N'); // 512
	public static final Permission ACTION_EXEC = new ExtendedPermission(1 << 10, 'B');
	public static final Permission BIND = new ExtendedPermission(1 << 11, 'E');
	public static final Permission STOP = new ExtendedPermission(1 << 12, 'F');
	public static final Permission RESUME = new ExtendedPermission(1 << 13, 'H');
	public static final Permission SCRIPT = new ExtendedPermission(1 << 14, 'I');
	public static final Permission VERSION_UPDATE = new ExtendedPermission(1 << 15, 'J');
	public static final Permission VAR_CREATE = new ExtendedPermission(1 << 16, 'K');
	public static final Permission VAR_UPDATE = new ExtendedPermission(1 << 17, 'L');
	public static final Permission VAR_DELETE = new ExtendedPermission(1 << 18, 'P');
	public static final Permission DOC_CREATE = new ExtendedPermission(1 << 19, 'Q');
	public static final Permission DOC_UPDATE = new ExtendedPermission(1 << 20, 'T');
	public static final Permission DOC_DELETE = new ExtendedPermission(1 << 21, 'U');
	public static final Permission DOC_ATTACH = new ExtendedPermission(1 << 22, 'V');
	public static final Permission TASK_REASSIGN = new ExtendedPermission(1 << 23, 'X');
	public static final Permission TOKEN_REDIRECT = new ExtendedPermission(1 << 24, 'Y');
	public static final Permission GO_BACK = new ExtendedPermission(1 << 25, 'Z');
	public static final Permission CANCEL = new ExtendedPermission(1 << 26, '0');
	public static final Permission RESTORE = new ExtendedPermission(1 << 27, '1');



	protected ExtendedPermission(int mask) {
		super(mask);
	}

	protected ExtendedPermission(int mask, char code) {
		super(mask, code);
	}

}
