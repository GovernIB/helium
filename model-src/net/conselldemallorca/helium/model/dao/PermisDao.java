/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import net.conselldemallorca.helium.model.hibernate.Permis;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus permis
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class PermisDao extends HibernateGenericDao<Permis, String> {

	public PermisDao() {
		super(Permis.class);
	}
	
	public Permis getByCodi(String codi) {
		return (Permis)getSession()
						.createCriteria(getPersistentClass())
						.add(Restrictions.eq("codi", codi))
						.uniqueResult();
	}
}
