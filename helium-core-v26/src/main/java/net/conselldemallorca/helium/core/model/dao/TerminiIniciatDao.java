/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus TerminiIniciat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
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

	public List<TerminiIniciat> findIniciatsAmbAlertesPrevies() {
		List<TerminiIniciat> resultat = new ArrayList<TerminiIniciat>();
		
		String hql = "from TerminiIniciat as ti "
				+ "where "
				+ "    ti.dataCompletat is null " 
				+ "and (ti.termini.alertaPrevia = true and ti.alertaPrevia = false) " 
				+ "and ti.dataAturada is null "
				+ "and ti.dataCancelacio is null";
		
		Query query = getSession().createQuery(hql);
		
		resultat = (List<TerminiIniciat>) query.list();
		
		return resultat;
	}
	
	public List<TerminiIniciat> findIniciatsAmbAlertesFinals() {
		List<TerminiIniciat> resultat = new ArrayList<TerminiIniciat>();
		
		String hql = "from TerminiIniciat as ti "
				+ "where "
				+ "    ti.dataCompletat is null " 
				+ "and (ti.termini.alertaFinal = true and ti.alertaFinal = false) " 
				+ "and ti.dataAturada is null "
				+ "and ti.dataCancelacio is null";
		
		Query query = getSession().createQuery(hql);
		
		resultat = (List<TerminiIniciat>) query.list();
		
		return resultat;
	}

}
