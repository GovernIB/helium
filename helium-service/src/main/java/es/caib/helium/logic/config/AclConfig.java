package es.caib.helium.logic.config;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.helium.commons.config.BaseConfig;
import es.caib.helium.logic.security.ExtendedPermission;

/**
 * Configuració de les ACLs de Spring Security.
 *
 * @author Limit Tecnologies
 */
@Configuration
public class AclConfig {

	private static final boolean CLASS_ID_SUPPORTED = false;

	@PersistenceContext
	private EntityManager entityManager;

	@Value("${spring.jpa.properties.hibernate.dialect:#{null}}")
	private String hibernateDialect;

	@Autowired
	private DataSource dataSource;

	@Bean
	public SpringCacheBasedAclCache aclCache(CacheManager springCacheManager) {
		return new SpringCacheBasedAclCache(
				springCacheManager.getCache(CacheConfig.ACL_CACHE_NAME),
				permissionGrantingStrategy(),
				aclAuthorizationStrategy());
	}

	@Bean
	public PermissionGrantingStrategy permissionGrantingStrategy() {
		return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
	}

	@Bean
	public AclAuthorizationStrategy aclAuthorizationStrategy() {
		return new AclAuthorizationStrategy() {
			@Override
			public void securityCheck(Acl acl, int changeType) {
				if ((SecurityContextHolder.getContext() == null)
						|| (SecurityContextHolder.getContext().getAuthentication() == null)
						|| !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
					throw new AccessDeniedException(
							"Authenticated principal required to operate with ACLs");
				}
			}
		};
	}

	@Bean
	public PermissionFactory permissionFactory() {
		return new ExtendedPermissionFactory();
	}

	@Bean
	public LookupStrategy lookupStrategy(CacheManager springCacheManager) {
		BasicLookupStrategy lookupStrategy = new BasicLookupStrategy(
				dataSource,
				aclCache(springCacheManager),
				aclAuthorizationStrategy(),
				new ConsoleAuditLogger());
		String tableClass =  getPrefix() + "acl_class";
		String tableSid =  getPrefix() + "acl_sid";
		String tableOid =  getPrefix() + "acl_object_identity";
		String tableEntry =  getPrefix() + "acl_entry";
		String selectClause = "select " + tableOid + ".object_id_identity, "
				+ tableEntry + ".ace_order,  " + tableOid + ".id as acl_id, " +  tableOid + ".parent_object, "
				+ tableOid + ".entries_inheriting, " + tableEntry + ".id as ace_id, " + tableEntry + ".mask,  "
				+ tableEntry + ".granting,  " + tableEntry + ".audit_success, " + tableEntry + ".audit_failure,  "
				+ tableSid + ".principal as ace_principal, " + tableSid + ".sid as ace_sid,  "
				+ "acli_sid.principal as acl_principal, acli_sid.sid as acl_sid, " + tableClass + ".class ";
		String selectIdClause = ", " + tableClass + ".class_id_type  ";
		String fromClause = "from " + tableOid + " "
				+ "left join " + tableSid + " acli_sid on acli_sid.id = " +  tableOid + ".owner_sid "
				+ "left join " + tableClass + " on " + tableClass + ".id = " +  tableOid + ".object_id_class   "
				+ "left join " + tableEntry + " on " +  tableOid + ".id = " +  tableEntry + ".acl_object_identity "
				+ "left join " + tableSid + " on " + tableEntry + ".sid = " + tableSid + ".id  " + "where ( ";
		lookupStrategy.setPermissionFactory(permissionFactory());
		lookupStrategy.setAclClassIdSupported(CLASS_ID_SUPPORTED);
		lookupStrategy.setSelectClause(CLASS_ID_SUPPORTED ? selectClause + selectIdClause + fromClause : selectClause + fromClause);
		lookupStrategy.setLookupPrimaryKeysWhereClause("(" +  tableOid + ".id = ?)");
		lookupStrategy.setLookupObjectIdentitiesWhereClause("(" +  tableOid + ".object_id_identity = ? and " + tableClass + ".class = ?)");
		lookupStrategy.setOrderByClause(") order by " +  tableOid + ".object_id_identity asc, " +  tableEntry + ".ace_order asc");
		return lookupStrategy;
	}

