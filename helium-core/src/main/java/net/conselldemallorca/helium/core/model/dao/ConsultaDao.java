/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Consulta;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus Consulta
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class ConsultaDao extends HibernateGenericDao<Consulta, Long> {

	public ConsultaDao() {
		super(Consulta.class);
	}

	public List<Consulta> findAmbEntorn(Long entornId) {
		return findByCriteria(Restrictions.eq("entorn.id", entornId));
	}
	public List<Consulta> findAmbEntornIExpedientTipus(Long entornId, Long expedientTipusId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("expedientTipus.id", expedientTipusId));
	}
	public Consulta findAmbEntornExpedientTipusICodi(
			Long entornId,
			Long expedientTipusId,
			String codi) {
		List<Consulta> consultes = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("expedientTipus.id", expedientTipusId),
				Restrictions.eq("codi", codi));
		if (consultes.size() > 0)
			return consultes.get(0);
		return null;
	}

}
