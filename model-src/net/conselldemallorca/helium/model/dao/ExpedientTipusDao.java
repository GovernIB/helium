/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes tipus d'expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class ExpedientTipusDao extends HibernateGenericDao<ExpedientTipus, Long> {

	public ExpedientTipusDao() {
		super(ExpedientTipus.class);
	}

	public List<ExpedientTipus> findAmbEntorn(Long entornId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId));
	}

	public List<ExpedientTipus> findAmbSistraTramitCodi(String tramitCodi) {
		return findByCriteria(
				Restrictions.eq("sistraTramitCodi", tramitCodi));
	}

	public ExpedientTipus findAmbEntornICodi(
			Long entornId,
			String codi) {
		List<ExpedientTipus> tipus = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("codi", codi));
		if (tipus.size() > 0)
			return tipus.get(0);
		return null;
	}

}
