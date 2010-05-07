/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import net.conselldemallorca.helium.model.hibernate.AreaMembre;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus AreaMembre
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class AreaMembreDao extends HibernateGenericDao<AreaMembre, Long> {

	public AreaMembreDao() {
		super(AreaMembre.class);
	}

}
