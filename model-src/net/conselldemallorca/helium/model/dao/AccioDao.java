/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.model.hibernate.Accio;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus accio
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class AccioDao extends HibernateGenericDao<Accio, Long> {

	public AccioDao() {
		super(Accio.class);
	}

	@SuppressWarnings("unchecked")
	public List<Accio> findAmbDefinicioProces(Long definicioProcesId) {
		return (List<Accio>)getSession().
				createQuery(
						"from " +
						"    Accio a " +
						"where " +
						"    a.definicioProces.id=?").
				setLong(0, definicioProcesId).
				list();
	}

	@SuppressWarnings("unchecked")
	public Accio findAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		List<Accio> accions = (List<Accio>)getSession().
				createQuery(
						"from " +
						"    Accio a " +
						"where " +
						"    a.definicioProces.id=?" +
						"and a.codi=?").
				setLong(0, definicioProcesId).
				setString(1, codi).
				list();
		if (accions.size() > 0)
			return accions.get(0);
		return null;
	}

}
