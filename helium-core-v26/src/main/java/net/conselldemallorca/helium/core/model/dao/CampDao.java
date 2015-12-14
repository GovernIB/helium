/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;

import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus camp
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
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
	public Camp findAmbDefinicioProcesICodiSimple(Long definicioProcesId, String codi) {
		Camp camp = new Camp();
		Object obj = getSession().
				createQuery(
						"select " +
						"	c.dominiCampText, " +
						"	c.dominiCampValor, " +
						"	c.enumeracio.id, " +
						"	c.consulta.id," +
						"	c.consultaCampText " +
						"from " +
						"    Camp c " +
						"where " +
						"    c.definicioProces.id=? " +
						"and c.codi=?").
				setLong(0, definicioProcesId).
				setString(1, codi).uniqueResult();
		
		if (obj != null) {
			Object objCamp[] = (Object[])obj;
			camp.setDominiCampText(objCamp[0] == null ? null : (String)objCamp[0]);
			camp.setDominiCampValor(objCamp[1] == null ? null : (String)objCamp[1]);
			camp.setEnumeracio(objCamp[2] == null ? null : new Enumeracio());
			camp.setConsulta(objCamp[3] == null ? null : new Consulta());
			camp.setConsultaCampText(objCamp[4] == null ? null : (String)objCamp[4]);
		}
		
		return camp;
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
