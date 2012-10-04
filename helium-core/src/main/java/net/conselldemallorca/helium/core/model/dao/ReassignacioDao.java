package net.conselldemallorca.helium.core.model.dao;

import java.util.Calendar;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus reassignació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Repository
public class ReassignacioDao extends HibernateGenericDao<Reassignacio, Long> {

	public ReassignacioDao() {
		super(Reassignacio.class);
	}

	public List<Reassignacio> findLlistaActius() {
		return findByCriteria(
				Restrictions.ge("dataFi", Calendar.getInstance().getTime()),
				Restrictions.isNull("dataCancelacio"));
	}
	
	public List<Reassignacio> findLlistaActius(Long tipusExpedientId) {
		return findByCriteria(
				Restrictions.ge("dataFi", Calendar.getInstance().getTime()),
				Restrictions.isNull("dataCancelacio"),
				Restrictions.eq("tipusExpedientId",tipusExpedientId));
	}
	
	public List<Reassignacio> findLlistaActiusModificacio(Long id) {
		return findByCriteria(
				Restrictions.ge("dataFi", Calendar.getInstance().getTime()),
				Restrictions.isNull("dataCancelacio"),
				Restrictions.eq("id", id));
	}
	
	

	public Reassignacio findByUsuari(String responsable) {
		List<Reassignacio> reassignacions = findByCriteria(
				Restrictions.eq("usuariOrigen", responsable),
				Restrictions.le("dataInici", Calendar.getInstance().getTime()),
				Restrictions.ge("dataFi", Calendar.getInstance().getTime()),
				Restrictions.isNull("dataCancelacio"));
		if (reassignacions.size() > 0)
			return reassignacions.get(0);
		return null;
	}
}
