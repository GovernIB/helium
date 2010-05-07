/**
 * 
 */
package net.conselldemallorca.helium.security.permission;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.util.GlobalProperties;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Implementació de java.util.Map que inicialitza el contingut
 * amb el resultat d'una consulta a la taula JBPM_ID_GROUP.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class JbpmIdGroupMap<K,V> extends HashMap<K,V>  {

	public JbpmIdGroupMap(SessionFactory sessionFactory, String filterPrefix) {
		queryGroups(sessionFactory, filterPrefix);
	}
	public JbpmIdGroupMap(SessionFactory sessionFactory) {
		queryGroups(sessionFactory, null);
	}

	public void setToOverwriteAfterQuery(Map<K,V> toOverwriteAfterQuery) {
		putAll(toOverwriteAfterQuery);
	}



	@SuppressWarnings("unchecked")
	private void queryGroups(SessionFactory sessionFactory, String filterPrefix) {
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		List<K> grups = (List<K>)ht.executeWithNewSession(new HibernateCallback() {
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
		for (K grup: grups) {
			if (filterPrefix == null || ((String)grup).startsWith(filterPrefix))
				put(grup, (V)grup);
		}
	}

}
