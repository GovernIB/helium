/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import net.conselldemallorca.helium.core.model.hibernate.AreaMembre;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus AreaMembre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class AreaMembreDao extends HibernateGenericDao<AreaMembre, Long> {

	public AreaMembreDao() {
		super(AreaMembre.class);
	}

}
