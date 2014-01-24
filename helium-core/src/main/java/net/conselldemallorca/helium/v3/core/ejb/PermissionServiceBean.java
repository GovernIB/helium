/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.service.PermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

/**
 * Servei per gestionar permisos per als objectes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PermissionServiceBean implements PermissionService {
	@Autowired
	PermissionService delegate;

	@Override
	@SuppressWarnings("rawtypes")
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Map<Sid, List<AccessControlEntry>> getAclEntriesGroupedBySid(Serializable id, Class clazz) {
		return delegate.getAclEntriesGroupedBySid(id, clazz);
	}

	@Override
	@SuppressWarnings("rawtypes")
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isGrantedAny(Long object, Class clazz, Permission[] permissions) {
		return delegate.isGrantedAny(object, clazz, permissions);
	}

	@Override
	@SuppressWarnings("rawtypes")
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean filterAllowed(List tipus, Object objectIdentifierExtractor, Class<?> clazz, Permission[] permissions) {
		return delegate.filterAllowed(tipus, objectIdentifierExtractor, clazz, permissions);
	}
}
