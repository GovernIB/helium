/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.conselldemallorca.helium.core.extern.formulari.LlistatIds;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.command.Command;

/**
 * Command per obtenir la llista de tasques personals
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetProcessInstancesForActiveTasksCommand extends AbstractGetObjectBaseCommand implements Command {

	private static final long serialVersionUID = -1908847549444051495L;
	private String actorId;
	private List<Long> idsPIExpedients;
	private String tasca; 
	private Date dataCreacioInici; 
	private Date dataCreacioFi;
	private Integer prioritat;
	private Date dataLimitInici;
	private Date dataLimitFi;
	private boolean pooled;
	private int firstRow;
	private int maxResults;
	private String sort;
	private boolean asc;

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}	

	public GetProcessInstancesForActiveTasksCommand() {}

	public GetProcessInstancesForActiveTasksCommand(String actorId, List<Long> idsPIExpedients, boolean pooled) {
		super();
		this.actorId = actorId;
		this.idsPIExpedients = idsPIExpedients;
		this.pooled = pooled;
	}

	public GetProcessInstancesForActiveTasksCommand(String actorId, String tasca, List<Long> idsPIExpedients, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, String sort, boolean asc, boolean pooled) {
		super();
		this.actorId = actorId;
		this.idsPIExpedients = idsPIExpedients;
		this.tasca = tasca; 
		this.dataCreacioInici = dataCreacioInici; 
		this.dataCreacioFi = dataCreacioFi;
		this.prioritat = prioritat;
		this.dataLimitInici = dataLimitInici;
		this.dataLimitFi = dataLimitFi;
		this.pooled = pooled;
		this.sort = sort;
		this.asc = asc;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		
		setJbpmContext(jbpmContext);
		  
		String hqlPersonal =
		    "select  " + 
		    "    ti.processInstance.id, " +
		    "    ti.processInstance.superProcessToken.id, " +
		    "  ti.id, " +
		    "  (select (ta.nom) from Tasca as ta where ta.jbpmName = ti.name and ti.processInstance.processDefinition.id = cast(ta.definicioProces.jbpmId as long)) " +
		    "  from " +
		    "    org.jbpm.taskmgmt.exe.TaskInstance as ti " +
		    "  where " +
		    "  ti.actorId = :actorId " + 
		    "  and ti.isSuspended = false " +
		    "  and ti.isOpen = true";
		  
		String hqlPooled =  
		    "select  " + 
		    "    ti.processInstance.id, " +
		    "    ti.processInstance.superProcessToken.id, " +
		    "  ti.id, " +
		    "  (select (ta.nom) from Tasca as ta where ta.jbpmName = ti.name and ti.processInstance.processDefinition.id = cast(ta.definicioProces.jbpmId as long)) " +
		    "  from " +
		    "    org.jbpm.taskmgmt.exe.TaskInstance as ti " +
		    "  join ti.pooledActors pooledActor " +
		    "  where " +
		    "  pooledActor.actorId = :actorId " +
		    "  and ti.actorId is null " + 
		    "  and ti.isSuspended = false " +
		    "  and ti.isOpen = true";
		  
		String hql = pooled ? hqlPooled : hqlPersonal;
		
		if (dataCreacioInici != null) {
			hql += " and ti.create >= :dataCreacioInici";
		}

		if (dataCreacioFi != null) {
			hql += " and ti.create <= :dataCreacioFi";
		}

		if (dataLimitInici != null) {
			hql += " and ti.dueDate >= :dataLimitInici";
		}

		if (dataLimitFi != null) {
			hql += " and ti.dueDate <= :dataLimitFi";
		}

		if (prioritat != null) {
			hql += " and ti.priority = :prioritat";
		}

		if (tasca != null && !"".equals(tasca)) {
			hql += " and ti.processInstance.processDefinition.id = (select (cast(ta.definicioProces.jbpmId as long)) from Tasca as ta where UPPER(ta.nom) like UPPER(:tasca) and ta.jbpmName = ti.name and ti.processInstance.processDefinition.id = cast(ta.definicioProces.jbpmId as long)) ";
		}
		
		hql += " order by ";
		if ("dataCreacio".equals(sort)) {
			hql += " ti.create " + (asc ? "asc" : "desc");
		} else if ("prioritat".equals(sort)) {
			hql += " ti.priority " + (asc ? "asc" : "desc");
		} else if ("dataLimit".equals(sort)) {
			hql += " ti.dueDate " + (asc ? "asc" : "desc");
		} else if ("titol".equals(sort)) {
			hql += " 4 " + (asc ? "asc" : "desc");
		} else {
			hql += " 1 ";
		}
		
		Query query = jbpmContext.getSession().createQuery(hql);
		query.setString("actorId", actorId);
		
		if (dataCreacioInici != null) {
			query.setDate("dataCreacioInici", dataCreacioInici);
		}
		
		if (dataCreacioFi != null) {
			query.setDate("dataCreacioFi", dataCreacioFi);
		}
		
		if (dataLimitInici != null) {
			query.setDate("dataLimitInici", dataLimitInici);
		}
		
		if (dataLimitFi != null) {
			query.setDate("dataLimitFi", dataLimitFi);
		}
		
		if (prioritat != null) {
			query.setInteger("prioritat",3-prioritat);
		}
		
		if (tasca != null && !"".equals(tasca)) {
			query.setString("tasca","%"+tasca+"%");
		}		
		
		List<Object[]> llistaActorId = query.list();
		
		Set<Long> superProcessTokenIds = new HashSet<Long>();
		do {
			superProcessTokenIds.clear();
			for (Object[] reg: llistaActorId) {
				if (reg[1] != null)
					superProcessTokenIds.add((Long)reg[1]);
			}
			if (superProcessTokenIds.size() > 0) {
				Query queryProcessInstancesPare = jbpmContext.getSession().createQuery(
						"select " +
						"    t.processInstance.id, " +
						"    t.processInstance.superProcessToken.id, " +
						"    t.id " +
						"from " +
						"    org.jbpm.graph.exe.Token as t " +
						"where " +
						"    t.id in (:superProcessTokenIds)");
				queryProcessInstancesPare.setParameterList(
						"superProcessTokenIds",
						superProcessTokenIds);

				List<Object[]> llistaProcessInstancesPare = queryProcessInstancesPare.list();
				for (Object[] regAct: llistaActorId) {
					if (regAct[1] != null) {
						for (Object[] regSup: llistaProcessInstancesPare) {
							if (regSup[2].equals(regAct[1])) {
								regAct[0] = regSup[0];	
								regAct[1] = regSup[1];	
								break;
							}
						}
					}
				}
			}
		} while (superProcessTokenIds.size() > 0);
		
		List<Long> listadoTask = new ArrayList<Long>();
		
		// Ordenamos la lista en el caso de que sea por expedientes
	    if ("expedientTitol".equals(sort) || "expedientTipusNom".equals(sort)) {
	    	for (Long id : idsPIExpedients) {
		    	for (Object[] fila : llistaActorId) {
		    		if (id.equals(fila[0]) && !listadoTask.contains(fila[2])) {
		    			listadoTask.add((Long) fila[2]);
		    		}
		    	}	
	    	}			
		} else {
	    	for (Object[] fila : llistaActorId) {
	    		if (idsPIExpedients.contains(fila[0]) && !listadoTask.contains(fila[2])) {
	    			listadoTask.add((Long) fila[2]);
	    		}
	    	}			
		}
		
	    LlistatIds listado = new LlistatIds();
	    
	    if (getFirstRow() > listadoTask.size()) {
	    	setFirstRow(0);
	    }
	    
		maxResults = (maxResults > listadoTask.size()) ? listadoTask.size() : maxResults;
		int limit = (maxResults > 0)? getFirstRow()+maxResults : listadoTask.size();
		limit = (limit > listadoTask.size()) ? listadoTask.size() : limit;
	    listado.setCount(listadoTask.size());
	    listado.setIds(listadoTask.subList(getFirstRow(), limit));

	    return listado;
	}


	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getParametersToString() {
		return "GetProcessInstancesForActiveTasksCommand [actorId=" + actorId + ", idsPIExpedients=" + idsPIExpedients + ", tasca=" + tasca + ", dataCreacioInici=" + dataCreacioInici + ", dataCreacioFi=" + dataCreacioFi + ", prioritat=" + prioritat + ", dataLimitInici=" + dataLimitInici + ", dataLimitFi=" + dataLimitFi + ", pooled=" + pooled + ", firstRow=" + firstRow + ", maxResults=" + maxResults + ", sort=" + sort + ", asc=" + asc + "]";
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "actorId=" + actorId;
	}

	public GetProcessInstancesForActiveTasksCommand actorId(String actorId) {
		setActorId(actorId);
	    return this;
	}
}
