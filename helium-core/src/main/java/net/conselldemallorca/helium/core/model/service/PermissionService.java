/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.security.AclServiceDao;
import net.conselldemallorca.helium.core.security.PermissionUtil;
import net.conselldemallorca.helium.v3.core.api.dto.GenericEntityDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar permisos per als objectes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PermissionService {

	private AclServiceDao aclServiceDao;



	@SuppressWarnings("rawtypes")
	public void addPermissions(
			String recipient,
			boolean principal,
			Set<Permission> permissions,
			Serializable id,
			Class clazz,
			boolean granting) {
		for (Permission permission: permissions) {
			if (principal) {
				aclServiceDao.assignarPermisUsuari(
						recipient,
						clazz,
						(Long)id,
						permission);
			} else {
				aclServiceDao.assignarPermisRol(
						recipient,
						clazz,
						(Long)id,
						permission);
			}
		}
	}
	@SuppressWarnings("rawtypes")
	public void deletePermissions(
			String recipient,
			boolean principal,
			Set<Permission> permissions,
			Serializable id,
			Class clazz,
			boolean granting) {
		for (Permission permission: permissions) {
			if (principal) {
				aclServiceDao.revocarPermisUsuari(
						recipient,
						clazz,
						(Long)id,
						permission);
			} else {
				aclServiceDao.revocarPermisRol(
						recipient,
						clazz,
						(Long)id,
						permission);
			}
		}
	}
	@SuppressWarnings("rawtypes")
	public void deleteAllPermissionsForSid(
			String recipient,
			boolean principal,
			Serializable id,
			Class clazz) {
		deletePermissions(
				recipient,
				principal,
				new HashSet<Permission>(PermissionUtil.permissionMap.values()),
				id,
				clazz,
				true);
	}
	@SuppressWarnings("rawtypes")
	public Map<Sid, List<AccessControlEntry>> getAclEntriesGroupedBySid(
			Serializable id,
			Class clazz) {
		ObjectIdentity oid = new ObjectIdentityImpl(clazz, id);
		try {
			Map<Sid, List<AccessControlEntry>> resposta = new HashMap<Sid, List<AccessControlEntry>>();
			List<AccessControlEntry> aces = aclServiceDao.findAclsByOid(oid);
			if (aces != null) {
				for (AccessControlEntry ace: aces) {
					List<AccessControlEntry> entriesForSid = resposta.get(ace.getSid());
					if (entriesForSid == null) {
						entriesForSid = new ArrayList<AccessControlEntry>();
						resposta.put(ace.getSid(), entriesForSid);
					}
					entriesForSid.add(ace);
				}
			}
			return resposta;
		} catch (NotFoundException ex) {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public void filterAllowed(
			List list,
			Class clazz,
			Permission[] permissions) {
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object entry = it.next();
			if (!aclServiceDao.isGrantedAny((GenericEntityDto)entry, clazz, permissions))
				it.remove();
		}
	}
	@SuppressWarnings("rawtypes")
	public Object filterAllowed(
			GenericEntityDto object,
			Class clazz,
			Permission[] permissions) {
		if (aclServiceDao.isGrantedAny(object, clazz, permissions)) {
			return object;
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean isGrantedAny(
			GenericEntityDto object,
			Class clazz,
			Permission[] permissions) {
		return aclServiceDao.isGrantedAny(object, clazz, permissions);
	}



	@Autowired
	public void setAclServiceDao(AclServiceDao aclServiceDao) {
		this.aclServiceDao = aclServiceDao;
	}

}
