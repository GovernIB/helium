/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

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

	@SuppressWarnings("unchecked")
	public List<ExecucioMassivaExpedient> getExecucioMassivaActivaByUser(String username) {
		Query query = null;
		query = getSession().createQuery(
				"select e " +
				"from	ExecucioMassivaExpedient e " +
				"where 	e.execucioMassiva.usuari =	'" + username +"'" +
				" and	e.execucioMassiva.dataFi is null " +
				" order by e.ordre");

		return (List<ExecucioMassivaExpedient>)query.list();
	}

	@SuppressWarnings("unchecked")
	public List<ExecucioMassivaExpedient> getExecucioMassivaActivaById(Long id) {
		Query query = null;
		query = getSession().createQuery(
				"select e " +
				"from	ExecucioMassivaExpedient e " +
				"where 	e.execucioMassiva.id =	" + id +
				" and	e.execucioMassiva.dataFi is null " +
				" order by e.ordre");

		return (List<ExecucioMassivaExpedient>)query.list();
	}

	@SuppressWarnings("unchecked")
	public List<ExecucioMassivaExpedient> getExecucioMassivaById(Long id) {
		Query query = null;
		query = getSession().createQuery(
				"select e " +
				"from	ExecucioMassivaExpedient e " +
				"where 	e.execucioMassiva.id =	" + id +
				" order by e.ordre");

		return (List<ExecucioMassivaExpedient>)query.list();
	}
	
	@SuppressWarnings("unchecked")
	public Long getProgresExecucioMassivaByUser(String username) {
		Long percentatge = 100L;
		Query query = null;
		query = getSession().createQuery(
				"select count(e) " +
				"from	ExecucioMassivaExpedient e " +
				"where 	e.execucioMassiva.usuari =	'" + username +"'" +
				" and	e.execucioMassiva.dataFi is null");
		Long total = (Long)query.uniqueResult();

		if (total != null && total > 0) {
			query = getSession().createQuery(
					"select count(e) " +
					"from	ExecucioMassivaExpedient e " +
					"where 	e.execucioMassiva.usuari =	'" + username +"'" +
					" and	e.execucioMassiva.dataFi is null");
			
			Long pendent = (Long)query.uniqueResult();
			if (pendent != null)
				percentatge = 100 - (pendent * 100 / total);
		}
		
		return percentatge;
	}
	
	@SuppressWarnings("unchecked")
	public Long getProgresExecucioMassivaById(Long id) {
		Long percentatge = 100L;
		Query query = null;
		query = getSession().createQuery(
				"select count(e) " +
				"from	ExecucioMassivaExpedient e " +
				"where 	e.execucioMassiva.id =	" + id);
		Long total = (Long)query.uniqueResult();

		if (total != null && total > 0) {
			query = getSession().createQuery(
					"select count(e) " +
					"from	ExecucioMassivaExpedient e " +
					"where 	e.execucioMassiva.id =	" + id +
					" and	e.dataFi is null");
			
			Long pendent = (Long)query.uniqueResult();
			if (pendent != null)
				percentatge = 100 - (pendent * 100 / total);
		}
		
		return percentatge;
	}
}
