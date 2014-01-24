/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.CampTasca;

import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus camp de formulari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CampTascaDao extends HibernateGenericDao<CampTasca, Long> {

	public CampTascaDao() {
		super(CampTasca.class);
	}

	public int getNextOrder(Long tascaId) {
		Object result = getSession().createQuery(
				"select " +
				"	 max(ct.order) " +
				"from " +
				"    CampTasca ct " +
				"where " +
				"    ct.tasca.id=?").
				setLong(0, tascaId).uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}

	public CampTasca getAmbOrdre(Long tascaId, int order) {
		return (CampTasca)getSession().createQuery(
				"from " +
				"    CampTasca ct " +
				"where " +
				"    ct.tasca.id=? " +
				"and ct.order=?").
				setLong(0, tascaId).
				setInteger(1, order).
				uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<CampTasca> findAmbTascaOrdenats(Long tascaId) {
		List<CampTasca> resposta = (List<CampTasca>)getSession().createQuery(
				"from " +
				"    CampTasca ct " +
				"where " +
				"    ct.tasca.id=? " +
				"order by " +
				"    ct.order").
				setLong(0, tascaId).
				list();
		for (CampTasca camp: resposta)
			camp.getCamp().getValidacions().size();
		return resposta;
	}

	public CampTasca findAmbTascaCamp(Long tascaId, Long campId) {
		return (CampTasca)getSession().createQuery(
				"from " +
				"    CampTasca ct " +
				"where " +
				"    ct.tasca.id=? " +
				"and ct.camp.id=?").
				setLong(0, tascaId).
				setLong(1, campId).
				uniqueResult();
	}

	public CampTasca findAmbTascaCodi(Long tascaId, String campCodi) {
		return (CampTasca)getSession().createQuery(
				"from " +
				"    CampTasca ct " +
				"where " +
				"    ct.tasca.id=? " +
				"and ct.camp.codi=?").
				setLong(0, tascaId).
				setString(1, campCodi).
				uniqueResult();
	}

}
