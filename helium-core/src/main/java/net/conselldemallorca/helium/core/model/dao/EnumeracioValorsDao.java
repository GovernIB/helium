/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus Enumeracio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class EnumeracioValorsDao extends HibernateGenericDao<EnumeracioValors, Long> {

	public EnumeracioValorsDao() {
		super(EnumeracioValors.class);
	}

	public int getNextOrder(Long enumeracioId) {
		Object result = getSession().createQuery(
				"select " +
				"	 max(ev.ordre) " +
				"from " +
				"    EnumeracioValors ev " +
				"where " +
				"    ev.enumeracio.id=?").
				setLong(0, enumeracioId).uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}

	public EnumeracioValors getAmbOrdre(Long enumeracioId, int ordre) {
		return (EnumeracioValors)getSession().createQuery(
				"from " +
				"    EnumeracioValors ev " +
				"where " +
				"    ev.enumeracio.id=? " +
				"and ev.ordre=?").
				setLong(0, enumeracioId).
				setInteger(1, ordre).
				uniqueResult();
	}

	public List<EnumeracioValors> findAmbEnumeracioOrdenat(Long enumeracioId) {
		return findOrderedByCriteria(
				new String[] {"ordre", "id"},
				true,
				Restrictions.eq("enumeracio.id", enumeracioId));
	}

	public EnumeracioValors findAmbCodi(String codi) {
		List<EnumeracioValors> list =  findByCriteria(Restrictions.eq("codi", codi));
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public EnumeracioValors findAmbEnumeracioICodi(Long enumeracioId, String codi) {
		List<EnumeracioValors> list = findByCriteria(
				Restrictions.eq("enumeracio.id", enumeracioId),
				Restrictions.eq("codi", codi));
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public List<ParellaCodiValor> getLlistaValors(Long enumeracioId) {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		List<EnumeracioValors> valors = findAmbEnumeracioOrdenat(enumeracioId);
		if (valors != null) {
			for (int i = 0; i < valors.size(); i++) {
				EnumeracioValors enumValors = valors.get(i);
				if (enumValors != null)
					resposta.add(new ParellaCodiValor(enumValors.getCodi(), enumValors.getNom()));
			}
		}
		return resposta;
	}

}
