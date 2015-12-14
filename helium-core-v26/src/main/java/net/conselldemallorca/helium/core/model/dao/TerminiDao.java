/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Termini;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus Termini
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class TerminiDao extends HibernateGenericDao<Termini, Long> {

	public TerminiDao() {
		super(Termini.class);
	}

	public List<Termini> findAmbDefinicioProces(Long definicioProcesId) {
		return findByCriteria(
				Restrictions.eq("definicioProces.id", definicioProcesId));
	}
	public Termini findAmbDefinicioProcesICodi(
			Long definicioProcesId,
			String codi) {
		List<Termini> terminis = findByCriteria(
				Restrictions.eq("definicioProces.id", definicioProcesId),
				Restrictions.eq("codi", codi));
		if (terminis.size() > 0)
			return terminis.get(0);
		return null;
	}

}
