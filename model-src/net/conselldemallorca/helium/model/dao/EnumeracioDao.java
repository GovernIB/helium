/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.model.hibernate.Enumeracio;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus Enumeracio
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class EnumeracioDao extends HibernateGenericDao<Enumeracio, Long> {

	public EnumeracioDao() {
		super(Enumeracio.class);
	}

	public List<Enumeracio> findAmbEntorn(Long entornId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId));
	}
	public Enumeracio findAmbEntornICodi(
			Long entornId,
			String codi) {
		List<Enumeracio> enumeracions = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("codi", codi));
		if (enumeracions.size() > 0)
			return enumeracions.get(0);
		return null;
	}

}
