/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;


import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Persona;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao per al plugin de persones
 * 
 * @author Limit Tecnologies <limit@limit.es>
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

	public List<Persona> findPagedAndOrderedFiltre(
			String sort,
			boolean asc,
			int firstRow,
			int maxResults,
			String codi,
			String nomLike,
			String emailLike) {
		List<Criterion> crits = new ArrayList<Criterion>();
		if (codi != null && codi.length() > 0)
			crits.add(Restrictions.eq("codi", codi));
		if (nomLike != null && nomLike.length() > 0)
			crits.add(Restrictions.ilike("nomSencer", "%" + nomLike + "%"));
		if (emailLike != null && emailLike.length() > 0)
			crits.add(Restrictions.ilike("email", "%" + emailLike + "%"));
		return findPagedAndOrderedByCriteria(
				firstRow,
				maxResults,
				sort,
				asc,
				getCriterionPerFiltre(codi, nomLike, emailLike));
	}

	public int getCountFiltre(
			String codi,
			String nomLike,
			String emailLike) {
		return getCountByCriteria(getCriterionPerFiltre(codi, nomLike, emailLike));
	}



	private Criterion[] getCriterionPerFiltre(
			String codi,
			String nomLike,
			String emailLike) {
		List<Criterion> crits = new ArrayList<Criterion>();
		if (codi != null && codi.length() > 0)
			crits.add(Restrictions.eq("codi", codi));
		if (nomLike != null && nomLike.length() > 0)
			crits.add(Restrictions.ilike("nom", "%" + nomLike + "%"));
		if (emailLike != null && emailLike.length() > 0)
			crits.add(Restrictions.ilike("email", "%" + emailLike + "%"));
		return crits.toArray(new Criterion[crits.size()]);
	}

}
