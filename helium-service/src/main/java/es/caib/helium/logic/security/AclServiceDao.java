/**
 * 
 */
package es.caib.helium.logic.security;

import es.caib.helium.logic.helper.PermisosHelper;
import org.springframework.security.acls.model.Permission;

import javax.annotation.Resource;

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
				objectIdentifier,
				objectClass,
				permission);
	}
	public void assignarPermisRol(
			String roleName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		permisosHelper.assignarPermisRol(
				roleName,
				objectIdentifier,
				objectClass,
				permission);
	}

	public void revocarPermisUsuari(
			String userName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		permisosHelper.revocarPermisUsuari(
				userName,
				objectIdentifier,
				objectClass,
				permission);
	}
	public void revocarPermisRol(
			String roleName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		permisosHelper.revocarPermisRol(
				roleName,
				objectIdentifier,
				objectClass,
				permission);
	}

	public void revocarPermisosUsuari(
			String userName,
			Class<?> objectClass,
			Long objectIdentifier) {
		for (Permission permission: PermissionUtil.permissionMap.values()) {
			permisosHelper.revocarPermisUsuari(
					userName,
					objectIdentifier,
					objectClass,
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
					objectIdentifier,
					objectClass,
					permission);
		}
	}

//	public List<AccessControlEntry> findAclsByOid(
//			ObjectIdentity oid) {
//			try {
//				return permisosHelper.getAclSids(
//						Class.forName(oid.getType()),
//						(Long)oid.getIdentifier());
//			} catch (ClassNotFoundException e) {
//				return null;
//			}
//	}

//	@SuppressWarnings("rawtypes")
//	public boolean isGrantedAny(
//			GenericEntity object,
//			Class clazz,
//			Permission[] permissions) {
//		return permisosHelper.isGrantedAny(
//				(Long)object.getId(),
//				clazz,
//				permissions);
//	}

	

}
