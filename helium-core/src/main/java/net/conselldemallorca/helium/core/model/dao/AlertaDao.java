/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Alerta;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus Alerta
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class AlertaDao extends HibernateGenericDao<Alerta, Long> {

	public AlertaDao() {
		super(Alerta.class);
	}

	public List<Alerta> findActivesAmbEntornIUsuari(Long entornId, String usuariCodi) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("destinatari", usuariCodi),
				Restrictions.isNull("dataEliminacio"));
	}

}
