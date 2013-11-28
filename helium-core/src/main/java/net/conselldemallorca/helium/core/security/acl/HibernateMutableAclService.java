/**
 * 
 */
package net.conselldemallorca.helium.core.security.acl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.Authentication;
import org.springframework.security.acls.Acl;
import org.springframework.security.acls.AlreadyExistsException;
import org.springframework.security.acls.ChildrenExistException;
import org.springframework.security.acls.MutableAcl;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.jdbc.AclCache;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.sid.GrantedAuthoritySid;
import org.springframework.security.acls.sid.PrincipalSid;
import org.springframework.security.acls.sid.Sid;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.util.Assert;

/**
 * Servei per consultar ACLs amb hibernate
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("unchecked")
public class HibernateMutableAclService extends HibernateAclService implements MutableAclService {

	Session currentSession;

	private AclCache aclCache;



	public HibernateMutableAclService(
			SessionFactory sessionFactory,
			LookupStrategy lookupStrategy,
			AclCache aclCache) {
		super(lookupStrategy);
		Assert.notNull(sessionFactory, "sessionFactory required");
		setSessionFactory(sessionFactory);
		getHibernateTemplate().setAllowCreate(false);
		this.aclCache = aclCache;
	}

	public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
		Assert.notNull(objectIdentity, "Object Identity required");
		if (findAclObjectIdentity(objectIdentity) != null) {
			throw new AlreadyExistsException("Object identity '" + objectIdentity + "' already exists");
		}
		AclObjectIdentity newIdentity = new AclObjectIdentity();
		newIdentity.setObjectIdentity(new Long(objectIdentity.getIdentifier().toString()));
		// Cerca el Sid corresponent i si no el troba el crea
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		PrincipalSid sid = new PrincipalSid(auth);
		AclSid aclSid = createOrRetrieveAclSid(sid);
		newIdentity.setOwnerSid(aclSid);
		// Cerca el AclClass corresponent i si no el troba el crea
		AclClass aclClass = createOrRetrieveAclClass(objectIdentity.getJavaType().getName());
		newIdentity.setObjectClass(aclClass);
		newIdentity.setEntriesInheriting(true);
		getCurrentSession().saveOrUpdate(newIdentity);
		getCurrentSession().flush();
		// Retorna la acl fent una nova consulta per assegurar que tot s'omple correctament
		Acl resposta = readAclById(objectIdentity);
		Assert.isInstanceOf(MutableAcl.class, resposta, "MutableAcl should be been returned");
		return (MutableAcl)resposta;
	}

	public void deleteAcl(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException {
		AclObjectIdentity identity = findAclObjectIdentity(objectIdentity);
		if (identity != null) {
			if (!deleteChildren) {
				if (identity.getChildren().size() > 0)
					throw new ChildrenExistException("Cannot delete '" + objectIdentity + "' (has " + identity.getChildren().size() + " children)");
			}
			getCurrentSession().delete(identity);
		}
        aclCache.evictFromCache(objectIdentity);
	}

	public MutableAcl updateAcl(MutableAcl acl) throws NotFoundException {
		Assert.notNull(acl.getId(), "Object Identity doesn't provide an identifier");
		AclObjectIdentity identity = findAclObjectIdentity(acl.getObjectIdentity());
		// Esborra les entrades actuals
		if (identity != null) {
			List<AclEntry> entries = findAclEntries(identity);
//			for (AclEntry entry: identity.getEntries())
			for (AclEntry entry: entries)
				getCurrentSession().delete(entry);
			getCurrentSession().flush();
			identity.getEntries().clear();
		}
		// Afegeix les entrades noves
		for (int i = 0; i < acl.getEntries().length; i++) {
			Assert.isTrue(acl.getEntries()[i] instanceof AccessControlEntryImpl, "Unknown ACE class");
			AccessControlEntryImpl ace = (AccessControlEntryImpl)acl.getEntries()[i];
			AclEntry entry = new AclEntry();
			entry.setMask(ace.getPermission().getMask());
			entry.setAceOrder(i);
			entry.setGranting(ace.isGranting());
			entry.setAuditSuccess(ace.isAuditSuccess());
			entry.setAuditFailure(ace.isAuditFailure());
			entry.setIdentity(identity);
			entry.setSid(createOrRetrieveAclSid(ace.getSid()));
			if (!identity.getEntries().contains(entry)) {
				identity.addEntry(entry);
				getCurrentSession().save(entry);
			}
		}
		getCurrentSession().saveOrUpdate(identity);
		// Retorna la acl fent una nova consulta per assegurar que tot s'omple correctament
		Acl resposta = readAclById(acl.getObjectIdentity());
		Assert.isInstanceOf(MutableAcl.class, resposta, "MutableAcl should be been returned");
		aclCache.evictFromCache(acl.getObjectIdentity());
		return (MutableAcl)resposta;
	}



	private AclSid createOrRetrieveAclSid(Sid sid) {
		List<AclSid> sids = getAclSids(sid);
		if (sids.size() > 0) {
			return sids.get(0);
		} else {
			AclSid aclSid = newAclSidFromSid(sid);
			getCurrentSession().persist(aclSid);
			return aclSid;
		}
	}
	private AclClass createOrRetrieveAclClass(String javaType) {
		List<AclClass> classes = getClassesForType(javaType);
		if (classes.size() > 0) {
			return classes.get(0);
		} else {
			AclClass aclClass = new AclClass(javaType);
			getCurrentSession().persist(aclClass);
			return aclClass;
		}
	}
	private AclObjectIdentity findAclObjectIdentity(ObjectIdentity identity) {
		List<AclObjectIdentity> identities = getIdentitiesForObject(identity);
		if (identities.size() > 0)
			return identities.get(0);
		return null;
	}
	private List<AclObjectIdentity> getIdentitiesForObject(ObjectIdentity identity) {
		String identifier = identity.getIdentifier().toString();
		long id = (Long.valueOf(identifier)).longValue();
		String javaTypeName = identity.getJavaType().getName();
		Query q = getCurrentSession().createQuery(
				"from AclObjectIdentity oid where oid.objectIdentity=? and oid.objectClass.javaType=?");
		q.setParameter(0, id);
		q.setParameter(1, javaTypeName);
		return q.list();
		/*return getHibernateTemplate().find(
				"from AclObjectIdentity oid where oid.objectIdentity=? and oid.objectClass.javaType=?",
				new Object[] {id, javaType});*/
	}
	private List<AclEntry> findAclEntries(AclObjectIdentity identity) {
		Query q = getCurrentSession().createQuery(
				"from AclEntry e where e.identity=?");
		q.setParameter(0, identity);
		return q.list();
	}
	private List<AclClass> getClassesForType(String javaType) {
		Query q = getCurrentSession().createQuery(
				"from AclClass cls where cls.javaType=?");
		q.setParameter(0, javaType);
		return q.list();
		/*return getHibernateTemplate().find(
				"from AclClass cls where cls.javaType=?",
				new Object[] {javaType});*/
	}
	private List<AclSid> getAclSids(Sid sid) {
		AclSid aclSid = newAclSidFromSid(sid);
		Query q = getCurrentSession().createQuery(
			"from AclSid sid where sid.sid=? and sid.principal=?");
		q.setParameter(0, aclSid.getSid());
		q.setParameter(1, aclSid.isPrincipal());
		return q.list();
		/*return getHibernateTemplate().find(
				"from AclSid sid where sid.sid=? and sid.principal=?",
				new Object[] {aclSid.getSid(), aclSid.isPrincipal()});*/
	}
	private AclSid newAclSidFromSid(Sid sid) {
		String sidName = null;
		boolean isPrincipal;
		if (sid instanceof PrincipalSid) {
			sidName = ((PrincipalSid)sid).getPrincipal();
			isPrincipal = true;
		} else if (sid instanceof GrantedAuthoritySid) {
			sidName = ((GrantedAuthoritySid)sid).getGrantedAuthority();
			isPrincipal = false;
		} else {
			throw new IllegalArgumentException("Unsupported implementation of Sid");
		}
		return new AclSid(sidName, isPrincipal);
	}

	protected Session getCurrentSession() {
		try {
			return getSession();
		} catch (Exception ex) {
			if (currentSession == null)
				currentSession = getHibernateTemplate().getSessionFactory().openSession();
			return currentSession;
		}
	}

}
