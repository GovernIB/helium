/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus ConsultaCamp
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class ConsultaCampDao extends HibernateGenericDao<ConsultaCamp, Long> {

	public ConsultaCampDao() {
		super(ConsultaCamp.class);
	}

	@SuppressWarnings("unchecked")
	public List<ConsultaCamp> findAmbConsultaITipusOrdenats(Long consultaId, TipusConsultaCamp tipus) {
		return (List<ConsultaCamp>)getSession().
				createQuery(
						"from " +
						"    ConsultaCamp cc " +
						"where " +
						"    cc.consulta.id=? " +
						"and cc.tipus=? " +
						"order by " +
						"    cc.ordre").
				setLong(0, consultaId).
				setParameter(1, tipus).
				list();
	}

	public int getNextOrderPerTipus(Long consultaId, TipusConsultaCamp tipus) {
		Object result = getSession().createQuery(
				"select max(cc.ordre) " +
				"from ConsultaCamp cc " +
				"where cc.consulta.id = ? " +
				"and cc.tipus = ? ").
				setLong(0, consultaId).
				setParameter(1, tipus).
				uniqueResult();
		if (result == null)
			return 0;
		return ((Integer)result).intValue() + 1;
	}

	public ConsultaCamp getAmbTipusIOrdre(Long consultaId, TipusConsultaCamp tipus, int ordre) {
		return (ConsultaCamp)getSession().createQuery(
				"from " +
				"    ConsultaCamp cc " +
				"where " +
				"    cc.consulta.id=? " +
				"and cc.tipus=? " +
				"and cc.ordre=?").
				setLong(0, consultaId).
				setParameter(1, tipus).
				setInteger(2, ordre).
				uniqueResult();
	}/*

	@SuppressWarnings("unchecked")
	public List<Object> findProcessosExpedient(Long consultaId) {
		return getSession().createQuery(
				"select distinct dp.jbpmKey " +
				"from Consulta c, " +
				"	DefinicioProces dp " +
				"where c.id = ? " +
				"and c.expedientTipus = dp.expedientTipus ")
				.setLong(0, consultaId)
				.list();
	}*/

	@SuppressWarnings("unchecked")
	public List<Camp> findCampsProces(Long consultaId, String defprocJbpmKey) {
		return getSession().createQuery(
				"select ca " +
				"from Camp ca " +
				"	left join ca.definicioProces dp, " +
				"	Consulta c " +
				"	left join c.expedientTipus et " +
				"where c.id = ? " +
				"and ((dp.expedientTipus.id = et.id) or (dp.expedientTipus.id is null)) " +
				"and ca.definicioProces.id = dp.id " +
				"and (ca.codi, dp.id) " +
				"	in ( " +
				"		select cm.codi, max(cm.definicioProces.id) " +
				"		from Camp cm " +
				"		group by cm.codi, cm.tipus " +
				"	) " +
				"and dp.jbpmKey = ? " +
				"order by ca.codi asc ")
				.setLong(0, consultaId)
				.setString(1, defprocJbpmKey)
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<ConsultaCamp> findCampsConsulta(Long consultaId, TipusConsultaCamp tipus) {
		return getSession().createQuery(
				"select cc " +
				"from ConsultaCamp cc " +
				"where cc.consulta.id = ? " +
				"and cc.tipus = ? " +
				"order by cc.ordre asc ")
				.setLong(0, consultaId)
				.setParameter(1, tipus)
				.list();
	}

}
