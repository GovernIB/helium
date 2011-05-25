/**
 * 
 */
package net.conselldemallorca.helium.core.security.acl;

import java.util.Map;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.security.acls.Acl;
import org.springframework.security.acls.AclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.sid.Sid;

/**
 * Servei per consultar ACLs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("unchecked")
public class HibernateAclService extends HibernateDaoSupport implements AclService {

	private LookupStrategy lookupStrategy;



	public HibernateAclService(LookupStrategy lookupStrategy) {
		this.lookupStrategy = lookupStrategy;
	}

	public ObjectIdentity[] findChildren(ObjectIdentity parentIdentity) {
		/*Object[] args = {parentIdentity.getIdentifier(), parentIdentity.getJavaType().getName()};
		List objects = jdbcTemplate.query(
				selectAclObjectWithParent,
				args,
				new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						String javaType = rs.getString("class");
						String identifier = rs.getString("obj_id");
						return new ObjectIdentityImpl(javaType, identifier);
					}
		});
		return (ObjectIdentityImpl[]) objects.toArray(new ObjectIdentityImpl[] {});*/
		return null;
    }
    public Acl readAclById(ObjectIdentity object, Sid[] sids)
        throws NotFoundException {
        Map map = readAclsById(new ObjectIdentity[] {object}, sids);
        if (map.size() == 0) {
            throw new NotFoundException("Could not find ACL");
        } else {
            return (Acl)map.get(object);
        }
    }
    public Acl readAclById(ObjectIdentity object) throws NotFoundException {
        return readAclById(object, null);
    }

    public Map readAclsById(ObjectIdentity[] objects) {
        return readAclsById(objects, null);
    }
    public Map readAclsById(ObjectIdentity[] objects, Sid[] sids)
        throws NotFoundException {
        return lookupStrategy.readAclsById(objects, sids);
    }






	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*@Override
	public ObjectIdentity[] findChildren(ObjectIdentity parentIdentity) {
		List<AclObjectIdentity> identities = getIdentitiesWithParent(parentIdentity);
		List<ObjectIdentity> resposta = new ArrayList<ObjectIdentity>();
		for (AclObjectIdentity identity: identities) {
			resposta.add(new ObjectIdentityImpl(
					identity.getObjectClass().getJavaType(),
					identity.getObjectIdentity()));
		}
		if (resposta.size() == 0)
			return null;
		return (ObjectIdentityImpl[]) resposta.toArray(new ObjectIdentityImpl[resposta.size()]);
	}

	@Override
	public Acl readAclById(ObjectIdentity object) throws NotFoundException {
		return readAclById(object, null);
	}

	@Override
	public Acl readAclById(ObjectIdentity object, Sid[] sids) throws NotFoundException {
		Map map = readAclsById(new ObjectIdentity[] {object}, sids);
		Assert.isTrue(map.containsKey(object), "There should have been an Acl entry for ObjectIdentity " + object);
		Acl acl = (Acl)map.get(object);
		return acl;
	}

	@Override
	public Map readAclsById(ObjectIdentity[] objects) throws NotFoundException {
		return readAclsById(objects, null);
	}

	@Override
	public Map<ObjectIdentity, Acl> readAclsById(ObjectIdentity[] objects, Sid[] sids) throws NotFoundException {
		Map<ObjectIdentity, Acl> result = new HashMap<ObjectIdentity, Acl>();
		for (int i = 0; i < objects.length; i++) {
			AclObjectIdentity identity = findAclObjectIdentity(objects[i]);
			if (identity != null) {
				ObjectIdentity oi = new ObjectIdentityImpl(
						identity.getObjectClass().getJavaType(),
						identity.getObjectIdentity());
				result.put(oi, aclFromAclObjectIdentity(identity));
			} else {
				throw new NotFoundException("Unable to find ACL information for object identity '" + objects[i] + "'");
			}
		}
		return result;
	}



	public void setAclAuthorizationStrategy(
			AclAuthorizationStrategy aclAuthorizationStrategy) {
		this.aclAuthorizationStrategy = aclAuthorizationStrategy;
	}

	public void setAuditLogger(AuditLogger auditLogger) {
		this.auditLogger = auditLogger;
	}



	private AclObjectIdentity findAclObjectIdentity(ObjectIdentity identity) {
		List<AclObjectIdentity> identities = getIdentitiesForObject(identity);
		if (identities.size() > 0)
			return identities.get(0);
		return null;
	}
	private Acl aclFromAclObjectIdentity(AclObjectIdentity identity) {
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(
				identity.getObjectClass().getJavaType(),
				identity.getObjectIdentity());
		Acl parentAcl = null;
		if (identity.getParentObject() != null)
			parentAcl = new StubAclParent(identity.getParentObject().getId());
		Sid owner;
		if (identity.getOwnerSid().isPrincipal())
			owner = new PrincipalSid(identity.getOwnerSid().getSid());
		else
			owner = new GrantedAuthoritySid(identity.getOwnerSid().getSid());
		Acl acl = new AclImpl(
				objectIdentity,
				identity.getId(),
				aclAuthorizationStrategy,
				auditLogger,
				parentAcl,
				null,
				identity.isEntriesInheriting(),
	            owner);
		for (AclEntry entry: identity.getEntries()) {
			Sid recipient;
			if (entry.getSid().isPrincipal())
				recipient = new PrincipalSid(entry.getSid().getSid());
			else
				recipient = new GrantedAuthoritySid(entry.getSid().getSid());
			Permission permission = ExtendedPermission.buildFromMask(entry.getMask());
			AccessControlEntryImpl ace = new AccessControlEntryImpl(
					entry.getId(),
					acl,
					recipient,
					permission,
					entry.isGranting(),
	                entry.isAuditSuccess(),
	                entry.isAuditFailure());
			Field acesField = FieldUtils.getField(AclImpl.class, "aces");
			List aces;
			try {
				acesField.setAccessible(true);
				aces = (List) acesField.get(acl);
			} catch (IllegalAccessException ex) {
				throw new IllegalStateException("Could not obtain AclImpl.ace field: cause[" + ex.getMessage() + "]");
			}
			if (!aces.contains(ace))
				aces.add(ace);
		}
		return acl;
	}
	private class StubAclParent implements Acl {
		private static final long serialVersionUID = 1L;
		private Long id;
		public StubAclParent(Long id) {
			this.id = id;
		}
		public AccessControlEntry[] getEntries() {
			throw new UnsupportedOperationException("Stub only");
		}
		public Long getId() {
			return id;
		}
		public ObjectIdentity getObjectIdentity() {
			throw new UnsupportedOperationException("Stub only");
		}
		public Sid getOwner() {
			throw new UnsupportedOperationException("Stub only");
		}
		public Acl getParentAcl() {
			throw new UnsupportedOperationException("Stub only");
		}
		public boolean isEntriesInheriting() {
			throw new UnsupportedOperationException("Stub only");
		}
		public boolean isGranted(Permission[] permission, Sid[] sids, boolean administrativeMode)
			throws NotFoundException, UnloadedSidException {
			throw new UnsupportedOperationException("Stub only");
		}
		public boolean isSidLoaded(Sid[] sids) {
			throw new UnsupportedOperationException("Stub only");
		}
	}

	private List<AclObjectIdentity> getIdentitiesWithParent(ObjectIdentity parentIdentity) {
		Query q = getCurrentSession().createQuery(
					"from AclObjectIdentity oid where oid.parentObject.id=? and oid.parentObject.objectClass.javaType=?");
		q.setParameter(0, parentIdentity.getIdentifier());
		q.setParameter(1, parentIdentity.getJavaType().getName());
		return q.list();
		/*List<AclObjectIdentity> identities = getHibernateTemplate().find(
				"from AclObjectIdentity oid where oid.parentObject.id=? and oid.parentObject.objectClass.javaType=?",
				new Object[] {parentIdentity.getIdentifier(), parentIdentity.getJavaType().getName()});
		return identities;*/
	/*}
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
	/*}

	protected Session getCurrentSession() {
		try {
			return getSession();
		} catch (Exception ex) {
			if (currentSession == null)
				currentSession = getHibernateTemplate().getSessionFactory().openSession();
			return currentSession;
		}
	}*/

}
