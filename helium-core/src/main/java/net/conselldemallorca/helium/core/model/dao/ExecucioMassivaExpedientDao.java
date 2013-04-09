/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.sun.star.util.DateTime;

/**
 * Dao pels objectes del tipus ExecucioMassivaExpedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class ExecucioMassivaExpedientDao extends HibernateGenericDao<ExecucioMassivaExpedient, Long> {

	public ExecucioMassivaExpedientDao() {
		super(ExecucioMassivaExpedient.class);
	}

	@SuppressWarnings("unchecked")
	public ExecucioMassivaExpedient getExecucioMassivaActiva(Long lastMassiu) {
		Query query = null;
		Date ara = new Date();
		query = getSession().createQuery(
				"select e " +
				"from	ExecucioMassivaExpedient e " +
				"where	e.execucioMassiva.id =	" +
				"			(select min(id) " +
				"			 from 	ExecucioMassiva " +
				"			 where 	dataInici <= ? " +
				"					and dataFi is null) " +
				"	and	e.dataFi is null " +
				" order by e.ordre")
				.setTimestamp(0, ara);
		
		if (lastMassiu != null) {
			Long nextMassiu = (Long)getSession().createQuery(
								"select min(id) " +
								"from 	ExecucioMassiva " +
								"where 	dataInici <= ? " +
								"	and dataFi is null " +
								"	and id > ? )")
								.setTimestamp(0, ara)
								.setLong(1, lastMassiu)
								.uniqueResult();
			if (nextMassiu != null) {
				query = getSession().createQuery(
						"select e " +
						"from	ExecucioMassivaExpedient e " +
						"where	e.execucioMassiva.id = ? " +
						"   and	e.dataFi is null " +
						" order by e.ordre")
						.setLong(0, nextMassiu);
			}
		}
		List<ExecucioMassivaExpedient> llista = (List<ExecucioMassivaExpedient>)query.list();
		if (llista != null && llista.size() > 0) {
			return llista.get(0);
		}
		return null;
	}
}
