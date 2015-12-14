/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Accio;

import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus accio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
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
						"    a.definicioProces.id=? " +
						"order by a.nom").
				setLong(0, definicioProcesId).
				list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Accio> findVisiblesAmbDefinicioProces(Long definicioProcesId) {
		return (List<Accio>)getSession().
				createQuery(
						"from " +
						"    Accio a " +
						"where " +
						"    a.definicioProces.id=? " +
						"and a.oculta =?" +
						"order by a.nom").
				setLong(0, definicioProcesId).
				setBoolean(1, Boolean.FALSE).
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
						"and a.codi=? " +
						"order by a.nom").
				setLong(0, definicioProcesId).
				setString(1, codi).
				list();
		if (accions.size() > 0)
			return accions.get(0);
		return null;
	}

}
