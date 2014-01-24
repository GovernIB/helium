/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;

import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus camp de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CampRegistreDao extends HibernateGenericDao<CampRegistre, Long> {

	public CampRegistreDao() {
		super(CampRegistre.class);
	}

	public int getNextOrder(Long registreId) {
		Object result = getSession().createQuery(
				"select " +
				"	 max(cr.ordre) " +
				"from " +
				"    CampRegistre cr " +
				"where " +
				"    cr.registre.id=?").
				setLong(0, registreId).uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}

	public CampRegistre getAmbOrdre(Long registreId, int ordre) {
		return (CampRegistre)getSession().createQuery(
				"from " +
				"    CampRegistre cr " +
				"where " +
				"    cr.registre.id=? " +
				"and cr.ordre=?").
				setLong(0, registreId).
				setInteger(1, ordre).
				uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<CampRegistre> findAmbRegistreOrdenats(Long registreId) {
		return getSession().createQuery(
				"from " +
				"    CampRegistre cr " +
				"where " +
				"    cr.registre.id=? " +
				"order by " +
				"    cr.ordre").
				setLong(0, registreId).
				list();
	}

	public CampRegistre findAmbRegistreMembre(Long registreId, Long membreId) {
		return (CampRegistre)getSession().createQuery(
				"from " +
				"    CampRegistre cr " +
				"where " +
				"    cr.registre.id=? " +
				"and cr.membre.id=?").
				setLong(0, registreId).
				setLong(1, membreId).
				uniqueResult();
	}

	public CampRegistre findAmbRegistreCodi(Long tascaId, String membreCodi) {
		return (CampRegistre)getSession().createQuery(
				"from " +
				"    CampRegistre cr " +
				"where " +
				"    cr.registre.id=? " +
				"and cr.membre.codi=?").
				setLong(0, tascaId).
				setString(1, membreCodi).
				uniqueResult();
	}

}
