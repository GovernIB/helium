/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import net.conselldemallorca.helium.core.model.update.Versio;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus versio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class VersioDao extends HibernateGenericDao<Versio, Long> {

	public VersioDao() {
		super(Versio.class);
	}

}
