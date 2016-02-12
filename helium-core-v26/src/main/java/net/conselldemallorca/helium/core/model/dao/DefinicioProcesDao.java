/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus definicio procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
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
				new String[] {"id"},
				false,
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("jbpmKey", jbpmKey));
	}
	public List<DefinicioProces> findAmbEntornIJbpmIds(
			Long entornId,
			List<String> jbpmIds) {
		
		return findOrderedByCriteria(
				new String[] {"jbpmKey", "versio"},
				true,
				Restrictions.eq("entorn.id", entornId),
				Restrictions.in("jbpmId", jbpmIds));
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
				"    and dps.jbpmKey=dp.jbpmKey) " +
				"order by dp.jbpmKey")
				.setLong("entornId", entornId)
				.list();
	}

	public DefinicioProces findDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmName) {
		List<DefinicioProces> llistat = findOrderedByCriteria(
				new String[] {"versio"},
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
