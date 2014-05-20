/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

/**
 * Servei per gestionar permisos per als objectes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PermissionService {

	@SuppressWarnings("rawtypes")
	public Map<Sid, List<AccessControlEntry>> getAclEntriesGroupedBySid(Serializable id, Class clazz);

	@SuppressWarnings("rawtypes")
	public boolean isGrantedAny(Long object, Class clazz, Permission[] permissions);

	@SuppressWarnings("rawtypes")
	public boolean filterAllowed(List tipus, Object objectIdentifierExtractor, Class<?> clazz, Permission[] permissions);

}
