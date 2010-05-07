/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.model.hibernate.Validacio;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus plantilla
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class ValidacioDao extends HibernateGenericDao<Validacio, Long> {

	public ValidacioDao() {
		super(Validacio.class);
	}

	public int getNextOrderPerTasca(Long tascaId) {
		Object result = getSession().createQuery(
				"select " +
				"	 max(v.ordre) " +
				"from " +
				"    Validacio v " +
				"where " +
				"    v.tasca.id=?").
				setLong(0, tascaId).uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}
	public Validacio getAmbOrdrePerTasca(Long tascaId, int ordre) {
		return (Validacio)getSession().createQuery(
				"from " +
				"    Validacio v " +
				"where " +
				"    v.tasca.id=? " +
				"and v.ordre=?").
				setLong(0, tascaId).
				setInteger(1, ordre).
				uniqueResult();
	}
	@SuppressWarnings("unchecked")
	public List<Validacio> findAmbTascaOrdenats(Long tascaId) {
		return (List<Validacio>)getSession().
				createQuery(
						"from " +
						"    Validacio v " +
						"where " +
						"    v.tasca.id=? " +
						"order by " +
						"    v.ordre").
				setLong(0, tascaId).
				list();
	}

	public int getNextOrderPerCamp(Long campId) {
		Object result = getSession().createQuery(
				"select " +
				"	 max(v.ordre) " +
				"from " +
				"    Validacio v " +
				"where " +
				"    v.camp.id=?").
				setLong(0, campId).uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}
	public Validacio getAmbOrdrePerCamp(Long campId, int ordre) {
		return (Validacio)getSession().createQuery(
				"from " +
				"    Validacio v " +
				"where " +
				"    v.camp.id=? " +
				"and v.ordre=?").
				setLong(0, campId).
				setInteger(1, ordre).
				uniqueResult();
	}
	@SuppressWarnings("unchecked")
	public List<Validacio> findAmbCampOrdenats(Long campId) {
		return (List<Validacio>)getSession().
				createQuery(
						"from " +
						"    Validacio v " +
						"where " +
						"    v.camp.id=? " +
						"order by " +
						"    v.ordre").
				setLong(0, campId).
				list();
	}

}
