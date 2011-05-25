/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.AreaTipus;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus AreaTipus
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class AreaTipusDao extends HibernateGenericDao<AreaTipus, Long> {

	public AreaTipusDao() {
		super(AreaTipus.class);
	}

	public List<AreaTipus> findPagedAndOrderedAmbEntorn(
			Long entornId,
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		return findPagedAndOrderedByCriteria(
				firstRow,
				maxResults,
				sort,
				asc,
				Restrictions.eq("entorn.id", entornId));
	}
	public int getCountAmbEntorn(
			Long entornId) {
		return getCountByCriteria(Restrictions.eq("entorn.id", entornId));
	}
	public List<AreaTipus> findAmbEntorn(
			Long entornId) {
		return findByCriteria(
				Restrictions.eq("entorn", (Entorn)getSession().get(Entorn.class, entornId)));
	}
	public AreaTipus findAmbEntornICodi(
			Long entornId,
			String codi) {
		List<AreaTipus> tipus = findByCriteria(
				Restrictions.eq("entorn", (Entorn)getSession().get(Entorn.class, entornId)),
				Restrictions.eq("codi", codi));
		if (tipus.size() > 0)
			return tipus.get(0);
		return null;
	}

}
