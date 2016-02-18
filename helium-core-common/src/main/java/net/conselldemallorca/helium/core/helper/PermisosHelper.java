/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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

import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.ControlPermisosDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PrincipalTipusEnumDto;


/**
 * Helper per a la gestió de permisos dins les ACLs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("permisosHelperV3")
public class PermisosHelper {

	@Resource
	private LookupStrategy lookupStrategy;
	@Resource
	private MutableAclService aclService;



	public void assignarPermisUsuari(
			String userName,
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new PrincipalSid(userName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				false);
	}
	public void assignarPermisRol(
			String roleName,
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new GrantedAuthoritySid(getMapeigRol(roleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				false);
	}

	public void revocarPermisUsuari(
			String userName,
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		revocarPermisos(
				new PrincipalSid(userName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}
	public void revocarPermisRol(
			String roleName,
			Long objectIdentifier,
			Class<?> objectClass,
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
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new PrincipalSid(targetUserName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				false);
		revocarPermisos(
				new PrincipalSid(sourceUserName),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}
	public void mourePermisRol(
			String sourceRoleName,
			String targetRoleName,
			Long objectIdentifier,
			Class<?> objectClass,
			Permission permission) {
		assignarPermisos(
				new GrantedAuthoritySid(getMapeigRol(targetRoleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission},
				false);
		revocarPermisos(
				new GrantedAuthoritySid(getMapeigRol(sourceRoleName)),
				objectClass,
				objectIdentifier,
				new Permission[] {permission});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void filterGrantedAny(
			Collection<?> objects,
			ObjectIdentifierExtractor objectIdentifierExtractor,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		Iterator<?> it = objects.iterator();
		while (it.hasNext()) {
			Long objectIdentifier = objectIdentifierExtractor.getObjectIdentifier(
					it.next());
			if (!isGrantedAny(
					objectIdentifier,
					clazz,
					permissions,
					auth))
				it.remove();
		}
	}
	public boolean isGrantedAny(
			Long objectIdentifier,
			Class<?> clazz,
			Permission[] permissions,
			Authentication auth) {
		boolean[] granted = verificarPermisos(
				objectIdentifier,
				clazz,
				permissions,
				auth);
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
				permissions,
				auth);
		boolean result = true;
		for (int i = 0; i < granted.length; i++) {
			if (!granted[i]) {
				result = false;
				break;
			}
		}
		return result;
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
	public void updatePermis(
			Long objectIdentifier,
			Class<?> objectClass,
			PermisDto permis) {
		if (PrincipalTipusEnumDto.USUARI.equals(permis.getPrincipalTipus())) {
			assignarPermisos(
					new PrincipalSid(permis.getPrincipalNom()),
					objectClass,
					objectIdentifier,
					getPermissionsFromPermis(permis),
					true);
		} else if (PrincipalTipusEnumDto.ROL.equals(permis.getPrincipalTipus())) {
			assignarPermisos(
					new GrantedAuthoritySid(getMapeigRol(permis.getPrincipalNom())),
					objectClass,
					objectIdentifier,
					getPermissionsFromPermis(permis),
					true);
		}
	}
	public void deletePermis(
			Long objectIdentifier,
			Class<?> objectClass,
			Long permisId) {
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			Acl acl = aclService.readAclById(oid);
			for (AccessControlEntry ace: acl.getEntries()) {
				if (permisId.equals(ace.getId())) {
					assignarPermisos(
							ace.getSid(),
							objectClass,
							objectIdentifier,
							new Permission[] {},
							true);
				}
			}
		} catch (NotFoundException nfex) {
		}
	}
	public void deleteAcl(
			Long objectIdentifier,
			Class<?> objectClass) {
		try {
			ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
			aclService.deleteAcl(oid, true);
		} catch (NotFoundException nfex) {
		}
	}

	public void omplirControlPermisosSegonsUsuariActual(
			List<Long> ids,
			List<? extends ControlPermisosDto> dtos,
			Class<?> classePermisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ObjectIdentifierExtractor<Long> oie = new ObjectIdentifierExtractor<Long>() {
			public Long getObjectIdentifier(Long id) {
				return id;
			}
		};
		List<Long> idsAmbPermisCreate = new ArrayList<Long>();
		idsAmbPermisCreate.addAll(ids);
		filterGrantedAny(
				idsAmbPermisCreate,
				oie,
				classePermisos,
				new Permission[] {
					ExtendedPermission.CREATE,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisRead = new ArrayList<Long>();
		idsAmbPermisRead.addAll(ids);
		filterGrantedAny(
				idsAmbPermisRead,
				oie,
				classePermisos,
				new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisWrite = new ArrayList<Long>();
		idsAmbPermisWrite.addAll(ids);
		filterGrantedAny(
				idsAmbPermisWrite,
				oie,
				classePermisos,
				new Permission[] {
					ExtendedPermission.WRITE,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisDelete = new ArrayList<Long>();
		idsAmbPermisDelete.addAll(ids);
		filterGrantedAny(
				idsAmbPermisDelete,
				oie,
				classePermisos,
				new Permission[] {
					ExtendedPermission.DELETE,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisSupervision = new ArrayList<Long>();
		idsAmbPermisSupervision.addAll(ids);
		filterGrantedAny(
				idsAmbPermisSupervision,
				oie,
				classePermisos,
				new Permission[] {
					ExtendedPermission.SUPERVISION,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisReassignment = new ArrayList<Long>();
		idsAmbPermisReassignment.addAll(ids);
		filterGrantedAny(
				idsAmbPermisReassignment,
				oie,
				classePermisos,
				new Permission[] {
					ExtendedPermission.REASSIGNMENT,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisAdministration = new ArrayList<Long>();
		idsAmbPermisAdministration.addAll(ids);
		filterGrantedAll(
				idsAmbPermisAdministration,
				oie,
				classePermisos,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION},
				auth);
		for (int i = 0; i < ids.size(); i++) {
			Long id = ids.get(i);
			ControlPermisosDto dto = dtos.get(i);
			dto.setPermisCreate(
					idsAmbPermisCreate.contains(id));
			dto.setPermisRead(
					idsAmbPermisRead.contains(id));
			dto.setPermisWrite(
					idsAmbPermisWrite.contains(id));
			dto.setPermisDelete(
					idsAmbPermisDelete.contains(id));
			dto.setPermisSupervision(
					idsAmbPermisSupervision.contains(id));
			dto.setPermisReassignment(
					idsAmbPermisReassignment.contains(id));
			dto.setPermisAdministration(
					idsAmbPermisAdministration.contains(id));
		}
	}

	public void omplirControlPermisosSegonsUsuariActual(
			Long id,
			ControlPermisosDto dto,
			Class<?> classePermisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		dto.setPermisCreate(
				isGrantedAny(
						id,
						classePermisos,
						new Permission[] {
								ExtendedPermission.CREATE,
								ExtendedPermission.ADMINISTRATION},
						auth));
		dto.setPermisRead(
				isGrantedAny(
						id,
						classePermisos,
						new Permission[] {
								ExtendedPermission.READ,
								ExtendedPermission.ADMINISTRATION},
						auth));
		dto.setPermisWrite(
				isGrantedAny(
						id,
						classePermisos,
						new Permission[] {
								ExtendedPermission.WRITE,
								ExtendedPermission.ADMINISTRATION},
						auth));
		dto.setPermisDelete(
				isGrantedAny(
						id,
						classePermisos,
						new Permission[] {
								ExtendedPermission.DELETE,
								ExtendedPermission.ADMINISTRATION},
						auth));
		dto.setPermisSupervision(
				isGrantedAny(
						id,
						classePermisos,
						new Permission[] {
								ExtendedPermission.SUPERVISION,
								ExtendedPermission.ADMINISTRATION},
						auth));
		dto.setPermisReassignment(
				isGrantedAny(
						id,
						classePermisos,
						new Permission[] {
								ExtendedPermission.REASSIGNMENT,
								ExtendedPermission.ADMINISTRATION},
						auth));
		dto.setPermisAdministration(
				isGrantedAll(
						id,
						classePermisos,
						new Permission[] {
								ExtendedPermission.ADMINISTRATION},
						auth));
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
	private void assignarPermisos(
			Sid sid,
			Class<?> objectClass,
			Serializable objectIdentifier,
			Permission[] permissions,
			boolean netejarAbans) {
		ObjectIdentity oid = new ObjectIdentityImpl(objectClass, objectIdentifier);
		MutableAcl acl = null;
		try {
			acl = (MutableAcl)aclService.readAclById(oid);
		} catch (NotFoundException nfex) {
			acl = aclService.createAcl(oid);
		}
		if (netejarAbans) {
			// Es recorren girats perque cada vegada que s'esborra un ace
			// es reorganitzen els índexos
			for (int i = acl.getEntries().size() - 1; i >= 0; i--) {
				AccessControlEntry ace = acl.getEntries().get(i);
				if (ace.getSid().equals(sid))
					acl.deleteAce(i);
			}
		}
		aclService.updateAcl(acl);
		for (Permission permission: permissions) {
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
			Permission[] permissions,
			Authentication auth) {
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities())
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
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
							false);
					ps.clear();
				} catch (NotFoundException ex) {}
			}
		} catch (NotFoundException ex) {}
		return granted;
	}

	private Permission[] getPermissionsFromPermis(PermisDto permis) {
		List<Permission> permissions = new ArrayList<Permission>();
		if (permis.isRead())
			permissions.add(ExtendedPermission.READ);
		if (permis.isWrite())
			permissions.add(ExtendedPermission.WRITE);
		if (permis.isCreate())
			permissions.add(ExtendedPermission.CREATE);
		if (permis.isDelete())
			permissions.add(ExtendedPermission.DELETE);
		if (permis.isAdministration())
			permissions.add(ExtendedPermission.ADMINISTRATION);
		return permissions.toArray(new Permission[permissions.size()]);
	}

	private String getMapeigRol(String rol) {
		/*String propertyMapeig = 
				(String)PropertiesHelper.getProperties().get(
						"es.caib.helium.mapeig.rol." + rol);
		if (propertyMapeig != null)
			return propertyMapeig;
		else*/
			return rol;
	}



	public interface ObjectIdentifierExtractor<T> {
		public Long getObjectIdentifier(T object);
	}

}
