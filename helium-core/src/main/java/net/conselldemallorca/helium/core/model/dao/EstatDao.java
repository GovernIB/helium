/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Estat;

import org.springframework.stereotype.Component;

/**
 * Dao pels objectes que representen l'estat d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EstatDao extends HibernateGenericDao<Estat, Long> {

	public EstatDao() {
		super(Estat.class);
	}

	@SuppressWarnings("unchecked")
	public List<Estat> findAmbExpedientTipusOrdenats(Long expedientTipusId) {
		return (List<Estat>)getSession().createQuery(
				"from " +
				"    Estat e " +
				"where " +
				"    e.expedientTipus.id=? " +
				"order by " +
				"    e.ordre").
		setLong(0, expedientTipusId).
		list();
	}

	@SuppressWarnings("unchecked")
	public List<Estat> findAmbEntornOrdenats(Long entornId) {
		return (List<Estat>)getSession().createQuery(
				"from " +
				"    Estat e " +
				"where " +
				"    e.expedientTipus.entorn.id=? " +
				"order by " +
				"    e.expedientTipus, " +
				"    e.ordre").
		setLong(0, entornId).
		list();
	}

	@SuppressWarnings("unchecked")
	public Estat findAmbExpedientTipusICodi(Long expedientTipusId, String codi) {
		List<Estat> estats = (List<Estat>)getSession().createQuery(
				"from " +
				"    Estat e " +
				"where " +
				"    e.expedientTipus.id=? " +
				"and e.codi=?").
		setLong(0, expedientTipusId).
		setString(1, codi).
		list();
		if (estats.size() > 0) {
			return estats.get(0);
		}
		return null;
	}

	public int getSeguentOrdre(Long expedientTipusId) {
		Object result = getSession().createQuery(
				"select " +
				"	 max(e.ordre) " +
				"from " +
				"    Estat e " +
				"where " +
				"    e.expedientTipus.id=?").
				setLong(0, expedientTipusId).uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}
	public Estat getAmbOrdre(Long expedientTipusId, int ordre) {
		return (Estat)getSession().createQuery(
				"from " +
				"    Estat e " +
				"where " +
				"    e.expedientTipus.id=? " +
				"and e.ordre=?").
		setLong(0, expedientTipusId).
		setInteger(1, ordre).
		uniqueResult();
	}

}
