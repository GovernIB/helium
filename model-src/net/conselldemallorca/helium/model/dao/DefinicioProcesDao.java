/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.model.hibernate.DefinicioProces;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus definicio procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class DefinicioProcesDao extends HibernateGenericDao<DefinicioProces, Long> {

	public DefinicioProcesDao() {
		super(DefinicioProces.class);
	}

	public List<DefinicioProces> findAmbEntorn(
			Long entornId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId));
	}

	public DefinicioProces findAmbJbpmId(
			String jbpmId) {
		List<DefinicioProces> llistat = findByCriteria(
				Restrictions.eq("jbpmId", jbpmId));
		if (llistat.size() > 0)
			return llistat.get(0);
		return null;
	}

	public List<DefinicioProces> findAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey) {
		return findOrderedByCriteria(
				"id",
				false,
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("jbpmKey", jbpmKey));
	}
	public List<DefinicioProces> findAmbEntornExpedientTipusIJbpmKey(
			Long entornId,
			Long expedientTipusId,
			String jbpmKey) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("expedientTipus.id", expedientTipusId),
				Restrictions.eq("jbpmKey", jbpmKey));
	}

	@SuppressWarnings("unchecked")
	public List<DefinicioProces> findDarreresVersionsAmbEntorn(
			Long entornId) {
		return (List<DefinicioProces>)getSession().createQuery(
				"from " +
				"    DefinicioProces dp " +
				"where " +
				"    dp.entorn.id=:entornId " +
				"and dp.versio = (" +
				"    select " +
				"        max(dps.versio) " +
				"    from " +
				"        DefinicioProces dps " +
				"    where " +
				"        dps.entorn.id=:entornId " +
				"    and dps.jbpmKey=dp.jbpmKey)")
				.setLong("entornId", entornId)
				.list();
	}

	public DefinicioProces findDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmName) {
		List<DefinicioProces> llistat = findOrderedByCriteria(
				"versio",
				false,
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("jbpmKey", jbpmName));
		if (llistat.size() > 0)
			return llistat.get(0);
		return null;
	}

	public DefinicioProces findAmbJbpmKeyIVersio(
			String jbpmKey,
			int versio) {
		List<DefinicioProces> llistat = findByCriteria(
				Restrictions.eq("jbpmKey", jbpmKey),
				Restrictions.eq("versio", versio));
		if (llistat.size() > 0)
			return llistat.get(0);
		return null;
	}

}
