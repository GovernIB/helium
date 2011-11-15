/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.AreaJbpmId;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus AreaJbpmId
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class AreaJbpmIdDao extends HibernateGenericDao<AreaJbpmId, Long> {

	public AreaJbpmIdDao() {
		super(AreaJbpmId.class);
	}



	public AreaJbpmId findAmbCodi(String codi) {
		List<AreaJbpmId> carrecs = findByCriteria(
				Restrictions.eq("codi", codi));
		if (carrecs.size() > 0)
			return carrecs.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<AreaJbpmId> findSenseAssignar() {
		return (List<AreaJbpmId>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<AreaJbpmId> resposta = new ArrayList<AreaJbpmId>();
				Query query = session.createQuery(
						"select " +
						"    group.name " +
						"from " +
						"    org.jbpm.identity.Group g " +
						"where " +
						"    g.name not in (" +
						"        select " +
						"            a.codi " +
						"        from " +
						"            AreaJbpmId a) ");
				List<String> files = query.list();
				for (String grup: files) {
					AreaJbpmId area = new AreaJbpmId();
					area.setCodi(grup);
					resposta.add(area);
				}
				return resposta;
			}
		});
	}

}
