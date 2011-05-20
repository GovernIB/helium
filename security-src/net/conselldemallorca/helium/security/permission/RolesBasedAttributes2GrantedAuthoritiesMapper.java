package net.conselldemallorca.helium.security.permission;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.conselldemallorca.helium.util.GlobalProperties;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.authoritymapping.Attributes2GrantedAuthoritiesMapper;
import org.springframework.security.authoritymapping.MappableAttributesRetriever;
import org.springframework.util.StringUtils;


/**
 * Implementació de java.util.Map que inicialitza el contingut
 * amb el resultat d'una consulta a la taula JBPM_ID_GROUP.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("unchecked")
public class RolesBasedAttributes2GrantedAuthoritiesMapper implements Attributes2GrantedAuthoritiesMapper, MappableAttributesRetriever, InitializingBean {

	private Map attributes2grantedAuthoritiesMap = null;
	private String stringSeparator = ",";
	private List<String> mappableAttributes = new ArrayList<String>();

	private SessionFactory sessionFactory = null;
	private String filterPrefix = null;
	private Map toOverwriteAfterQuery = null;

	/**
	 * Check whether all properties have been set to correct values, and do some preprocessing.
	 */
	public void afterPropertiesSet() {
		queryGroups(sessionFactory);
		attributes2grantedAuthoritiesMap.putAll(toOverwriteAfterQuery);
		attributes2grantedAuthoritiesMap = preProcessMap(attributes2grantedAuthoritiesMap);
		try {
			for (String key: ((Map<String, Object>)attributes2grantedAuthoritiesMap).keySet())
				mappableAttributes.add(key);
		} catch ( ArrayStoreException ase ) {
			throw new IllegalArgumentException("attributes2grantedAuthoritiesMap contains non-String objects as keys");
		}
	}

	public void addRole(String role) {
		addRole(role, true);
	}
	public void addRole(String role, boolean doPreProcess) {
		if (filterPrefix == null || ((String)role).startsWith(filterPrefix)) {
			if (!attributes2grantedAuthoritiesMap.containsKey(role)) {
				attributes2grantedAuthoritiesMap.put(role, role);
				if (doPreProcess)
					attributes2grantedAuthoritiesMap = preProcessMap(attributes2grantedAuthoritiesMap);
			}
			if (!mappableAttributes.contains(role))
				mappableAttributes.add(role);
		}
	}
	public void removeRole(String role) {
		if (attributes2grantedAuthoritiesMap.containsKey(role)) {
			attributes2grantedAuthoritiesMap.remove(role);
			attributes2grantedAuthoritiesMap = preProcessMap(attributes2grantedAuthoritiesMap);
		}
		if (mappableAttributes.contains(role))
			mappableAttributes.remove(role);
	}

	/**
	 * Preprocess the given map
	 * @param orgMap The map to process
	 * @return the processed Map
	 */
	private Map preProcessMap(Map orgMap) {
		Map result = new HashMap(orgMap.size());
		Iterator it = orgMap.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry entry = (Map.Entry)it.next();
			result.put(entry.getKey(),getGrantedAuthorityCollection(entry.getValue()));
		}
		return result;
	}

	/**
	 * Convert the given value to a collection of Granted Authorities
	 *
	 * @param value
	 *            The value to convert to a GrantedAuthority Collection
	 * @return Collection containing the GrantedAuthority Collection
	 */
	private Collection getGrantedAuthorityCollection(Object value) {
		Collection result = new ArrayList();
		addGrantedAuthorityCollection(result,value);
		return result;
	}

	/**
	 * Convert the given value to a collection of Granted Authorities,
	 * adding the result to the given result collection.
	 *
	 * @param value
	 *            The value to convert to a GrantedAuthority Collection
	 * @return Collection containing the GrantedAuthority Collection
	 */
	private void addGrantedAuthorityCollection(Collection result, Object value) {
		if ( value != null ) {
			if ( value instanceof Collection ) {
				addGrantedAuthorityCollection(result,(Collection)value);
			} else if ( value instanceof Object[] ) {
				addGrantedAuthorityCollection(result,(Object[])value);
			} else if ( value instanceof String ) {
				addGrantedAuthorityCollection(result,(String)value);
			} else if ( value instanceof GrantedAuthority ) {
				result.add(value);
			} else {
				throw new IllegalArgumentException("Invalid object type: "+value.getClass().getName());
			}
		}
	}

	private void addGrantedAuthorityCollection(Collection result, Collection value) {
		Iterator it = value.iterator();
		while ( it.hasNext() ) {
			addGrantedAuthorityCollection(result,it.next());
		}
	}

	private void addGrantedAuthorityCollection(Collection result, Object[] value) {
		for ( int i = 0 ; i < value.length ; i++ ) {
			addGrantedAuthorityCollection(result,value[i]);
		}
	}

	private void addGrantedAuthorityCollection(Collection result, String value) {
		StringTokenizer st = new StringTokenizer(value,stringSeparator,false);
		while ( st.hasMoreTokens() ) {
			String nextToken = st.nextToken();
			if ( StringUtils.hasText(nextToken) ) {
				result.add(new GrantedAuthorityImpl(nextToken));
			}
		}
	}

	/**
	 * Map the given array of attributes to Spring Security GrantedAuthorities.
	 */
	public GrantedAuthority[] getGrantedAuthorities(String[] attributes) {
		List gaList = new ArrayList();
		for (int i = 0; i < attributes.length; i++) {
			Collection c = (Collection)attributes2grantedAuthoritiesMap.get(attributes[i]);
			if ( c != null ) {
				gaList.addAll(c);
			}
		}
		GrantedAuthority[] result = new GrantedAuthority[gaList.size()];
		result = (GrantedAuthority[])gaList.toArray(result);
		return result;
	}

	/**
	 *
	 * @see org.springframework.security.authoritymapping.MappableAttributesRetriever#getMappableAttributes()
	 */
	public String[] getMappableAttributes() {
		return mappableAttributes.toArray(new String[mappableAttributes.size()]);
	}
	/**
	 * @return Returns the stringSeparator.
	 */
	public String getStringSeparator() {
		return stringSeparator;
	}
	/**
	 * @param stringSeparator The stringSeparator to set.
	 */
	public void setStringSeparator(String stringSeparator) {
		this.stringSeparator = stringSeparator;
	}
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	public String getFilterPrefix() {
		return filterPrefix;
	}
	public void setFilterPrefix(String filterPrefix) {
		this.filterPrefix = filterPrefix;
	}
	public Map getToOverwriteAfterQuery() {
		return toOverwriteAfterQuery;
	}
	public void setToOverwriteAfterQuery(Map toOverwriteAfterQuery) {
		this.toOverwriteAfterQuery = toOverwriteAfterQuery;
	}

	private void queryGroups(SessionFactory sessionFactory) {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		List<String> grups = (List<String>)ht.executeWithNewSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String strQuery = "";
				String source = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
				if (source.equalsIgnoreCase("helium")) {
					strQuery = "select " +
								"	distinct codi " +
								"from " +
								"	net.conselldemallorca.helium.model.hibernate.Permis ";
				} else {
					strQuery = "select " +
								"    distinct name " +
								"from " +
								"    org.jbpm.identity.Group";
				}
				Query query = session.createQuery(strQuery);
				return query.list();
			}
		});
		attributes2grantedAuthoritiesMap = new HashMap<String, String>();
		for (String grup: grups)
			addRole(grup, false);
	}

}
