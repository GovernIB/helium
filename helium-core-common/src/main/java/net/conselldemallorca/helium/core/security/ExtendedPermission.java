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

	/* Permisos existents */
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

	/* Nous permisos */
	public static final Permission CANCEL = new ExtendedPermission(1 << 10, 'B');
	public static final Permission STOP = new ExtendedPermission(1 << 11, 'E');
	public static final Permission RELATE = new ExtendedPermission(1 << 12, 'F');
	public static final Permission DATA_MANAGE = new ExtendedPermission(1 << 13, 'H');
	public static final Permission DOC_MANAGE = new ExtendedPermission(1 << 14, 'I');
	public static final Permission TERM_MANAGE = new ExtendedPermission(1 << 15, 'J');
	public static final Permission TASK_MANAGE = new ExtendedPermission(1 << 16, 'K');
	public static final Permission TASK_SUPERV = new ExtendedPermission(1 << 17, 'L');
	public static final Permission TASK_ASSIGN = new ExtendedPermission(1 << 18, 'P');
	public static final Permission LOG_READ = new ExtendedPermission(1 << 26, 'Y');
	public static final Permission LOG_MANAGE = new ExtendedPermission(1 << 19, 'Q');
	public static final Permission TOKEN_READ = new ExtendedPermission(1 << 27, 'Z');
	public static final Permission TOKEN_MANAGE = new ExtendedPermission(1 << 28, '1');
	public static final Permission DESIGN_ADMIN = new ExtendedPermission(1 << 21, 'T');
	public static final Permission DESIGN_DELEG = new ExtendedPermission(1 << 22, 'U');
	public static final Permission SCRIPT_EXE = new ExtendedPermission(1 << 23, 'V');
	public static final Permission UNDO_END = new ExtendedPermission(1 << 24, 'W');
	public static final Permission DEFPROC_UPDATE = new ExtendedPermission(1 << 25, 'X');



	protected ExtendedPermission(int mask) {
		super(mask);
	}

	protected ExtendedPermission(int mask, char code) {
		super(mask, code);
	}

}
