/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import net.conselldemallorca.helium.model.hibernate.UsuariPreferencies;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus preferencies d'usuari
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class UsuariPreferenciesDao extends HibernateGenericDao<UsuariPreferencies, String> {



	public UsuariPreferenciesDao() {
		super(UsuariPreferencies.class);
	}

}
