/**
 * 
 */
package es.caib.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import es.caib.helium.jbpm3.integracio.JbpmTask;
import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

import net.conselldemallorca.helium.core.api.ResultatConsultaPaginada;

/**
 * Command per a trobar els logs associats a una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FindJbpmTasksFiltreCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;

	private Long entornId;
	private String actorId;
	private String taskName;
	private String titol;
	private Long expedientId;
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

	public FindJbpmTasksFiltreCommand(
			Long entornId,
			String actorId,
			String taskName,
			String titol,
			Long expedientId,
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
		this.expedientId = expedientId;
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
		String desactivarOptimitzacioLlistatTascaString = (String) Jbpm3HeliumBridge.getInstanceService().getHeliumProperty("app.llistat.tasques.optimitzacio.desactivada");
		boolean desactivarOptimitzarLlistatTasques = "true".equalsIgnoreCase(desactivarOptimitzacioLlistatTascaString);
		StringBuilder taskQuerySb = consulta1(desactivarOptimitzarLlistatTasques);
		Query queryCount = jbpmContext.getSession().createQuery(
				"select count(distinct ti.id) " + taskQuerySb.toString());
		setQueryParams(
				queryCount,
				entornId,
				actorId,
				taskName,
				titol,
				expedientId,
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
		int count = ((Long)queryCount.uniqueResult()).intValue();
		StringBuilder taskQuerySb2 = null;
		if (actorId != null && mostrarAssignadesUsuari && mostrarAssignadesGrup && !desactivarOptimitzarLlistatTasques) {
			taskQuerySb2 = consulta2(desactivarOptimitzarLlistatTasques);
			Query queryCount2 = jbpmContext.getSession().createQuery(
					"select count(distinct ti.id) " + taskQuerySb2.toString());
			setQueryParams(
					queryCount2,
					entornId,
					actorId,
					taskName,
					titol,
					expedientId,
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
			count += ((Long)queryCount2.uniqueResult()).intValue();
		}
		if (!nomesCount) {
			taskQuerySb.insert(0, "select tki from org.jbpm.taskmgmt.exe.TaskInstance tki where tki.id in (select distinct ti.id ");
			taskQuerySb.append(") ");
			//afegim les ids del segon subconjunt si aquest subconjunt existeix
			if (taskQuerySb2 != null && !taskQuerySb2.toString().isEmpty())
				taskQuerySb.append(" or tki.id in (select distinct ti.id " + taskQuerySb2.toString() + ")");
			List<String> sortColumns = new ArrayList<String>();
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
					sortColumns.add("tki.description");
				} else if ("expedientTitol".equals(sort)) {
					sortColumns.add("tki.processInstance.expedient.numero");
					sortColumns.add("tki.processInstance.expedient.titol");
				} else if ("expedientTipusNom".equals(sort)) {
					sortColumns.add("tki.processInstance.expedient.tipus.nom");
				} else if ("dataCreacio".equals(sort)) {
					sortColumns.add("tki.create");
				} else if ("prioritat".equals(sort)) {
					sortColumns.add("tki.priority");
				} else if ("dataLimit".equals(sort)) {
					sortColumns.add("tki.dueDate");
				}
				boolean sortFirst = true;
				for (String sortColumn: sortColumns) {
					if (!sortFirst)
						taskQuerySb.append(", ");
					taskQuerySb.append(sortColumn);
					taskQuerySb.append((asc) ? " asc" : " desc");
					sortFirst = false;
				}
			}
			Query queryTaskInstances = jbpmContext.getSession().createQuery(taskQuerySb.toString());
			setQueryParams(
					queryTaskInstances,
					entornId,
					actorId,
					taskName,
					titol,
					expedientId,
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
			List<TaskInstance> resultat = (List<TaskInstance>)queryTaskInstances.list();
			List<JbpmTask> tasks = new ArrayList<JbpmTask>();
			for (TaskInstance task: resultat) {
				tasks.add(new JbpmTask(task));
			}
			return new ResultatConsultaPaginada<JbpmTask>(
					count,
					tasks);
		} else {
			return new ResultatConsultaPaginada<JbpmTask>(count);
		}
	}

	private StringBuilder consulta1 (boolean desactivarOptimitzarLlistatTasques) {
		StringBuilder taskQuerySb = new StringBuilder();
		if (actorId != null) {
			taskQuerySb.append(
					"from " +
					"    org.jbpm.taskmgmt.exe." + triaTaula(desactivarOptimitzarLlistatTasques) + " ti left join ti.pooledActors pa " +
					"where ");
			if (mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				if (desactivarOptimitzarLlistatTasques)
					taskQuerySb.append("((ti.actorId is not null and ti.actorId = :actorId) or (ti.actorId is null and pa.actorId = :actorId)) ");
				else
					taskQuerySb.append("((ti.actorId is not null and ti.actorId = :actorId)) ");
			} else if (mostrarAssignadesUsuari && !mostrarAssignadesGrup) {
				taskQuerySb.append("ti.actorId is not null and ti.actorId = :actorId ");
			} else if (!mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				taskQuerySb.append("ti.actorId is null and pa.actorId = :actorId ");
			} else {
				taskQuerySb.append("ti.id is not null ");
			}
		} else {
			taskQuerySb.append(
					"from " +
					"    org.jbpm.taskmgmt.exe." + triaTaula(desactivarOptimitzarLlistatTasques) + " ti " +
					"where ");
			if (mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				taskQuerySb.append("((ti.actorId is not null) or (ti.actorId is null and exists elements(ti.pooledActors))) ");
			} else if (mostrarAssignadesUsuari && !mostrarAssignadesGrup) {
				taskQuerySb.append("ti.actorId is not null ");
			} else if (!mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				taskQuerySb.append("ti.actorId is null and exists elements(ti.pooledActors) ");
			} else {
				taskQuerySb.append("ti.id is not null ");
			}
		}
		if (nomesPendents) {
			taskQuerySb.append("and ti.isSuspended = false and ti.isOpen = true ");
		}
		if (taskName != null && !taskName.isEmpty()) {
			taskQuerySb.append("and ti.task.name = :taskName ");
		}
		if (titol != null && !titol.isEmpty()) {
			//taskQuerySb.append("and upper(ti.description) like '%@#@TITOL@#@%'||:titol||'%@#@ENTORNID@#@%' ");
			taskQuerySb.append("and upper(ti.description) like '%'||:titol||'%' ");
		}
		if (expedientId != null) {
			taskQuerySb.append("and ti.processInstance.expedient.id = :expedientId ");
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
			taskQuerySb.append("and ti.processInstance.expedient.entorn.id = :entornId ");
		}
		if (expedientTipusId != null) {
			taskQuerySb.append("and ti.processInstance.expedient.tipus.id = :expedientTipusId ");
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
		
		return taskQuerySb;
	}
	
	private StringBuilder consulta2 (boolean desactivarOptimitzarLlistatTasques) {
		StringBuilder taskQuerySb = new StringBuilder();
		if (actorId != null) {
			taskQuerySb.append(
					"from " +
					"    org.jbpm.taskmgmt.exe." + triaTaula(desactivarOptimitzarLlistatTasques) + " ti left join ti.pooledActors pa " +
					"where ");
			if (mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				taskQuerySb.append("((ti.actorId is null and pa.actorId = :actorId)) ");
			} else if (mostrarAssignadesUsuari && !mostrarAssignadesGrup) {
				taskQuerySb.append("ti.actorId is not null and ti.actorId = :actorId ");
			} else if (!mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				taskQuerySb.append("ti.actorId is null and pa.actorId = :actorId ");
			} else {
				taskQuerySb.append("ti.id is not null ");
			}
		} else {
			taskQuerySb.append(
					"from " +
					"    org.jbpm.taskmgmt.exe." + triaTaula(desactivarOptimitzarLlistatTasques) + " ti " +
					"where ");
			if (mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				taskQuerySb.append("((ti.actorId is not null) or (ti.actorId is null and exists elements(ti.pooledActors))) ");
			} else if (mostrarAssignadesUsuari && !mostrarAssignadesGrup) {
				taskQuerySb.append("ti.actorId is not null ");
			} else if (!mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				taskQuerySb.append("ti.actorId is null and exists elements(ti.pooledActors) ");
			} else {
				taskQuerySb.append("ti.id is not null ");
			}
		}
		if (nomesPendents) {
			taskQuerySb.append("and ti.isSuspended = false and ti.isOpen = true ");
		}
		if (taskName != null && !taskName.isEmpty()) {
			taskQuerySb.append("and ti.task.name = :taskName ");
		}
		if (titol != null && !titol.isEmpty()) {
			//taskQuerySb.append("and upper(ti.description) like '%@#@TITOL@#@%'||:titol||'%@#@ENTORNID@#@%' ");
			taskQuerySb.append("and upper(ti.description) like '%'||:titol||'%' ");
		}
		if (expedientId != null) {
			taskQuerySb.append("and ti.processInstance.expedient.id = :expedientId ");
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
			taskQuerySb.append("and ti.processInstance.expedient.entorn.id = :entornId ");
		}
		if (expedientTipusId != null) {
			taskQuerySb.append("and ti.processInstance.expedient.tipus.id = :expedientTipusId ");
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
		
		return taskQuerySb;
	}

	private String triaTaula (boolean desactivarOptimitzarLlistatTasques)  {
		if (nomesPendents && !desactivarOptimitzarLlistatTasques)
			return "MvTaskInstance";
		else
			return "TaskInstance";
	}

	private void setQueryParams(
			Query query,
			Long entornId,
			String actorId,
			String taskName,
			String titol,
			Long expedientId,
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
		if (actorId != null && (mostrarAssignadesUsuari || mostrarAssignadesGrup)) {
			query.setParameter("actorId", actorId);
		}
		if (taskName != null && !taskName.isEmpty()) {
			query.setParameter("taskName", taskName);
		}
		if (titol != null && !titol.isEmpty()) {
			query.setParameter("titol", titol.toUpperCase());
		}
		if (expedientId != null) {
			query.setParameter("expedientId", expedientId);
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
