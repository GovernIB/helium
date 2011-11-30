/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.CarrecJbpmId;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus CarrecJbpmId
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class CarrecJbpmIdDao extends HibernateGenericDao<CarrecJbpmId, Long> {

	public CarrecJbpmIdDao() {
		super(CarrecJbpmId.class);
	}



	public CarrecJbpmId findAmbCodi(String codi) {
		List<CarrecJbpmId> carrecs = findByCriteria(
				Restrictions.eq("codi", codi));
		if (carrecs.size() > 0)
			return carrecs.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<CarrecJbpmId> findSenseAssignar() {
		return (List<CarrecJbpmId>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<CarrecJbpmId> resposta = new ArrayList<CarrecJbpmId>();
				Query query = session.createQuery(
						"select " +
						"    distinct m.role " +
						"from " +
						"    org.jbpm.identity.Membership m " +
						"where " +
						"    m.role not in (" +
						"        select " +
						"            c.codi " +
						"        from " +
						"            CarrecJbpmId c) ");
				List<String> files = query.list();
				for (String rol: files) {
					CarrecJbpmId carrec = new CarrecJbpmId();
					carrec.setCodi(rol);
					resposta.add(carrec);
				}
				return resposta;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<String> findPersonesAmbCarrecCodi(final String codi) {
		return (List<String>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(
						"select " +
						"    m.user.name " +
						"from " +
						"    org.jbpm.identity.Membership m " +
						"where " +
						"    m.role = :role");
				query.setString("role", codi);
				return query.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public String findPersonaAmbGroupICarrec(
			final String codiGroup,
			final String codiCarrec) {
		return (String)getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						Query query = session.createQuery(
								"select " +
								"    m.user.name " +
								"from " +
								"    org.jbpm.identity.Membership m " +
								"where " +
								"    m.group.name = :codiGroup " +
								"and m.role = :codiCarrec");
						query.setString("codiGroup", codiGroup);
						query.setString("codiCarrec", codiCarrec);
						List<String> files = query.list();
						if (files.size() > 0)
							return files.get(0);
						return null;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<String> findCarrecsCodiAmbPersonaArea(final String codiPersona, final String codiArea) {
		return (List<String>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(
						"select " +
						"    m.role " +
						"from " +
						"    org.jbpm.identity.Membership m " +
						"where " +
						"    m.user.name = :codiPersona " +
						"and m.group.name = :codiArea");
				query.setString("codiPersona", codiPersona);
				query.setString("codiArea", codiArea);
				return query.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<String> findPersonesAmbGroup(final String group) {
		return (List<String>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(
						"select " +
						"    m.user.name " +
						"from " +
						"    org.jbpm.identity.Membership m " +
						"where " +
						"    m.group.name = :group");
				query.setString("group", group);
				return query.list();
			}
		});
	}

}
