/**
 * 
 */
package net.conselldemallorca.helium.core.security.j2ee;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Permis;
import net.conselldemallorca.helium.core.model.service.PermisService;
import net.conselldemallorca.helium.core.util.GlobalProperties;

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
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolesBasedMappableAttributesRetriever implements MappableAttributesRetriever {

	private SessionFactory sessionFactory;
	private PermisService permisService;

	private List<String> defaultMappableAttributes;

	private List<String> mappableAttributes = new ArrayList<String>();



	public String[] getMappableAttributes() {
		if (permisService != null) {
			refrescarMappableAttributes();
		}
		return mappableAttributes.toArray(new String[mappableAttributes.size()]);
	}

	public void setDefaultMappableAttributes(List<String> defaultMappableAttributes) {
		this.defaultMappableAttributes = defaultMappableAttributes;
	}

	@Autowired
	public void setPermisService(PermisService permisService) {
		this.permisService = permisService;
	}
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void refrescarMappableAttributes() {
		mappableAttributes.clear();
		if (defaultMappableAttributes != null)
			mappableAttributes.addAll(defaultMappableAttributes);
		String source = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
		if (source.equalsIgnoreCase("helium")) {
			for (Permis permis: permisService.findAll()) {
				String codi = permis.getCodi();
				if (!mappableAttributes.contains(codi))
					mappableAttributes.add(codi);
			}
		} else {
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
			for (String grup: grups) {
				if (!mappableAttributes.contains(grup))
					mappableAttributes.add(grup);				
			}
		}
	}

}
