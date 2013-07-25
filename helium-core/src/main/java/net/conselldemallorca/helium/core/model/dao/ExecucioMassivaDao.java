/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes del tipus ExecucioMassiva.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class ExecucioMassivaDao extends HibernateGenericDao<ExecucioMassiva, Long> {

	public ExecucioMassivaDao() {
		super(ExecucioMassiva.class);
	}
	
	public List<ExecucioMassiva> getExecucionsMassivesActives() {
		return findByCriteria(
				Restrictions.and(
						Restrictions.le("dataInici", new Date()), 
						Restrictions.isNull("dataFi")));
	}
	
	public List<ExecucioMassiva> getExecucionsMassivesActivesByUser(String username) {
		return findByCriteria(
				Restrictions.and(
						Restrictions.ge("dataInici", new Date()), 
						Restrictions.isNull("dataFi")),
						Restrictions.eq("usuari", username));
	}
	
	@SuppressWarnings("unchecked")
	public List<ExecucioMassiva> getExecucionsMassivesByUser(String username, Long entorn, Integer results) {
		Query query = null;
		query = getSession().createQuery(
				"from	ExecucioMassiva e " +
				"where 	e.usuari =	'" + username + "' " +
				"and	(e.entorn is null or e.entorn = " + entorn + ")" +
				"order by e.dataInici desc, e.id desc");
		if (results != null)
				query.setMaxResults(results);

		return (List<ExecucioMassiva>)query.list();
	}
	
	public List<ExecucioMassiva> getExecucionsPendentsMassivesByUser(String username) {
		return findByCriteria(
				Restrictions.and(
						Restrictions.isNull("dataFi"),
						Restrictions.eq("usuari", username)));
	}
	
	public Long getNombreExecucionsMassivesActivesByUser(String username) {
		Query query = null;
		query = getSession().createQuery(
				"select count(e) " +
				"from	ExecucioMassiva e " +
				"where 	e.usuari =	'" + username + "' " +
				"and	e.dataFi is null");

		return (Long)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public String getExecucioMassivaTasca(Long execucioMassivaId) {
		String tasca = null;
		Query query = null;
		query = getSession().createQuery(
				"select e " +
				"from	ExecucioMassivaExpedient e " +
				"where 	e.execucioMassiva.id =	" + execucioMassivaId);
		List<ExecucioMassivaExpedient> exps = (List<ExecucioMassivaExpedient>)query.list();
		if (exps != null && !exps.isEmpty()) {
			tasca = exps.get(0).getTascaId();
		}
		return tasca;
	}
}
