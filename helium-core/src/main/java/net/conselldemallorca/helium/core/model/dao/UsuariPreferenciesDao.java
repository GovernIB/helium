/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import net.conselldemallorca.helium.core.model.hibernate.UsuariPreferencies;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus preferencies d'usuari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class UsuariPreferenciesDao extends HibernateGenericDao<UsuariPreferencies, String> {



	public UsuariPreferenciesDao() {
		super(UsuariPreferencies.class);
	}

}
