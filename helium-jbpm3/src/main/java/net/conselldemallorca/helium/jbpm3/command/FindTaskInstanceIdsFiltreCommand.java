/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;

import net.conselldemallorca.helium.jbpm3.integracio.LlistatIds;

/**
 * Command per a trobar els logs associats a una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FindTaskInstanceIdsFiltreCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;

	private Long entornId;
	private String actorId;
	private String taskName;
	private String titol;
	private String expedientTitol;
	private String expedientNumero;
	private Long expedientTipusId;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	private Integer prioritat;
	private Date dataLimitInici;
	private Date dataLimitFi;
	private boolean mostrarAssignadesUsuari;
	private boolean mostrarAssignadesGrup;
	private boolean nomesPendents;
	private int firstResult;
	private int maxResults;
	private String sort;
	private boolean asc;
	private boolean nomesCount;

	public FindTaskInstanceIdsFiltreCommand(
			Long entornId,
			String actorId,
			String taskName,
			String titol,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			boolean mostrarAssignadesUsuari,
			boolean mostrarAssignadesGrup,
			boolean nomesPendents,
			int firstResult,
			int maxResults,
			String sort,
			boolean asc,
			boolean nomesCount) {
		super();
		this.entornId = entornId;
		this.actorId = actorId;
		this.taskName = taskName;
		this.titol = titol;
		this.expedientTitol = expedientTitol;
		this.expedientNumero = expedientNumero;
		this.expedientTipusId = expedientTipusId;
		this.dataCreacioInici = dataCreacioInici;
		this.dataCreacioFi = dataCreacioFi;
		this.prioritat = prioritat;
		this.dataLimitInici = dataLimitInici;
		this.dataLimitFi = dataLimitFi;
		this.mostrarAssignadesUsuari = mostrarAssignadesUsuari;
		this.mostrarAssignadesGrup = mostrarAssignadesGrup;
		this.nomesPendents = nomesPendents;
		this.firstResult = firstResult;
		this.maxResults = maxResults;
		this.sort = sort;
		this.asc = asc;
		this.nomesCount = nomesCount;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		StringBuilder taskQuerySb = new StringBuilder();
		taskQuerySb.append(
				"from " +
				"    org.jbpm.taskmgmt.exe.TaskInstance ti left join ti.pooledActors pa " +
				"where ");
		if (mostrarAssignadesUsuari && mostrarAssignadesGrup) {
			taskQuerySb.append("((ti.actorId is not null and ti.actorId = :actorId) or (ti.actorId is null and pa.actorId = :actorId)) ");
		} else if (mostrarAssignadesUsuari && !mostrarAssignadesGrup) {
			taskQuerySb.append("ti.actorId is not null and ti.actorId = :actorId ");
		} else if (!mostrarAssignadesUsuari && mostrarAssignadesGrup) {
			taskQuerySb.append("ti.actorId is null and pa.actorId = :actorId ");
		}
		if (nomesPendents) {
			taskQuerySb.append("and ti.isSuspended = false and ti.isOpen = true ");
		}
		if (taskName != null && !taskName.isEmpty()) {
			taskQuerySb.append("and ti.task.name = :taskName ");
		}
		if (titol != null && !titol.isEmpty()) {
			taskQuerySb.append("and upper(ti.description) like '%@#@TITOL@#@%'||:titol||'%@#@ENTORNID@#@%' ");
		}
		if (expedientTitol != null && !expedientTitol.isEmpty()) {
			taskQuerySb.append(
					"and (upper(case " +
					"     when (ti.processInstance.expedient.numero is not null and ti.processInstance.expedient.titol is not null) then ('['||ti.processInstance.expedient.numero||'] ' || ti.processInstance.expedient.titol) " +
					"     when (ti.processInstance.expedient.numero is not null and ti.processInstance.expedient.titol is null) then ti.processInstance.expedient.numero " +
					"     when (ti.processInstance.expedient.numero is null and ti.processInstance.expedient.titol is not null) then ti.processInstance.expedient.titol " +
					"     else ti.processInstance.expedient.numeroDefault end) like upper(:expedientTitol))");
		}
		if (expedientNumero != null && !expedientNumero.isEmpty()) {
			taskQuerySb.append(
					"and (upper(case " +
					"    when (ti.processInstance.expedient.numero is not null AND ti.processInstance.expedient.titol is not null) then ('['||ti.processInstance.expedient.numero||']') " +
					"    when (ti.processInstance.expedient.numero is not null AND ti.processInstance.expedient.titol is null) then ti.processInstance.expedient.numero " +
					"    else ti.processInstance.expedient.numeroDefault END) like upper(:expedientNumero))");
		}
		if (entornId != null) {
			taskQuerySb.append("and ti.processInstance.expedient.entornId = :entornId ");
		}
		if (expedientTipusId != null) {
			taskQuerySb.append("and ti.processInstance.expedient.expedientTipusId = :expedientTipusId ");
		}
		if (dataCreacioInici != null) {
			taskQuerySb.append("and ti.create >= :dataCreacioInici ");
		}
		if (dataCreacioFi != null) {
			taskQuerySb.append("and ti.create <= :dataCreacioFi ");
		}
		if (dataLimitInici != null) {
			taskQuerySb.append("and ti.dueDate >= :dataLimitInici ");
		}
		if (dataLimitFi != null) {
			taskQuerySb.append("and ti.dueDate <= :dataLimitFi ");
		}
		if (prioritat != null) {
			taskQuerySb.append("and ti.priority = :prioritat ");
		}
		LlistatIds resposta = new LlistatIds();
		Query queryCount = jbpmContext.getSession().createQuery(
				"select count(distinct ti.id) " + taskQuerySb.toString());
		setQueryParams(
				queryCount,
				entornId,
				actorId,
				taskName,
				titol,
				expedientTitol,
				expedientNumero,
				expedientTipusId,
				dataCreacioInici,
				dataCreacioFi,
				prioritat,
				dataLimitInici,
				dataLimitFi,
				0,
				-1);
		resposta.setCount(((Long)queryCount.uniqueResult()).intValue());
		if (!nomesCount) {
			String sortColumn = null;
			if (sort != null) {
				/*sorts:
				titol
				expedientTitol
				expedientTipusNom
				dataCreacio
				prioritat
				dataLimit*/
				// Per defecte: dataCreacio desc
				taskQuerySb.append("order by ");
				if ("titol".equals(sort)) {
					sortColumn = "ti.description";
				} else if ("expedientTitol".equals(sort)) {
					sortColumn = "ti.processInstance.expedient.titol";
				} else if ("expedientTipusNom".equals(sort)) {
					sortColumn = "ti.processInstance.expedient.expedientTipusId";
				} else if ("dataCreacio".equals(sort)) {
					sortColumn = "ti.create";
				} else if ("prioritat".equals(sort)) {
					sortColumn = "ti.priority";
				} else if ("dataLimit".equals(sort)) {
					sortColumn = "ti.dueDate";
				} else {
					sortColumn = sort;
				}
				taskQuerySb.append(sortColumn);
				taskQuerySb.append((asc) ? " asc" : " desc");
			}
			if (sortColumn != null) {
				taskQuerySb.insert(0, "select distinct ti.id, " + sortColumn + " ");
			} else {
				taskQuerySb.insert(0, "select distinct ti.id ");
			}
			Query queryIds = jbpmContext.getSession().createQuery(taskQuerySb.toString());
			setQueryParams(
					queryIds,
					entornId,
					actorId,
					taskName,
					titol,
					expedientTitol,
					expedientNumero,
					expedientTipusId,
					dataCreacioInici,
					dataCreacioFi,
					prioritat,
					dataLimitInici,
					dataLimitFi,
					firstResult,
					maxResults);
			List<Object[]> resultat = (List<Object[]>)queryIds.list();
			if (sortColumn != null) {
				List<Long> ids = new ArrayList<Long>();
				for (Object[] fila: resultat) {
					ids.add((Long)fila[0]);
				}
				resposta.setIds(ids);
			} else {
				resposta.setIds((List<Long>)queryIds.list());
			}
			
		}
		return resposta;
	}

	private void setQueryParams(
			Query query,
			Long entornId,
			String actorId,
			String taskName,
			String titol,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			int firstResult,
			int maxResults) {
		if (mostrarAssignadesUsuari || mostrarAssignadesGrup) {
			query.setParameter("actorId", actorId);
		}
		if (taskName != null && !taskName.isEmpty()) {
			query.setParameter("taskName", taskName);
		}
		if (titol != null && !titol.isEmpty()) {
			query.setParameter("titol", titol.toUpperCase());
		}
		if (expedientTitol != null && !expedientTitol.isEmpty()) {
			query.setParameter("expedientTitol", "%" + expedientTitol + "%");
		}
		if (expedientNumero != null && !expedientNumero.isEmpty()) {
			query.setParameter("expedientNumero", "%" + expedientNumero + "%");
		}
		if (entornId != null) {
			query.setParameter("entornId", entornId);
		}
		if (expedientTipusId != null) {
			query.setParameter("expedientTipusId", expedientTipusId);
		}
		if (dataCreacioInici != null) {
			query.setParameter("dataCreacioInici", dataCreacioInici);
		}
		if (dataCreacioFi != null) {
			query.setParameter("dataCreacioFi", dataCreacioFi);
		}
		if (dataLimitInici != null) {
			query.setParameter("dataLimitInici", dataLimitInici);
		}
		if (dataLimitFi != null) {
			query.setParameter("dataLimitFi", dataLimitFi);
		}
		if (prioritat != null) {
			query.setParameter("prioritat", prioritat);
		}
		if (maxResults != -1) {
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
		}
	}

}
