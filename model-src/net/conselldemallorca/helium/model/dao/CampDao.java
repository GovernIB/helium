/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.Camp.TipusCamp;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus camp
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class CampDao extends HibernateGenericDao<Camp, Long> {

	public CampDao() {
		super(Camp.class);
	}

	@SuppressWarnings("unchecked")
	public List<Camp> findAmbDefinicioProces(Long definicioProcesId) {
		return (List<Camp>)getSession().
				createQuery(
						"from " +
						"    Camp c " +
						"where " +
						"    c.definicioProces.id=?").
				setLong(0, definicioProcesId).
				list();
	}

	public Camp findAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		return (Camp)getSession().
				createQuery(
						"from " +
						"    Camp c " +
						"where " +
						"    c.definicioProces.id=? " +
						"and c.codi=?").
				setLong(0, definicioProcesId).
				setString(1, codi).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Camp> findAmbDefinicioProcesITipus(Long definicioProcesId, TipusCamp tipus) {
		return (List<Camp>)getSession().
				createQuery(
						"from " +
						"    Camp c " +
						"where " +
						"    c.definicioProces.id=? " +
						"and c.tipus=?").
				setLong(0, definicioProcesId).
				setParameter(1, tipus).
				list();
	}

	@SuppressWarnings("unchecked")
	public List<Camp> findAmbDefinicioProcesIMultiple(Long definicioProcesId) {
		return (List<Camp>)getSession().
				createQuery(
						"from " +
						"    Camp c " +
						"where " +
						"    c.definicioProces.id=? " +
						"and c.multiple=?").
				setLong(0, definicioProcesId).
				setBoolean(1, true).
				list();
	}

}
