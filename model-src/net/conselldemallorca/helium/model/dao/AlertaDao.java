/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.model.hibernate.Alerta;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus Alerta
 * 
 * @author Josep Gayà <josepg@limit.es>
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
