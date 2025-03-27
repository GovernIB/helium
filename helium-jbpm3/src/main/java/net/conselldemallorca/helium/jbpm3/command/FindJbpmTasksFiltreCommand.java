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
import org.jbpm.taskmgmt.exe.TaskInstance;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.ResultatConsultaPaginadaJbpm;

/**
 * Command per a trobar els logs associats a una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FindJbpmTasksFiltreCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	//Map<Long, List<Long>> unitatsPerTipusComu;
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
	private boolean isAdministrador;

	public FindJbpmTasksFiltreCommand(
			//Map<Long, List<Long>> unitatsPerTipusComu,
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
			boolean nomesCount,
			boolean isAdministrador) {
		super();
		//this.unitatsPerTipusComu = unitatsPerTipusComu;
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
		this.isAdministrador = isAdministrador;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		String desactivarOptimitzacioLlistatTascaString = (String)Jbpm3HeliumBridge.getInstanceService().getHeliumProperty("app.llistat.tasques.optimitzacio.desactivada");
		boolean desactivarOptimitzarLlistatTasques = "true".equalsIgnoreCase(desactivarOptimitzacioLlistatTascaString);
		StringBuilder taskQuerySb = consulta1(desactivarOptimitzarLlistatTasques);
		Query queryCount = jbpmContext.getSession().createQuery(
				"select count(distinct ti.id) " + taskQuerySb.toString());
		setQueryParams(
				queryCount,
				//unitatsPerTipusComu,
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
					//unitatsPerTipusComu,
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
					//unitatsPerTipusComu,
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
			return new ResultatConsultaPaginadaJbpm<JbpmTask>(
					count,
					tasks);
		} else {
			return new ResultatConsultaPaginadaJbpm<JbpmTask>(count);
		}
	}

	private StringBuilder consulta1 (boolean desactivarOptimitzarLlistatTasques) {
		StringBuilder taskQuerySb = new StringBuilder();
		if (actorId != null) {
			taskQuerySb.append(
					"from " +
					"    org.jbpm.taskmgmt.exe." + triaTaula(desactivarOptimitzarLlistatTasques) + " ti left join ti.pooledActors pa ");

//			if(unitatsPerTipusComu!=null && !unitatsPerTipusComu.isEmpty()) {
//				taskQuerySb.append(" join ti.processInstance pi join pi.expedient e join e.tipus et ");
//				
//			}
			
			//Posam el entornId que no será mai null el primer, així evitam errors de QuerySyntaxException al tenir un "where and" per haver botat un filtre
			taskQuerySb.append("where ti.processInstance.expedient.entorn.id = :entornId ");
			

//			if( unitatsPerTipusComu !=null && !unitatsPerTipusComu.isEmpty()) {				
//				taskQuerySb.append("( et.procedimentComu <> 1 ");
//				List<Long> codisUos;
//				for (Long expedientTipusComuId : unitatsPerTipusComu.keySet()) {
//					codisUos = unitatsPerTipusComu.get(expedientTipusComuId);
//					if (!codisUos.isEmpty()) {
//						taskQuerySb.append(" or ( et.procedimentComu = 1 and et.id = " + expedientTipusComuId);
//						// en subllistes
//						taskQuerySb.append(" and ( ");
//						for (int i = 0; i <= codisUos.size() / 1000; i++ ) {
//							if( i > 0) { 
//								taskQuerySb.append(" or ");
//							}
//							taskQuerySb.append(" e.unitatOrganitzativaId in (:uos_" + expedientTipusComuId + "_" + i + ") ");
//						}
//						taskQuerySb.append(" ) ) ");
//					}
//				}
//				taskQuerySb.append(") and ");
//			}
			
			if (mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				if (desactivarOptimitzarLlistatTasques)
					taskQuerySb.append("and ((ti.actorId is not null and ti.actorId = :actorId) or (ti.actorId is null and pa.actorId = :actorId)) ");
				else
//					taskQuerySb.append("((ti.actorId is null or ti.actorId = :actorId)) ");
					taskQuerySb.append("and ((ti.actorId is not null and ti.actorId = :actorId)) ");
			} else if (mostrarAssignadesUsuari && !mostrarAssignadesGrup) {
				taskQuerySb.append("and ti.actorId is not null and ti.actorId = :actorId ");
			} else if (!mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				taskQuerySb.append("and ti.actorId is null and pa.actorId = :actorId ");
			} else {
				taskQuerySb.append("and ti.id is not null ");
			}
		} else {
			//El parametre "actor" es null, per tant no tenen marcat el botó de "Nomes amb tasques meves"
			//per tant en aquest punt, si ets admin, veurás també les que no tenen ningú assignat.
			taskQuerySb.append(
					"from " +
					"    org.jbpm.taskmgmt.exe." + triaTaula(desactivarOptimitzarLlistatTasques) + " ti " +
					"where ");
			
			//Posam el entornId que no será mai null el primer, així evitam errors de QuerySyntaxException al tenir un "where and" per haver botat un filtre
			taskQuerySb.append("ti.processInstance.expedient.entorn.id = :entornId ");
			
			if (mostrarAssignadesUsuari && mostrarAssignadesGrup) {
					//Mostram totes, tenguin o no usuari (TODO: Aqui hauria de deixar el exists elements(ti.pooledActors) ?? )
//					taskQuerySb.append("((ti.actorId is not null) or (ti.actorId is null and exists elements(ti.pooledActors))) ");
					//Volem mostrar les que tenen algun usuari individual, o algun usuari en un grup. Pero no es mostren les que no tenen cap usuari.
				if(!isAdministrador)
					taskQuerySb.append("and ((ti.actorId is not null) or (ti.actorId is null and exists elements(ti.pooledActors))) ");
			} else if (mostrarAssignadesUsuari && !mostrarAssignadesGrup) {
				taskQuerySb.append("and ti.actorId is not null ");
			} else if (!mostrarAssignadesUsuari && mostrarAssignadesGrup) {
				taskQuerySb.append("and ti.actorId is null and exists elements(ti.pooledActors) ");
			} else {
				taskQuerySb.append("and ti.id is not null ");
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
					"    org.jbpm.taskmgmt.exe." + triaTaula(desactivarOptimitzarLlistatTasques) + " ti left join ti.pooledActors pa ");

//			if(unitatsPerTipusComu!=null && !unitatsPerTipusComu.isEmpty()) {
//				taskQuerySb.append(" join ti.processInstance pi join pi.expedient e join e.tipus et ");			
//			}
			
			taskQuerySb.append("where ");
			
//			if(unitatsPerTipusComu!=null && !unitatsPerTipusComu.isEmpty()) {
//				taskQuerySb.append("( ");
//				List<Long> codisUos;
//				boolean filtrePerTipus = false;
//				for (Long expedientTipusComuId : unitatsPerTipusComu.keySet()) {
//					codisUos = unitatsPerTipusComu.get(expedientTipusComuId);
//					if (!codisUos.isEmpty()) {
//						if (filtrePerTipus) {
//							taskQuerySb.append(" or ");
//						}
//						taskQuerySb.append(" ( et.procedimentComu = 1 and et.id = " + expedientTipusComuId);
//						// en subllistes
//						taskQuerySb.append(" and ( ");
//						for (int i = 0; i <= codisUos.size() / 1000; i++ ) {
//							if( i > 0) { 
//								taskQuerySb.append(" or ");
//							}
//							taskQuerySb.append(" e.unitatOrganitzativaId in (:uos_" + expedientTipusComuId + "_" + i + ") ");
//						}
//						taskQuerySb.append(" ) ) ");
//						filtrePerTipus = true;
//					}
//				}
//				taskQuerySb.append(") and ");
//			}
			
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
			//Map<Long, List<Long>> unitatsPerTipusComu,
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
//		if(unitatsPerTipusComu !=null && !unitatsPerTipusComu.isEmpty()) {
//			// en subllistes
//			List<Long> idsUos;
//			for (Long expedientTipusComuId : unitatsPerTipusComu.keySet()) {
//				idsUos = unitatsPerTipusComu.get(expedientTipusComuId);
//				if (!idsUos.isEmpty()) {
//					// en subllistes
//					for (int i = 0; i <= idsUos.size() / 1000; i++ ) {
//						query.setParameterList(
//								"uos_" + expedientTipusComuId + "_" + i , 
//								idsUos.subList(i*1000, Math.min(idsUos.size(), (i + 1)*1000)));
//					}
//				}
//			}
//		}
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