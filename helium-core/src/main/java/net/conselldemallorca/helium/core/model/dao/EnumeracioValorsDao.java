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

	public List<EnumeracioValors> findAmbEnumeracio(Long enumeracioId) {
		return findByCriteria(
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
		List<EnumeracioValors> valors = findAmbEnumeracio(enumeracioId);
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
