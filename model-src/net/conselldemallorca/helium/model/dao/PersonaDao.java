/**
 * 
 */
package net.conselldemallorca.helium.model.dao;


import java.util.List;

import net.conselldemallorca.helium.model.hibernate.Persona;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao per al plugin de persones
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class PersonaDao extends HibernateGenericDao<Persona, Long> {

	public PersonaDao() {
		super(Persona.class);
	}

	public List<Persona> findLikeNomSencer(String text) {
		return findByCriteria(
				Restrictions.ilike("nomSencer", "%" + text + "%"));
	}

	public Persona findAmbCodi(String codi) {
		List<Persona> persones = findByCriteria(
				Restrictions.eq("codi", codi));
		if (persones.size() > 0)
			return persones.get(0);
		return null;
	}

}
