/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import net.conselldemallorca.helium.core.model.hibernate.Permis;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus permis
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
