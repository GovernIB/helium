/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus Consulta
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class ConsultaDao extends HibernateGenericDao<Consulta, Long> {

	public ConsultaDao() {
		super(Consulta.class);
	}

	public List<Consulta> findAmbEntorn(Long entornId) {
		return findByCriteria(Restrictions.eq("entorn.id", entornId));
	}
	public List<Consulta> findAmbEntornIExpedientTipus(Long entornId, Long expedientTipusId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("expedientTipus.id", expedientTipusId));
	}
	public Consulta findAmbEntornICodi(Long entornId, String codi) {
		List<Consulta> consultes = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("codi", codi));
		if (consultes.size() > 0)
			return consultes.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Camp> findCampsFiltre(Long consultaId) {
		return (List<Camp>)getSession().
		createQuery(
				"select c " +
				"from Camp c, " +
				"     ConsultaCamp cc, " +
				"     DefinicioProces dp " +
				"where cc.consulta.id = ? " +
				"and cc.tipus = ? " +
				"and dp.jbpmKey = cc.defprocJbpmKey " +
				"and dp.versio = cc.defprocVersio " +
				"and dp.id = c.definicioProces.id " +
				"and cc.campCodi = c.codi " +
				"order by " +
				"    cc.ordre").
		setLong(0, consultaId).
		setParameter(1, TipusConsultaCamp.FILTRE).
		list();
	}

	@SuppressWarnings("unchecked")
	public List<Camp> findCampsInforme(Long consultaId) {
		return (List<Camp>)getSession().
		createQuery(
				"select c " +
				"from Camp c, " +
				"     ConsultaCamp cc, " +
				"     DefinicioProces dp " +
				"where cc.consulta.id = ? " +
				"and cc.tipus = ? " +
				"and dp.jbpmKey = cc.defprocJbpmKey " +
				"and dp.versio = cc.defprocVersio " +
				"and dp.id = c.definicioProces.id " +
				"and cc.campCodi = c.codi " +
				"order by " +
				"    cc.ordre").
		setLong(0, consultaId).
		setParameter(1, TipusConsultaCamp.INFORME).
		list();
	}

}