	@Bean
	public JdbcMutableAclService aclService(CacheManager springCacheManager) {
		// S'han hagut de modificar els mètodes retrieveObjectIdentityPrimaryKey i findChildren per a
		// solucionar errors en les consultes quan el tipus de base de dades és PostgreSQL. Si forçam
		// que l'identificador del ObjectIdentity sigui un String dona error al executar la consulta
		// dient que no es pot convertir un bigint al tipus varchar.
		JdbcMutableAclService jdbcMutableAclService = new JdbcMutableAclService(dataSource, lookupStrategy(springCacheManager), aclCache(springCacheManager)) {
			protected Long retrieveObjectIdentityPrimaryKey(ObjectIdentity oid) {
				return super.retrieveObjectIdentityPrimaryKey(
						new ObjectIdentityImpl(
								oid.getType(),
								oid.getIdentifier().toString()));
			}
			public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
				return super.findChildren(
						new ObjectIdentityImpl(
								parentIdentity.getType(),
								parentIdentity.getIdentifier().toString()));
			}
		};
		String tableClass =  getPrefix() + "acl_class";
		String tableSid =  getPrefix() + "acl_sid";
		String tableOid =  getPrefix() + "acl_object_identity";
		String tableEntry =  getPrefix() + "acl_entry";

		if (hibernateDialect == null) {
			// Obté a partir del dialectectat de la connexió amb la BD.
			Session session = (Session) entityManager.getDelegate();
			SessionFactoryImpl sessionFactory = (SessionFactoryImpl) session.getSessionFactory();
			Dialect dialect = sessionFactory.getJdbcServices().getDialect();
			hibernateDialect = dialect.toString();
		}

