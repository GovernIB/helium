/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.model.hibernate.TerminiIniciat;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus TerminiIniciat
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class TerminiIniciatDao extends HibernateGenericDao<TerminiIniciat, Long> {

	public TerminiIniciatDao() {
		super(TerminiIniciat.class);
	}

	public TerminiIniciat findAmbTerminiIdIProcessInstanceId(
			Long terminiId,
			String processInstanceId) {
		List<TerminiIniciat> terminis = findByCriteria(
				Restrictions.eq("termini.id", terminiId),
				Restrictions.eq("processInstanceId", processInstanceId));
		if (terminis.size() > 0) {
			return terminis.get(0);
		}
		return null;
	}

	public List<TerminiIniciat> findAmbProcessInstanceId(String processInstanceId) {
		return findByCriteria(
				Restrictions.eq("processInstanceId", processInstanceId));
	}

	public List<TerminiIniciat> findAmbTaskInstanceId(Long taskInstanceId) {
		return findByCriteria(
				Restrictions.eq("taskInstanceId", taskInstanceId.toString()));
	}

	public List<TerminiIniciat> findAmbTaskInstanceIds(String[] taskInstanceIds) {
		return findByCriteria(
				Restrictions.in("taskInstanceId", taskInstanceIds));
	}

	public List<TerminiIniciat> findIniciatsActius() {
		return findByCriteria(
				Restrictions.isNull("dataAturada"),
				Restrictions.isNull("dataCancelacio"));
	}

}
