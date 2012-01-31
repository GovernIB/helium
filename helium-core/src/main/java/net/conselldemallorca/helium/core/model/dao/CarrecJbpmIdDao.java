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



	@SuppressWarnings("unchecked")
	public List<CarrecJbpmId> findSenseAssignar() {
		return (List<CarrecJbpmId>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<CarrecJbpmId> resposta = new ArrayList<CarrecJbpmId>();
				Query query = session.createQuery(
						"select " +
						"    distinct m.role," +
						"    m.group.name " +
						"from " +
						"    org.jbpm.identity.Membership m " +
						"where " +
						"    (m.role, m.group.name) not in (" +
						"        select " +
						"            c.codi," +
						"            c.grup " +
						"        from " +
						"            CarrecJbpmId c) ");
				List<Object[]> files = query.list();
				for (Object[] fila: files) {
					CarrecJbpmId carrec = new CarrecJbpmId();
					carrec.setCodi((String)fila[0]);
					carrec.setGrup((String)fila[1]);
					resposta.add(carrec);
				}
				return resposta;
			}
		});
	}

	public CarrecJbpmId findAmbCodiGrup(String codi, String grup) {
		List<CarrecJbpmId> carrecs = findByCriteria(
				Restrictions.eq("codi", codi),
				Restrictions.eq("grup", grup));
		if (carrecs.size() > 0)
			return carrecs.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public String findPersonaAmbGrupICarrec(
			final String grup,
			final String carrec) {
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
						query.setString("codiGroup", grup);
						query.setString("codiCarrec", carrec);
						List<String> files = query.list();
						if (files.size() > 0)
							return files.get(0);
						return null;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<String> findCarrecsCodiAmbPersonaGrup(final String codiPersona, final String codiArea) {
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
	public List<String> findPersonesAmbGrup(final String group) {
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

	/* Per suprimir */
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
	public CarrecJbpmId findAmbCodi(String codi) {
		List<CarrecJbpmId> carrecs = findByCriteria(
				Restrictions.eq("codi", codi));
		if (carrecs.size() > 0)
			return carrecs.get(0);
		return null;
	}

}
