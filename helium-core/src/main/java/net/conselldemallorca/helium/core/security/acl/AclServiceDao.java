/**
 * 
 */
package net.conselldemallorca.helium.core.security.acl;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.Acl;
import org.springframework.security.acls.AlreadyExistsException;
import org.springframework.security.acls.ChildrenExistException;
import org.springframework.security.acls.MutableAcl;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.security.acls.sid.Sid;

/**
 * Dao per gestionar ACLs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AclServiceDao {

	private MutableAclService mutableAclService;
	
	public AclServiceDao(MutableAclService mutableAclService) {
		this.mutableAclService = mutableAclService;
	}

	/*@SuppressWarnings("unchecked")
	public void addPrincipalAclEntry(
			String principal,
			Serializable id,
			Class clazz,
			Permission permission,
			boolean granting) {
		addAclEntry(new PrincipalSid(principal), id, clazz, permission, granting);
	}
	@SuppressWarnings("unchecked")
	public void addGrantedAuthorityAclEntry(
			String grantedAuthority,
			Serializable id,
			Class clazz,
			Permission permission,
			boolean granting) {
		addAclEntry(new GrantedAuthoritySid(grantedAuthority), id, clazz, permission, granting);
	}
	@SuppressWarnings("unchecked")
	public void addAclEntry(
			Sid recipient,
			Serializable id,
			Class clazz,
			Permission[] permissions,
			boolean granting) {
		MutableAcl acl;
		ObjectIdentity oid = new ObjectIdentityImpl(clazz.getCanonicalName(), id);
		try {
			acl = (MutableAcl)mutableAclService.readAclById(oid);
		} catch (NotFoundException nfe) {
			acl = mutableAclService.createAcl(oid);
		}
		boolean insertar;
		try {
			insertar = !acl.isGranted(new Permission[] {permission}, new Sid[] {recipient}, false);
		} catch (Exception ignored) {
			insertar = true;
		}
		if (insertar) {
			acl.insertAce(acl.getEntries().length, permission, recipient, true);
			mutableAclService.updateAcl(acl);
			if (logger.isDebugEnabled()) {
				logger.debug("Added permission " + permission + " for Sid " + recipient + " id " + id);
			}
		}
	}
	@SuppressWarnings("unchecked")
	public void addAclEntry(
			Sid recipient,
			Serializable id,
			Class clazz,
			Permission[] permissions,
			boolean granting) {
		MutableAcl acl;
		ObjectIdentity oid = new ObjectIdentityImpl(clazz.getCanonicalName(), id);
		try {
			acl = (MutableAcl)mutableAclService.readAclById(oid);
		} catch (NotFoundException nfe) {
			acl = mutableAclService.createAcl(oid);
		}
		boolean insertar;
		try {
			insertar = !acl.isGranted(new Permission[] {permission}, new Sid[] {recipient}, false);
		} catch (Exception ignored) {
			insertar = true;
		}
		if (insertar) {
			acl.insertAce(acl.getEntries().length, permission, recipient, true);
			mutableAclService.updateAcl(acl);
			if (logger.isDebugEnabled()) {
				logger.debug("Added permission " + permission + " for Sid " + recipient + " id " + id);
			}
		}
	}*/
	@SuppressWarnings("rawtypes")
	public void deleteAclEntry(
			Sid recipient,
			Serializable id,
			Class clazz,
			Permission permission,
			boolean granting) {
	    ObjectIdentity oid = new ObjectIdentityImpl(clazz.getCanonicalName(), id);
	    MutableAcl acl = (MutableAcl)mutableAclService.readAclById(oid);
	    // Remove all permissions associated with this particular recipient (string equality used to keep things simple)
	    AccessControlEntry[] entries = acl.getEntries();
	    for (int i = 0; i < entries.length; i++) {
			if (entries[i].getSid().equals(recipient) && entries[i].getPermission().equals(permission) && entries[i].isGranting() == granting) {
				acl.deleteAce(i);
			}
	    }
	    mutableAclService.updateAcl(acl);
	    if (logger.isDebugEnabled()) {
	    	logger.debug("Deleted securedObject " + id + " ACL permissions for recipient " + recipient);
	    }
	}
	@SuppressWarnings("rawtypes")
	public void deleteAclEntriesForSid(Sid recipient, Serializable id, Class clazz) {
		ObjectIdentity oid = new ObjectIdentityImpl(clazz.getCanonicalName(), id);
		try {
			MutableAcl acl = (MutableAcl)mutableAclService.readAclById(oid);
			boolean deleted;
			do {
				deleted = false;
				AccessControlEntry[] entries = acl.getEntries();
				for (int i = 0; i < entries.length; i++) {
					if (entries[i].getSid().equals(recipient)) {
						acl.deleteAce(i);
						deleted = true;
						break;
					}
				}
			} while (deleted);
			mutableAclService.updateAcl(acl);
			if (logger.isDebugEnabled()) {
				logger.debug("Deleted securedObject " + id + " ACL permissions for recipient " + recipient);
			}
		} catch (NotFoundException ignored) {}
	}

	public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
		return mutableAclService.createAcl(objectIdentity);
	}
	public void updateAcl(MutableAcl acl) {
		mutableAclService.updateAcl(acl);
	}
	public void deleteAcl(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException {
		mutableAclService.deleteAcl(objectIdentity, deleteChildren);
	}
	public Acl readAclById(ObjectIdentity objectIdentity) throws NotFoundException {
		return mutableAclService.readAclById(objectIdentity);
	}
	public MutableAcl readMutableAclById(ObjectIdentity objectIdentity) throws NotFoundException {
		return (MutableAcl)mutableAclService.readAclById(objectIdentity);
	}



	/*private String getUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getPrincipal() instanceof UserDetails) {
			return ((UserDetails)auth.getPrincipal()).getUsername();
		} else {
			return auth.getPrincipal().toString();
		}
	}*/

	private static Log logger = LogFactory.getLog(AclServiceDao.class);

}
