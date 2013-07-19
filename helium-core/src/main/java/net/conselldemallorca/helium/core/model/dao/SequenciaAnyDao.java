/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes tipus SequenciaAny
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class SequenciaAnyDao extends HibernateGenericDao<SequenciaAny, Long> {

	public SequenciaAnyDao() {
		super(SequenciaAny.class);
	}
	
	public void clearSequencies() {
		getSession().createQuery("delete from SequenciaAny sa where sa.expedientTipus is null").executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public SequenciaAny findAmbExpedientTipusIAny(Long expedientTipusId, int any) {
		List<SequenciaAny> sas = (List<SequenciaAny>)getSession().createQuery(
				"from " +
				"    SequenciaAny sa " +
				"where " +
				"    sa.expedientTipus.id=? " +
				"and sa.any=?").
		setLong(0, expedientTipusId).
		setInteger(1, any).
		list();
		if (sas.size() > 0) {
			return sas.get(0);
		}
		return null;
	}
}