		jdbcMutableAclService.setAclClassIdSupported(CLASS_ID_SUPPORTED);
		if (hibernateDialect.toLowerCase().contains("oracle") && isOracleSequenceLegacy()) {
			jdbcMutableAclService.setClassIdentityQuery("select " + tableClass.toUpperCase() + "_seq.currval from dual");
			jdbcMutableAclService.setSidIdentityQuery("select " + tableSid.toUpperCase() + "_seq.currval from dual");
		} else if (hibernateDialect.toLowerCase().contains("oracle") && !isOracleSequenceLegacy()) {
			jdbcMutableAclService.setClassIdentityQuery("select current_value('" + tableClass.toUpperCase() + "') from dual");
			jdbcMutableAclService.setSidIdentityQuery("select current_value('" + tableSid.toUpperCase() + "') from dual");
		} else if (hibernateDialect.toLowerCase().contains("postgres")) {
			jdbcMutableAclService.setClassIdentityQuery("select currval(pg_get_serial_sequence('" + tableClass + "', 'id'))");
			jdbcMutableAclService.setSidIdentityQuery("select currval(pg_get_serial_sequence('" + tableSid + "', 'id'))");
		} else if (hibernateDialect.toLowerCase().contains("hsql")) {
			jdbcMutableAclService.setClassIdentityQuery("call identity()");
			jdbcMutableAclService.setSidIdentityQuery("call identity()");
		}
		jdbcMutableAclService.setFindChildrenQuery("select obj.object_id_identity as obj_id, class.class as class" +
				(CLASS_ID_SUPPORTED ? ", class.class_id_type as class_id_type" : "") +
				" from " + tableOid + " obj, " + tableOid + " parent, " + tableClass + " class " +
				"where obj.parent_object = parent.id and obj.object_id_class = class.id " +
				"and parent.object_id_identity = ? and parent.object_id_class = (" +
				"select id FROM " + tableClass + " where " + tableClass + ".class = ?)");
		jdbcMutableAclService.setDeleteEntryByObjectIdentityForeignKeySql(
				"delete from " + tableEntry + " where acl_object_identity=?");
		jdbcMutableAclService.setDeleteObjectIdentityByPrimaryKeySql(
				"delete from " + tableOid + " where id=?");
		jdbcMutableAclService.setInsertClassSql(
				CLASS_ID_SUPPORTED ? "insert into " + tableClass + " (class, class_id_type) values (?, ?)" : "insert into " + tableClass + " (class) values (?)");
		jdbcMutableAclService.setInsertEntrySql(
				"insert into " + tableEntry + " " +
				"(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)" +
				"values (?, ?, ?, ?, ?, ?, ?)");
		jdbcMutableAclService.setInsertObjectIdentitySql(
				"insert into " + tableOid + " " +
				"(object_id_class, object_id_identity, owner_sid, entries_inheriting) " +
				"values (?, ?, ?, ?)");
		jdbcMutableAclService.setInsertSidSql(
				"insert into " + tableSid + " (principal, sid) values (?, ?)");
		jdbcMutableAclService.setClassPrimaryKeyQuery(
				"select id from " + tableClass + " where class=?");
		jdbcMutableAclService.setObjectIdentityPrimaryKeyQuery(
				"select " + tableOid + ".id from " + tableOid + ", " + tableClass + " " +
				"where " + tableOid + ".object_id_class = " + tableClass + ".id and " + tableClass + ".class=? " +
				"and " + tableOid + ".object_id_identity = ?");
		jdbcMutableAclService.setSidPrimaryKeyQuery(
				"select id from " + tableSid + " where principal=? and sid=?");
		jdbcMutableAclService.setUpdateObjectIdentity(
				"update " + tableOid + " set " +
				"parent_object = ?, owner_sid = ?, entries_inheriting = ?" + " where id = ?");
		return jdbcMutableAclService;
	}

	public String getIdsWithPermissionQuery(boolean anyPermission) {
		String tableClass =  getPrefix() + "acl_class";
		String tableSid =  getPrefix() + "acl_sid";
		String tableOid =  getPrefix() + "acl_object_identity";
		String tableEntry =  getPrefix() + "acl_entry";
		return "select " +
				"    distinct " + tableOid + ".object_id_identity id " +
				"from " +
				"    " + tableEntry + " " +
				"	 left join " + tableOid + " on " + tableOid + ".id = " + tableEntry + ".acl_object_identity " +
				"where " +
				"    " + tableEntry + ".granting = :isTrue " +
				(anyPermission ? "and " + tableEntry + ".mask in (:masks) " : "") +
				"and " + tableOid + ".object_id_class = (select id from " + tableClass + " where class = :className) " +
				"and ( " +
				"    " + tableEntry + ".sid in (select " + tableSid + ".id from " + tableSid + " where " + tableSid + ".principal = :isTrue and " + tableSid + ".sid = :principal) " +
				"    or " + tableEntry + ".sid in (select " + tableSid + ".id from " + tableSid + " where " + tableSid + ".principal = :isFalse and " + tableSid + ".sid in (:grantedAuthorities))) ";
	}

	public String getResourceIdsWithPermissionQuery(boolean anyPermission) {
		String tableClass =  getPrefix() + "acl_class";
		String tableSid =  getPrefix() + "acl_sid";
		String tableOid =  getPrefix() + "acl_object_identity";
		String tableEntry =  getPrefix() + "acl_entry";
		return "select " +
				"    distinct " + tableOid + ".object_id_identity " +
				"from " +
				"    " + tableEntry + " " +
				"    left join " + tableOid + " on " + tableOid + ".id = " + tableEntry + ".acl_object_identity " +
				"where " +
				"    " + tableEntry + ".granting = :isTrue " +
				(anyPermission ? "and " + tableEntry + ".mask in (:masks) " : "") +
				"and " + tableOid + ".object_id_class = (select id from " + tableClass + " where class = :className) " +
				"and (" + tableEntry + ".sid in (select " + tableSid + ".id from " + tableSid + " where " + tableSid + ".principal = :isPrincipal and " + tableSid + ".sid in (:sids))) ";
	}

	protected String getPrefix() {
		return BaseConfig.DB_PREFIX;
	}

	protected boolean isOracleSequenceLegacy() {
		return true;
	}

	private class ExtendedPermissionFactory extends DefaultPermissionFactory {
		private ExtendedPermissionFactory() {
			super();
			registerPublicPermissions(ExtendedPermission.class);
		}
	}

}
