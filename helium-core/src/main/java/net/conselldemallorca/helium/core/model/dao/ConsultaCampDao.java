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
	}

	public ConsultaCamp getAmbTipusIId(
			Long consultaId,
			Long consultaCampId,
			TipusConsultaCamp tipus) {
		return (ConsultaCamp)getSession().createQuery(
				"from " +
				"    ConsultaCamp cc " +
				"where " +
				"    cc.consulta.id=? " +
				"and cc.id=? " +
				"and cc.tipus=?").
				setLong(0, consultaId).
				setLong(1, consultaCampId).
				setParameter(2, tipus).
				uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Camp> findCampsDefinicioProcesAmbJbpmKey(Long entornId, String defprocJbpmKey) {
		return getSession().createQuery(
				"from" +
				"    Camp camp " +
				"where (camp.codi, camp.tipus, camp.definicioProces.id) in (" +
				"        select c.codi, c.tipus, max(dp.id) " + 
				"        from " +
				"            Camp c " +
				"            left join c.definicioProces dp " +
				"        where " +
				"            dp.entorn.id = ? " +
				"        and dp.jbpmKey = ? " +
				"        group by " +
				"            c.codi, " +
				"            c.tipus) " +
				"order by " +
				"    camp.codi asc ")
				.setLong(0, entornId)
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
