/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.AreaJbpmId;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus AreaJbpmId
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
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

	@SuppressWarnings({ "unchecked"})
	public List<String> findAmbUsuariCodi(final String usuariCodi) {
		Query query = getSession().createQuery(
				"select " +
				"    m.group.name " +
				"from " +
				"    org.jbpm.identity.Membership m " +
				"where " +
				"    m.user.name = :usuariCodi");
		query.setString("usuariCodi", usuariCodi);
		List<String> resposta = query.list();
		return resposta;
	}

	@SuppressWarnings({ "unchecked"})
	public List<AreaJbpmId> findSenseAssignar() {
		List<AreaJbpmId> resposta = new ArrayList<AreaJbpmId>();
		Query query = getSession().createQuery(
				"select " +
				"    g.name " +
				"from " +
				"    org.jbpm.identity.Group g " +
				"where " +
				"	 g.type = 'organisation'" +
				"    and g.name not in (" +
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

	@SuppressWarnings("unchecked")
	public List<String> findRolesAmbUsuariCodi(final String usuariCodi) {
		Query query = getSession().createQuery(
				"select distinct " +
				"    m.group.name " +
				"from " +
				"    org.jbpm.identity.Membership m " +
				"where " +
				"    m.user.name = :usuariCodi " +
				"	 and m.group.type = 'security-role'");
		query.setString("usuariCodi", usuariCodi);
		List<String> resposta = query.list();
		return resposta;
	}
	
	@SuppressWarnings({ "unchecked"})
	public List<AreaJbpmId> findDistinctJbpmGrupAll() {
		List<AreaJbpmId> resposta = new ArrayList<AreaJbpmId>();
		Query query = getSession().createQuery(
				"select distinct name from org.jbpm.identity.Group");
		List<String> files = query.list();
		for (String grup: files) {
			AreaJbpmId area = new AreaJbpmId();
			area.setCodi(grup);
			resposta.add(area);
		}
		return resposta;
	}
}
