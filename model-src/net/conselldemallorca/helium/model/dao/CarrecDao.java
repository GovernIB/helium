/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.model.hibernate.Carrec;
import net.conselldemallorca.helium.model.hibernate.Entorn;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus Carrec
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class CarrecDao extends HibernateGenericDao<Carrec, Long> {

	public CarrecDao() {
		super(Carrec.class);
	}

	public List<Carrec> findPagedAndOrderedAmbEntorn(
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
	public Carrec findAmbEntornICodi(
			Long entornId,
			String codi) {
		List<Carrec> carrecs = findByCriteria(
				Restrictions.eq("entorn", (Entorn)getSession().get(Entorn.class, entornId)),
				Restrictions.eq("codi", codi));
		if (carrecs.size() > 0)
			return carrecs.get(0);
		return null;
	}
	public List<Carrec> findAmbArea(
			Long areaId) {
		return findByCriteria(
				Restrictions.eq("area.id", areaId));
	}
	public Carrec findAmbAreaICodi(
			Long areaId,
			String codi) {
		List<Carrec> carrecs = findByCriteria(
				Restrictions.eq("area.id", areaId),
				Restrictions.eq("codi", codi));
		if (carrecs.size() > 0)
			return carrecs.get(0);
		return null;
	}

}
