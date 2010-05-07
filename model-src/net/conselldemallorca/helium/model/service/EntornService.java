/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.util.List;

import net.conselldemallorca.helium.model.dao.EntornDao;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Persona;
import net.conselldemallorca.helium.security.acl.AclServiceDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar els entorns
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class EntornService {

	private EntornDao entornDao;
	private AclServiceDao aclServiceDao;



	@Secured({"ROLE_ADMIN", "ROLE_USER", "AFTER_ACL_READ"})
	public Entorn getById(Long id) {
		return entornDao.getById(id, false);
	}
	@Secured({"ROLE_ADMIN", "AFTER_ACL_READ"})
	public Entorn create(Entorn entity) {
		Entorn saved = entornDao.saveOrUpdate(entity);
		aclServiceDao.createAcl(new ObjectIdentityImpl(Entorn.class, saved.getId()));
		return saved;
	}
	@Secured({"ROLE_ADMIN", "ROLE_USER", "AFTER_ACL_READ"})
	public Entorn update(Entorn entity) {
		return entornDao.merge(entity);
	}
	@Secured({"ROLE_ADMIN"})
	public void delete(Long id) {
		Entorn vell = getById(id);
		if (vell != null) {
			ObjectIdentity oid = new ObjectIdentityImpl(Entorn.class, id);
			aclServiceDao.deleteAcl(oid, true);
			entornDao.delete(id);
		}
	}
	@Secured({"ROLE_ADMIN", "AFTER_ACL_COLLECTION_READ"})
	public List<Entorn> findAll() {
		return entornDao.findAll();
	}
	@Secured({"ROLE_ADMIN"})
	public int countAll() {
		return entornDao.getCountAll();
	}
	@Secured({"ROLE_ADMIN", "AFTER_ACL_COLLECTION_READ"})
	public List<Entorn> findPagedAndOrderedAll(
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return entornDao.findPagedAndOrderedAll(sort, asc, firstRow, maxResults);
	}

	public Entorn findAmbCodi(String codi) {
		return entornDao.findAmbCodi(codi);
	}
	@Secured({"ROLE_ADMIN"})
	public List<Persona> findMembresEntorn(Long entornId) {
		return entornDao.findMembresEntorn(entornId);
	}

	/*@Secured({"ROLE_ADMIN"})
	public void afegirMembre(Long entornId, Long personaId, Set<Permission> permisos) {
		Entorn entorn = getById(entornId);
		if (entorn == null)
			throw new NotFoundException("No s'ha trobat l'entorn amb id:" + entornId);
		Persona persona = personaDao.getById(personaId, false);
		if (persona == null)
			throw new NotFoundException("No s'ha trobat la persona amb id:" + personaId);
		if (!entorn.getMembres().contains(persona))
			entorn.addMembre(persona);
		if (permisos != null) {
			aclServiceDao.deleteAclEntriesForSid(
					new PrincipalSid(persona.getCodi()),
					entorn.getId(),
					Entorn.class);
			for (Permission perm: permisos) {
				aclServiceDao.addAclEntry(
						new PrincipalSid(persona.getCodi()),
						entorn.getId(),
						Entorn.class,
						perm,
						true);
			}
		}
	}
	@Secured({"ROLE_ADMIN"})
	public void esborrarMembre(Long entornId, Long personaId) {
		Entorn entorn = getById(entornId);
		if (entorn == null)
			throw new NotFoundException("No s'ha trobat l'entorn amb id:" + entornId);
		Persona persona = personaDao.getById(personaId, false);
		if (persona == null)
			throw new NotFoundException("No s'ha trobat la persona amb id:" + entornId);
		// Esborra la persona com a membre
		entorn.removeMembre(persona);
		// Esborra tots els permisos d'aquesta persona per aquest entorn
		aclServiceDao.deleteAclEntriesForSid(
				new PrincipalSid(persona.getCodi()),
				entorn.getId(),
				Entorn.class);
	}
	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public Acl permisosMembresEntorn(Long entornId) {
		ObjectIdentity oid = new ObjectIdentityImpl(Entorn.class, entornId);
		try {
			return aclServiceDao.readAclById(oid);
		} catch (NotFoundException ex) {
			return aclServiceDao.createAcl(oid);
		}
	}*/

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public List<Entorn> findActius() {
		return entornDao.findActius();
	}

	/*@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public List<Entorn> findAccessibles() {
		List<Entorn> entorns = entornDao.findActius();
		List<Entorn> resposta = new ArrayList<Entorn>();
		for (Entorn entorn: entorns) {
			if (isAccessible(entorn))
				resposta.add(entorn);
		}
		return resposta;
	}
	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public Entorn getByIdIfAccessible(Long id) {
		Entorn entorn = entornDao.getById(id, false);
		if (entorn != null && entorn.isActiu() && isAccessible(entorn))
			return entorn;
		return null;
	}*/



	@Autowired
	public void setEntornDao(EntornDao entornDao) {
		this.entornDao = entornDao;
	}
	@Autowired
	public void setAclServiceDao(AclServiceDao aclServiceDao) {
		this.aclServiceDao = aclServiceDao;
	}



	/*private boolean isAccessible(Entorn entorn) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Set<Sid> sids = new HashSet<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities()) {
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		}
		try {
			Acl acl = aclServiceDao.readAclById(new ObjectIdentityImpl(Entorn.class, entorn.getId()));
			boolean grantedRead = false;
			try {
				grantedRead = acl.isGranted(
						new Permission[]{ExtendedPermission.READ},
						(Sid[])sids.toArray(new Sid[sids.size()]),
						false);
			} catch (NotFoundException ex) {}
			boolean grantedAdmin = false;
			try {
				grantedAdmin = acl.isGranted(
						new Permission[]{ExtendedPermission.ADMINISTRATION},
						(Sid[])sids.toArray(new Sid[sids.size()]),
						false);
			} catch (NotFoundException ex) {}
			return grantedRead || grantedAdmin;
		} catch (NotFoundException ex) {
			return false;
		}
	}
	private boolean isAccessible(Entorn entorn) {
		return isAccessible(entorn, null);
	}
	private boolean isAccessible(Entorn entorn, String usuari) {
		Sid sid = null;
		if (usuari == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			sid = new PrincipalSid(auth.getName());
		} else {
			sid = new PrincipalSid(usuari);
		}
		try {
			Acl acl = aclServiceDao.readAclById(new ObjectIdentityImpl(Entorn.class, entorn.getId()));
			return acl.isGranted(
					new Permission[]{ExtendedPermission.READ},
					new Sid[]{sid},
					false);
		} catch (org.springframework.security.acls.NotFoundException ignored) {}
		return false;
	}*/

}
