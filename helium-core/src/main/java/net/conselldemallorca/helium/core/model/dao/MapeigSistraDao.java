/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;

import org.springframework.stereotype.Component;

/**
 * Dao pels objectes que representen l'estat d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MapeigSistraDao extends HibernateGenericDao<MapeigSistra, Long> {

	public MapeigSistraDao() {
		super(MapeigSistra.class);
	}

	@SuppressWarnings("unchecked")
	public List<MapeigSistra> findVariablesAmbExpedientTipusOrdenats(Long expedientTipusId) {
		return (List<MapeigSistra>)getSession().createQuery(
				"from " +
				"    MapeigSistra m " +
				"where " +
				"    m.expedientTipus.id = ? and " +
				"	 m.tipus = ? " + 
				"order by " +
				"    m.tipus, m.codiHelium").
		setLong(0, expedientTipusId).
		setInteger(1, MapeigSistra.TipusMapeig.Variable.ordinal()).		
		list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MapeigSistra> findDocumentsAmbExpedientTipusOrdenats(Long expedientTipusId) {
		return (List<MapeigSistra>)getSession().createQuery(
				"from " +
				"    MapeigSistra m " +
				"where " +
				"    m.expedientTipus.id = ? and " +
				"	 m.tipus = ? " + 
				"order by " +
				"    m.tipus, m.codiHelium").
		setLong(0, expedientTipusId).
		setInteger(1, MapeigSistra.TipusMapeig.Document.ordinal()).		
		list();
	}

	@SuppressWarnings("unchecked")
	public List<MapeigSistra> findAdjuntsAmbExpedientTipusOrdenats(Long expedientTipusId) {
		return (List<MapeigSistra>)getSession().createQuery(
				"from " +
				"    MapeigSistra m " +
				"where " +
				"    m.expedientTipus.id = ? and " +
				"	 m.tipus = ? " +
				"order by " +
				"    m.tipus, m.codiHelium").
		setLong(0, expedientTipusId).
		setInteger(1, MapeigSistra.TipusMapeig.Adjunt.ordinal()).
		list();
	}

	@SuppressWarnings("unchecked")
	public MapeigSistra findAmbExpedientTipusICodi(Long expedientTipusId, String codi) {
		List<MapeigSistra> mapeigs = (List<MapeigSistra>)getSession().createQuery(
				"from " +
				"    MapeigSistra m " +
				"where " +
				"    m.expedientTipus.id=? " +
				"and m.codiHelium=?").
		setLong(0, expedientTipusId).
		setString(1, codi).
		list();
		if (mapeigs.size() > 0) {
			return mapeigs.get(0);
		}
		return null;
	}

}
