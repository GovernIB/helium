/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import net.conselldemallorca.helium.model.update.Versio;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus versio
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class VersioDao extends HibernateGenericDao<Versio, Long> {

	public VersioDao() {
		super(Versio.class);
	}

}
