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
		ObjectIdentifierExtractor<Long> oie = new ObjectIdentifierExtractor<Long>() {
			public Long getObjectIdentifier(Long id) {
				return id;
			}
		};
		List<Long> idsAmbPermisRead = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisWrite = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisCreate = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.CREATE,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisDelete = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.DELETE,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisAdministration = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisCancel = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.CANCEL,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisStop = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.STOP,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisRelate = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisDataManage = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisDocManage = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.DOC_MANAGE,
						ExtendedPermission.ADMINISTRATION});

		List<Long> idsAmbPermisTermManage = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.TERM_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisTaskManage = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.TASK_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisTaskSuperv = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.TASK_SUPERV,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisTaskAssign = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.TASK_ASSIGN,
						ExtendedPermission.REASSIGNMENT,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisLogRead = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisLogManage = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.LOG_MANAGE,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisTokenRead = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.TOKEN_READ,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisTokenManage = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.TOKEN_MANAGE,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisDesignAdmin = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.DESIGN_ADMIN,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisDesignDeleg = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.DESIGN_DELEG,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisScriptExe = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.SCRIPT_EXE,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisUndoEnd = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.UNDO_END,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisDefprocUpdate = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.DEFPROC_UPDATE,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisDesign = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.DESIGN,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisOrganization = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.ORGANIZATION,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisSupervision = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisManage = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.MANAGE,
						ExtendedPermission.ADMINISTRATION});
		List<Long> idsAmbPermisReassignment = filtrarIdsPermisos(
				ids,
				oie,
				classePermisos,
				new Permission[] {
						ExtendedPermission.REASSIGNMENT,
						ExtendedPermission.ADMINISTRATION});
		for (int i = 0; i < ids.size(); i++) {
			Long id = ids.get(i);
			ControlPermisosDto dto = dtos.get(i);
			dto.setPermisRead(
					idsAmbPermisRead.contains(id));
			dto.setPermisWrite(
					idsAmbPermisWrite.contains(id));
			dto.setPermisCreate(
					idsAmbPermisCreate.contains(id));
			dto.setPermisDelete(
					idsAmbPermisDelete.contains(id));
			dto.setPermisAdministration(
					idsAmbPermisAdministration.contains(id));
			dto.setPermisCancel(
					idsAmbPermisCancel.contains(id));
			dto.setPermisStop(
					idsAmbPermisStop.contains(id));
			dto.setPermisRelate(
					idsAmbPermisRelate.contains(id));
			dto.setPermisDataManagement(
					idsAmbPermisDataManage.contains(id));
			dto.setPermisDocManagement(
					idsAmbPermisDocManage.contains(id));
			dto.setPermisTermManagement(
					idsAmbPermisTermManage.contains(id));
			dto.setPermisTaskManagement(
					idsAmbPermisTaskManage.contains(id));
			dto.setPermisTaskSupervision(
					idsAmbPermisTaskSuperv.contains(id));
			dto.setPermisTaskAssign(
					idsAmbPermisTaskAssign.contains(id));
			dto.setPermisLogRead(
					idsAmbPermisLogRead.contains(id));
			dto.setPermisLogManage(
					idsAmbPermisLogManage.contains(id));
			dto.setPermisTokenRead(
					idsAmbPermisTokenRead.contains(id));
			dto.setPermisTokenManage(
					idsAmbPermisTokenManage.contains(id));
			dto.setPermisDesignAdmin(
					idsAmbPermisDesignAdmin.contains(id));
			dto.setPermisDesignDeleg(
					idsAmbPermisDesignDeleg.contains(id));
			dto.setPermisScriptExe(
					idsAmbPermisScriptExe.contains(id));
			dto.setPermisUndoEnd(
					idsAmbPermisUndoEnd.contains(id));
			dto.setPermisDefprocUpdate(
					idsAmbPermisDefprocUpdate.contains(id));
			dto.setPermisDesign(
					idsAmbPermisDesign.contains(id));
			dto.setPermisOrganization(
					idsAmbPermisOrganization.contains(id));
			dto.setPermisSupervision(
					idsAmbPermisSupervision.contains(id));
			dto.setPermisManage(
					idsAmbPermisManage.contains(id));
			dto.setPermisReassignment(
					idsAmbPermisReassignment.contains(id));
		}
	}

	public void omplirControlPermisosSegonsUsuariActual(
			Long id,
			ControlPermisosDto dto,
			Class<?> classePermisos) {
		List<Long> ids = new ArrayList<Long>();
		ids.add(id);
		List<ControlPermisosDto> dtos = new ArrayList<ControlPermisosDto>();
		dtos.add(dto);
		omplirControlPermisosSegonsUsuariActual(
				ids,
				dtos,
				classePermisos);
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
					permis = permisosRol.get(grantedAuthority);
					if (permis == null) {
						permis = new PermisDto();
						permis.setId((Long)ace.getId());
						permis.setPrincipalNom(grantedAuthority);
						permis.setPrincipalTipus(PrincipalTipusEnumDto.ROL);
						permisosRol.put(grantedAuthority, permis);
					}
				}
				if (permis != null) {
					updatePermisAmbPermission(
							permis,
							ace.getPermission());
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
							true);
					ps.clear();
				} catch (NotFoundException ex) {}
			}
		} catch (NotFoundException ex) {}
		return granted;
	}

	private Permission[] getPermissionsFromPermis(PermisDto permis) {
		List<Permission> permissions = new ArrayList<Permission>();
		/* Permisos bàsics */
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
		/* Permisos extesos */
		if (permis.isCancel())
			permissions.add(ExtendedPermission.CANCEL);
		if (permis.isStop())
			permissions.add(ExtendedPermission.STOP);
		if (permis.isRelate())
			permissions.add(ExtendedPermission.RELATE);
		if (permis.isDataManagement())
			permissions.add(ExtendedPermission.DATA_MANAGE);
		if (permis.isDocManagement())
			permissions.add(ExtendedPermission.DOC_MANAGE);
		if (permis.isTermManagement())
			permissions.add(ExtendedPermission.TERM_MANAGE);
		if (permis.isTaskManagement())
			permissions.add(ExtendedPermission.TASK_MANAGE);
		if (permis.isTaskSupervision())
			permissions.add(ExtendedPermission.TASK_SUPERV);
		if (permis.isTaskAssign())
			permissions.add(ExtendedPermission.TASK_ASSIGN);
		if (permis.isLogRead())
			permissions.add(ExtendedPermission.LOG_READ);
		if (permis.isLogManage())
			permissions.add(ExtendedPermission.LOG_MANAGE);
		if (permis.isTokenRead())
			permissions.add(ExtendedPermission.TOKEN_READ);
		if (permis.isTokenManage())
			permissions.add(ExtendedPermission.TOKEN_MANAGE);
		if (permis.isDesignAdmin())
			permissions.add(ExtendedPermission.DESIGN_ADMIN);
		if (permis.isDesignDeleg())
			permissions.add(ExtendedPermission.DESIGN_DELEG);
		if (permis.isScriptExe())
			permissions.add(ExtendedPermission.SCRIPT_EXE);
		if (permis.isUndoEnd())
			permissions.add(ExtendedPermission.UNDO_END);
		if (permis.isDefprocUpdate())
			permissions.add(ExtendedPermission.DEFPROC_UPDATE);
		/* Permisos antics */
		if (permis.isDesign())
			permissions.add(ExtendedPermission.DESIGN);
		if (permis.isOrganization())
			permissions.add(ExtendedPermission.ORGANIZATION);
		if (permis.isSupervision())
			permissions.add(ExtendedPermission.SUPERVISION);
		if (permis.isManage())
			permissions.add(ExtendedPermission.MANAGE);
		if (permis.isReassignment())
			permissions.add(ExtendedPermission.REASSIGNMENT);
		return permissions.toArray(new Permission[permissions.size()]);
	}

	private void updatePermisAmbPermission(
			PermisDto permis,
			Permission permission) {
		/* Permisos bàsics */
		if (ExtendedPermission.READ.equals(permission))
			permis.setRead(true);
		if (ExtendedPermission.WRITE.equals(permission))
			permis.setWrite(true);
		if (ExtendedPermission.CREATE.equals(permission))
			permis.setCreate(true);
		if (ExtendedPermission.DELETE.equals(permission))
			permis.setDelete(true);
		if (ExtendedPermission.ADMINISTRATION.equals(permission))
			permis.setAdministration(true);
		/* Permisos extesos */
		if (ExtendedPermission.CANCEL.equals(permission))
			permis.setCancel(true);
		if (ExtendedPermission.STOP.equals(permission))
			permis.setStop(true);
		if (ExtendedPermission.RELATE.equals(permission))
			permis.setRelate(true);
		if (ExtendedPermission.DATA_MANAGE.equals(permission))
			permis.setDataManagement(true);
		if (ExtendedPermission.DOC_MANAGE.equals(permission))
			permis.setDocManagement(true);
		if (ExtendedPermission.TERM_MANAGE.equals(permission))
			permis.setTermManagement(true);
		if (ExtendedPermission.TASK_MANAGE.equals(permission))
			permis.setTaskManagement(true);
		if (ExtendedPermission.TASK_SUPERV.equals(permission))
			permis.setTaskSupervision(true);
		if (ExtendedPermission.TASK_ASSIGN.equals(permission))
			permis.setTaskAssign(true);
		if (ExtendedPermission.LOG_READ.equals(permission))
			permis.setLogRead(true);
		if (ExtendedPermission.LOG_MANAGE.equals(permission))
			permis.setLogManage(true);
		if (ExtendedPermission.TOKEN_READ.equals(permission))
			permis.setTokenRead(true);
		if (ExtendedPermission.TOKEN_MANAGE.equals(permission))
			permis.setTokenManage(true);
		if (ExtendedPermission.DESIGN_ADMIN.equals(permission))
			permis.setDesignAdmin(true);
		if (ExtendedPermission.DESIGN_DELEG.equals(permission))
			permis.setDesignDeleg(true);
		if (ExtendedPermission.SCRIPT_EXE.equals(permission))
			permis.setScriptExe(true);
		if (ExtendedPermission.UNDO_END.equals(permission))
			permis.setUndoEnd(true);
		if (ExtendedPermission.DEFPROC_UPDATE.equals(permission))
			permis.setDefprocUpdate(true);
		/* Permisos antics */
		if (ExtendedPermission.DESIGN.equals(permission))
			permis.setDesign(true);
		if (ExtendedPermission.ORGANIZATION.equals(permission))
			permis.setOrganization(true);
		if (ExtendedPermission.SUPERVISION.equals(permission))
			permis.setSupervision(true);
		if (ExtendedPermission.MANAGE.equals(permission))
			permis.setManage(true);
		if (ExtendedPermission.REASSIGNMENT.equals(permission))
			permis.setReassignment(true);
	}

	private List<Long> filtrarIdsPermisos(
			Collection<Long> ids,
			ObjectIdentifierExtractor<Long> oie,
			Class<?> classePermisos,
			Permission[] permisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<Long> idsAmbPermis = new ArrayList<Long>();
		idsAmbPermis.addAll(ids);
		filterGrantedAny(
				idsAmbPermis,
				oie,
				classePermisos,
				permisos,
				auth);
		return idsAmbPermis;
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
