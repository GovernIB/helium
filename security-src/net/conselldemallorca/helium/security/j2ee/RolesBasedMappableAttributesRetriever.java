/**
 * 
 */
package net.conselldemallorca.helium.security.j2ee;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.model.hibernate.Permis;
import net.conselldemallorca.helium.model.service.PermisService;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.authoritymapping.MappableAttributesRetriever;

/**
 * Aconsegueix els rols possibles de la taula de permisos
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class RolesBasedMappableAttributesRetriever implements MappableAttributesRetriever {

	private SessionFactory sessionFactory;
	private PermisService permisService;

	private List<String> mappableAttributes = new ArrayList<String>();



	public String[] getMappableAttributes() {
		if (permisService != null) {
			refrescarMappableAttributes();
		}
		return mappableAttributes.toArray(new String[mappableAttributes.size()]);
	}

	@Autowired
	public void setPermisService(PermisService permisService) {
		this.permisService = permisService;
	}
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	private void refrescarMappableAttributes() {
		String source = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
		if (source.equalsIgnoreCase("helium")) {
			mappableAttributes.clear();
			for (Permis permis: permisService.findAll())
				mappableAttributes.add(permis.getCodi());
		} else {
			mappableAttributes.clear();
			HibernateTemplate ht = new HibernateTemplate(sessionFactory);
			List<String> grups = (List<String>)ht.executeWithNewSession(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Query query = session.createQuery("select " +
							"    distinct name " +
							"from " +
							"    org.jbpm.identity.Group");
					return query.list();
				}
			});
			mappableAttributes.addAll(grups);
		}
	}

}
