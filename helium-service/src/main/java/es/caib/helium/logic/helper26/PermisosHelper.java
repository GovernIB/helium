/**
 * 
 */
package es.caib.helium.logic.helper26;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.helium.logic.intf.dto.PermisDto;
import es.caib.helium.logic.intf.dto.PrincipalTipusEnumDto;
import es.caib.helium.logic.util.GlobalProperties;


/**
 * Helper per a la gestió de permisos dins les ACLs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PermisosHelper {

	@Resource
	private LookupStrategy lookupStrategy;
	@Resource
	private MutableAclService aclService;

	public List<AccessControlEntry> findAclsByOid(
			ObjectIdentity oid) {
			try {
				return getAclSids(
						Class.forName(oid.getType()), 
						(Long)oid.getIdentifier());
			} catch (ClassNotFoundException e) {
				return null;
			}
	}

	public void assignarPermisUsuari(
			String userName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		assignarPermisos(
				new PrincipalSid(userName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}
	public void assignarPermisRol(
			String roleName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		assignarPermisos(
				new GrantedAuthoritySid(getMapeigRol(roleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}

	public void revocarPermisUsuari(
			String userName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		revocarPermisos(
				new PrincipalSid(userName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}
	public void revocarPermisRol(
			String roleName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		revocarPermisos(
				new GrantedAuthoritySid(getMapeigRol(roleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}

	public void mourePermisUsuari(
			String sourceUserName,
			String targetUserName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		assignarPermisos(
				new PrincipalSid(targetUserName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
		revocarPermisos(
				new PrincipalSid(sourceUserName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}
	public void mourePermisRol(
			String sourceRoleName,
			String targetRoleName,
			Class<?> objectClass,
			Long objectIdentifier,
			Permission permission) {
		assignarPermisos(
				new GrantedAuthoritySid(getMapeigRol(targetRoleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
		revocarPermisos(
				new GrantedAuthoritySid(getMapeigRol(sourceRoleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}

	public List<AccessControlEntry> getAclSids(
			Class<?> objectClass,
			Long objectIdentifier) {
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			MutableAcl acl = (MutableAcl)aclService.readAclById(oid);
			return acl.getEntries();
		} catch (NotFoundException nfex) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean filterGrantedAny(
			Collection<?> objects,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(
					it.next());
			if (!isGrantedAny(
					objectIdentifier,
					clazz,
					permissions))
				it.remove();
		}
		
		return !objects.isEmpty();
	}
	public boolean isGrantedAny(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions) {
		boolean[] granted = verificarPermisos(
				objectIdentifier,
				clazz,
				permissions);
		for (int i = 0; i < granted.length; i++) {
			if (granted[i])
				return true;
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAll(
			Collection<?> objects,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(
					it.next());
			if (!isGrantedAll(
					objectIdentifier,
					clazz,
					permissions,
					auth))
				it.remove();
		}
	}
	public boolean isGrantedAll(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		boolean[] granted = verificarPermisos(
				objectIdentifier,
				clazz,
				permissions);
		boolean result = true;
		for (int i = 0; i < granted.length; i++) {
			if (!granted[i]) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	public boolean isGrantedAny(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		boolean[] granted = verificarPermisos(
				objectIdentifier,
				clazz,
				permissions);
		for (int i = 0; i < granted.length; i++) {
			if (granted[i])
				return true;
		}
		return false;
	}
	
	public List<PermisDto> findPermisos(
			Long objectIdentifier,
			Class<?> objectClass) {
		Acl acl = null;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			acl = aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			return new ArrayList<PermisDto>();
		}
		return findPermisosPerAcl(acl);
	}
	public Map<Long, List<PermisDto>> findPermisos(
			List<Long> objectIdentifiers,
			Class<?> objectClass) {
		try {
			Map<Long, List<PermisDto>> resposta = new HashMap<Long, List<PermisDto>>();
			List<ObjectIdentity> oids = new ArrayList<ObjectIdentity>();
			for (Long objectIdentifier: objectIdentifiers) {
				ObjectIdentity oid = new ObjectIdentityImpl(
						objectClass,
						objectIdentifier);
				oids.add(oid);
			}
			if (!oids.isEmpty()) {
				Map<ObjectIdentity, Acl> acls = lookupStrategy.readAclsById(oids, null);
				for (ObjectIdentity oid: acls.keySet()) {
					resposta.put(
							(Long)oid.getIdentifier(),
							findPermisosPerAcl(acls.get(oid)));
				}
			}
			return resposta;
		} catch (NotFoundException nfex) {
			return new HashMap<Long, List<PermisDto>>();
		}
	}

	private void assignarPermisos(
			Sid sid,
			Class<?> objectClass,
			Serializable objectIdentifier,
			Permission[] permissions) {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		MutableAcl acl = null;
		try {
			acl = (MutableAcl)aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			logger.error(nfex);
			acl = aclService.createAcl(oid);
		}
		for (Permission permission: permissions) {
			boolean insertar;
			try {
				List<Permission> permisos = new ArrayList<Permission>();
				List<Sid> sids = new ArrayList<Sid>();
				permisos.add(permission);
				sids.add(sid);
				insertar = !acl.isGranted(
						permisos,
						sids,
						true);
			} catch (Exception ignored) {
				insertar = true;
			}
			if (insertar)
				acl.insertAce(
						acl.getEntries().size(),
						permission,
						sid,
						true);
		}
		aclService.updateAcl(acl);
	}

	private void revocarPermisos(
			Sid sid,
			Class<?> objectClass,
			Serializable objectIdentifier,
			Permission[] permissions) throws NotFoundException {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		try {
			MutableAcl acl = (MutableAcl)aclService.readAclById(oid);
			List<Integer> indexosPerEsborrar = new ArrayList<Integer>();
			int aceIndex = 0;
			for (AccessControlEntry ace: acl.getEntries()) {
				if (ace.getSid().equals(sid)) {
					for (Permission p: permissions) {
						if (p.equals(ace.getPermission()))
							indexosPerEsborrar.add(aceIndex);
					}
				}
				aceIndex++;
			}
			for (Integer index: indexosPerEsborrar)
				acl.deleteAce(index);
			aclService.updateAcl(acl);
		} catch (NotFoundException nfex) {
			// Si no troba l'ACL no fa res
		}
	}

	private boolean[] verificarPermisos(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		if (auth.getAuthorities() != null) {
			for (GrantedAuthority ga: auth.getAuthorities())
				sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		}
		boolean[] granted = new boolean[permissions.length];
		for (int i = 0; i < permissions.length; i++)
			granted[i] = false;
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(
					clazz,
					objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			List<Permission> ps = new ArrayList<Permission>();
			for (int i = 0; i < permissions.length; i++) {
				try {
					ps.add(permissions[i]);
					granted[i] = acl.isGranted(
							ps,
							sids,
							true);
					ps.clear();
				} catch (NotFoundException ex) {}
			}
		} catch (NotFoundException ex) {}
		return granted;
	}

	private String getMapeigRol(String rol) {
		String propertyMapeig = 
				(String)GlobalProperties.getInstance().get(
						"es.caib.helium.mapeig.rol." + rol);
		if (propertyMapeig != null)
			return propertyMapeig;
		else
			return rol;
	}
	
	private List<PermisDto> findPermisosPerAcl(Acl acl) {
		List<PermisDto> resposta = new ArrayList<PermisDto>();
		if (acl != null) {
			Map<String, PermisDto> permisosUsuari = new HashMap<String, PermisDto>();
			Map<String, PermisDto> permisosRol = new HashMap<String, PermisDto>();
			for (AccessControlEntry ace: acl.getEntries()) {
				PermisDto permis = null;
				if (ace.getSid() instanceof PrincipalSid) {
					String principal = ((PrincipalSid)ace.getSid()).getPrincipal();
					permis = permisosUsuari.get(principal);
					if (permis == null) {
						permis = new PermisDto();
						permis.setId((Long)ace.getId());
						permis.setPrincipalNom(principal);
						permis.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
						permisosUsuari.put(principal, permis);
					}
				} else if (ace.getSid() instanceof GrantedAuthoritySid) {
					String grantedAuthority = ((GrantedAuthoritySid)ace.getSid()).getGrantedAuthority();
					permis = permisosUsuari.get(grantedAuthority);
					if (permis == null) {
						permis = new PermisDto();
						permis.setId((Long)ace.getId());
						permis.setPrincipalNom(grantedAuthority);
						permis.setPrincipalTipus(PrincipalTipusEnumDto.ROL);
						permisosRol.put(grantedAuthority, permis);
					}
				}
				if (permis != null) {
					if (BasePermission.READ.equals(ace.getPermission()))
						permis.setRead(true);
					if (BasePermission.WRITE.equals(ace.getPermission()))
						permis.setWrite(true);
					if (BasePermission.CREATE.equals(ace.getPermission()))
						permis.setCreate(true);
					if (BasePermission.DELETE.equals(ace.getPermission()))
						permis.setDelete(true);
					if (BasePermission.ADMINISTRATION.equals(ace.getPermission()))
						permis.setAdministration(true);
				}
			}
			resposta.addAll(permisosUsuari.values());
			resposta.addAll(permisosRol.values());
		}
		return resposta;
	}

	public interface ObjectIdentifierExtractor<T> {
		public Long getObjectIdentifier(T object);
	}
	
	private static final Log logger = LogFactory.getLog(PermisosHelper.class);
}
