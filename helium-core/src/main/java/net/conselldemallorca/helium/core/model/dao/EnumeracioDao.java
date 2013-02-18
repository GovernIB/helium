/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus Enumeracio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EnumeracioDao extends HibernateGenericDao<Enumeracio, Long> {

	public EnumeracioDao() {
		super(Enumeracio.class);
	}

	public List<Enumeracio> findAmbEntornSenseTipusExp(Long entornId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.isNull("expedientTipus.id"));
	}
	public List<Enumeracio> findAmbEntornITipusExp(Long entornId, Long tipusExpedientId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("expedientTipus.id", tipusExpedientId));
	}
	public List<Enumeracio> findAmbEntornAmbOSenseTipusExp(Long entornId, Long tipusExpedientId) {
		List<Enumeracio> enumeracions = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.isNull("expedientTipus.id"));
		if (tipusExpedientId != null)
			enumeracions.addAll(findAmbEntornITipusExp(entornId, tipusExpedientId));
		return enumeracions;
	}
	public Enumeracio findAmbEntornICodi(Long entornId, String codi) {
		List<Enumeracio> enumeracions = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("codi", codi));
		if (enumeracions.size() > 0)
			return enumeracions.get(0);
		return null;
	}

	public Enumeracio findAmbEntornSenseTipusExpICodi(
			Long entornId,
			String codi) {
		List<Enumeracio> enumeracions = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.isNull("expedientTipus.id"),
				Restrictions.eq("codi", codi));
		if (enumeracions.size() > 0)
			return enumeracions.get(0);
		return null;
	}
	public Enumeracio findAmbEntornAmbTipusExpICodi(
			Long entornId,
			Long tipusExpedientId,
			String codi) {
		List<Enumeracio> enumeracions = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("expedientTipus.id", tipusExpedientId),
				Restrictions.eq("codi", codi));
		if (enumeracions.size() > 0)
			return enumeracions.get(0);
		return null;
	}
	public Enumeracio findAmbEntornAmbOSenseTipusExpICodi(
			Long entornId,
			Long tipusExpedientId,
			String codi) {
		Enumeracio e = findAmbEntornSenseTipusExpICodi(entornId, codi);
		if (e != null)
			return e;
		else
			return findAmbEntornAmbTipusExpICodi(entornId, tipusExpedientId, codi);
	}
}
