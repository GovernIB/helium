/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes del tipus ExecucioMassiva.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class ExecucioMassivaDao extends HibernateGenericDao<ExecucioMassiva, Long> {

	public ExecucioMassivaDao() {
		super(ExecucioMassiva.class);
	}
	
	public List<ExecucioMassiva> getExecucionsMassivesActives() {
		return findByCriteria(
				Restrictions.and(
						Restrictions.le("dataInici", new Date()), 
						Restrictions.isNull("dataFi")));
	}
	
	public List<ExecucioMassiva> getExecucionsMassivesActivesByUser(String username) {
		return findByCriteria(
				Restrictions.and(
						Restrictions.le("dataInici", new Date()), 
						Restrictions.isNull("dataFi")),
						Restrictions.eq("usuari", username));
	}
}
