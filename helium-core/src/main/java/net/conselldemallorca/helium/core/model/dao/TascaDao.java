/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Tasca;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class TascaDao extends HibernateGenericDao<Tasca, Long> {

	public TascaDao() {
		super(Tasca.class);
	}

	@SuppressWarnings("unchecked")
	public List<Tasca> findAmbDefinicioProces(Long definicioProcesId) {
		return (List<Tasca>)getSession().
				createQuery(
						"from " +
						"    Tasca t " +
						"where " +
						"    t.definicioProces.id=?").
				setLong(0, definicioProcesId).
				list();
	}

	@SuppressWarnings("unchecked")
	public Tasca findAmbActivityNameIDefinicioProces(
			String activityName,
			Long definicioProcesId) {
		List<Tasca> llista = getSession().
				createQuery(
						"from " +
						"    Tasca t " +
						"where " +
						"    t.jbpmName=? " +
						"and t.definicioProces.id=?").
				setString(0, activityName).
				setLong(1, definicioProcesId).
				list();
		if (llista.size() > 0)
			return llista.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public Tasca findAmbActivityNameIProcessDefinitionId(
			String activityName,
			String processDefinitionId) {
		List<Tasca> llista = getSession().
				createQuery(
						"from " +
						"    Tasca t " +
						"where " +
						"    t.jbpmName=? " +
						"and t.definicioProces.jbpmId=?").
				setString(0, activityName).
				setString(1, processDefinitionId).
				list();
		if (llista.size() > 0)
			return llista.get(0);
		return null;
	}

}
