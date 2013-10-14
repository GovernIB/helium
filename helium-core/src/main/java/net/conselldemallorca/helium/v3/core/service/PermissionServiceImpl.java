/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.v3.core.api.service.PermissionService;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper;

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
@Service("permissionServiceV3")
public class PermissionServiceImpl implements PermissionService {
	@Resource
	private PermisosHelper permisosHelper;

	@SuppressWarnings("rawtypes")
	public Map<Sid, List<AccessControlEntry>> getAclEntriesGroupedBySid(Serializable id, Class clazz) {
		ObjectIdentity oid = new ObjectIdentityImpl(clazz, id);
		try {
			Map<Sid, List<AccessControlEntry>> resposta = new HashMap<Sid, List<AccessControlEntry>>();
			List<AccessControlEntry> aces = permisosHelper.findAclsByOid(oid);
			if (aces != null) {
				for (AccessControlEntry ace : aces) {
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
	public void filterAllowed(List list, Class clazz, Permission[] permissions) {
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object entry = it.next();
			if (!permisosHelper.isGrantedAny((Long)entry, clazz, permissions))
				it.remove();
		}
	}

	@SuppressWarnings("rawtypes")
	public Object filterAllowed(Long object, Class clazz, Permission[] permissions) {
		if (permisosHelper.isGrantedAny(object, clazz, permissions)) {
			return object;
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean isGrantedAny(Long object, Class clazz, Permission[] permissions) {
		return permisosHelper.isGrantedAny(object, clazz, permissions);
	}
}
