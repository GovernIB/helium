/**
 * 
 */
package net.conselldemallorca.helium.core.security;

import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.GenericEntity;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper;

import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Per a permetre cridar al PermisosHelper sense haver de
 * fer canvis als serveis de versions 2.x.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AclServiceDao {

	@Resource
	private PermisosHelper permisosHelper;

	public void assignarPermisUsuari(
			String userName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		permisosHelper.assignarPermisUsuari(
				userName,
				objectClass,
				objectIdentifier,
				permission);
	}
	public void assignarPermisRol(
			String roleName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		permisosHelper.assignarPermisRol(
				roleName,
				objectClass,
				objectIdentifier,
				permission);
	}

	public void revocarPermisUsuari(
			String userName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		permisosHelper.revocarPermisUsuari(
				userName,
				objectClass,
				objectIdentifier,
				permission);
	}
	public void revocarPermisRol(
			String roleName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		permisosHelper.revocarPermisRol(
				roleName,
				objectClass,
				objectIdentifier,
				permission);
	}

	public void revocarPermisosUsuari(
			String userName,
			Class<?> objectClass,
			Long objectIdentifier) {
		for (Permission permission: PermissionUtil.permissionMap.values()) {
			permisosHelper.revocarPermisUsuari(
					userName,
					objectClass,
					objectIdentifier,
					permission);
		}
	}
	public void revocarPermisosRol(
			String roleName,
			Class<?> objectClass,
			Long objectIdentifier) {
		for (Permission permission: PermissionUtil.permissionMap.values()) {
			permisosHelper.revocarPermisRol(
					roleName,
					objectClass,
					objectIdentifier,
					permission);
		}
	}

	public List<AccessControlEntry> findAclsByOid(
			ObjectIdentity oid) {
			try {
				return permisosHelper.getAclSids(
						Class.forName(oid.getType()), 
						(Long)oid.getIdentifier());
			} catch (ClassNotFoundException e) {
				return null;
			}
	}

	@SuppressWarnings("rawtypes")
	public boolean isGrantedAny(
			GenericEntity object,
			Class clazz,
			Permission[] permissions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return permisosHelper.isGrantedAny(
				(Long)object.getId(),
				clazz,
				permissions,
				auth);
	}

	

}
