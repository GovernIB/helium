/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.update.Versio;

import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus versio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class VersioDao extends HibernateGenericDao<Versio, Long> {

	public VersioDao() {
		super(Versio.class);
	}

	@SuppressWarnings("unchecked")
	public List<Versio> findAllOrdered() {
		return (List<Versio>)getSession().createQuery(
				"from " +
				"    Versio v " +
				"order by " +
				"    v.ordre asc").list();
	}

	@SuppressWarnings("unchecked")
	public Versio findAmbCodi(String codi) {
		List<Versio> versions = (List<Versio>)getSession().createQuery(
				"from " +
				"    Versio v " +
				"where " +
				"    v.codi=?").
		setString(0, codi).
		list();
		if (versions.size() > 0) {
			return versions.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Versio findLast() {
		List<Versio> versions = (List<Versio>)getSession().createQuery(
				"from " +
				"    Versio v " +
				"order by " +
				"    v.ordre desc").
		list();
		if (versions.size() > 0) {
			return versions.get(0);
		}
		return null;
	}

}
