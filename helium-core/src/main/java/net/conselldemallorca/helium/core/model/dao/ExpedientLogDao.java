/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus log d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientLogDao extends HibernateGenericDao<ExpedientLog, Long> {

	public ExpedientLogDao() {
		super(ExpedientLog.class);
	}

	public List<ExpedientLog> findAmbExpedientIdOrdenatsPerData(Long expedientId) {
		return findOrderedByCriteria(
				new String[] {"data"},
				true,
				Restrictions.eq("expedient.id", expedientId));
	}

	public List<ExpedientLog> findAmbExpedientRetroceditIdOrdenatsPerData(Long iniciadorId) {
		return findOrderedByCriteria(
				new String[] {"data"},
				true,
				Restrictions.eq("iniciadorRetroces", iniciadorId));
	}
	
	@SuppressWarnings("unchecked")
	public List<ExpedientLog> findLogsTascaByIdOrdenatsPerData(Long targetId) {
		return (List<ExpedientLog>)getSession().
				createQuery(
						"from " +
						"    ExpedientLog l " +
						"where " +
						"    l.targetId=? " +
						"order by data").
				setString(0, targetId.toString()).
				list();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ExpedientLog> findLogsTascaById(String logId) {
		return (List<ExpedientLog>)getSession().
				createQuery(
						" from " +
								" ExpedientLog l " +
								" where " +
								" l.targetId = ? ").
				setString(0, logId).
				list();
	}
	
	
	public Object findLogIdTascaById(String logId, String estat) {
		return getSession().
				createQuery(
						"select l.id, l.estat " +
						" from " +
						" ExpedientLog l " +
						" where " +
						" l.targetId = ? " +
						" and l.id = " +
						" (select min(id) "+
						" from ExpedientLog "+
						" where targetId = ?) ").
				setString(0, logId).setString(1, estat).
				uniqueResult();
	}
	
	
	public ExpedientLog findAmbJbpmLogId(Long jbpmLogId) {
		List<ExpedientLog> logs = findByCriteria(
				Restrictions.eq("jbpmLogId", jbpmLogId));
		if (logs.size() > 0)
			return logs.get(0);
		else
			return null;
	}

}
