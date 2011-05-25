/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus camp
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
	@SuppressWarnings("unchecked")
	public List<Camp> findAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId) {
		return (List<Camp>)getSession().
				createQuery(
						"from " +
						"    Camp c " +
						"where " +
						"    c.definicioProces.id=? " +
						"order by" +
						"    c.codi asc").
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

	public Camp getAmbOrdre(Long definicioProcesId, String agrupacioCodi, Integer ordre) {
		return (Camp)getSession().createQuery(
				"from Camp ca " +
				"where ca.definicioProces.id = ? " +
				"and ca.agrupacio.codi = ? " +
				"and ca.ordre = ?").
				setLong(0, definicioProcesId).
				setString(1, agrupacioCodi).
				setInteger(2, ordre).
				uniqueResult();
	}

	public Integer getNextOrdre(Long definicioProcesId, Long agrupacioId) {
		Object result = getSession().createQuery(
				"select max(ca.ordre) " +
				"from Camp ca " +
				"where ca.definicioProces.id = ? " +
				"and ca.agrupacio.id = ? ").
				setLong(0, definicioProcesId)
				.setLong(1, agrupacioId)
				.uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}

	@SuppressWarnings("unchecked")
	public List<Camp> findAmbDefinicioProcesIAgrupacioOrdenats(
			Long definicioProcesId, Long agrupacioId) {
		return (List<Camp>)getSession()
				.createQuery(
						"from Camp c " +
						"where c.definicioProces.id = ? " +
						"and c.agrupacio.id = ? " +
						"order by c.ordre asc ")
				.setLong(0, definicioProcesId)
				.setLong(1, agrupacioId)
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<Camp> findVariablesSenseAgrupacio(Long definicioProcesId) {
		return (List<Camp>)getSession()
				.createQuery(
						"from Camp c " +
						"where c.definicioProces.id = ? " +
						"and c.agrupacio is null " +
						"order by c.codi asc ")
				.setLong(0, definicioProcesId)
				.list();
	}

}
