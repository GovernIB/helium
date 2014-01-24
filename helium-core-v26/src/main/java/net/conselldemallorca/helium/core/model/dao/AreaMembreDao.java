/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.AreaMembre;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus AreaMembre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AreaMembreDao extends HibernateGenericDao<AreaMembre, Long> {

	public AreaMembreDao() {
		super(AreaMembre.class);
	}

	public List<AreaMembre> findAmbUsuariCodi(String usuariCodi) {
		return findByCriteria(Restrictions.eq("codi", usuariCodi));
	}

}
