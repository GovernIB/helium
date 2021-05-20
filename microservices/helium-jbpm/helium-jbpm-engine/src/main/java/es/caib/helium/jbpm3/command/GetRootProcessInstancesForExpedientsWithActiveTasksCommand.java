package es.caib.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.command.Command;

/**
 * Command per obtenir la llista de tasques personals
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetRootProcessInstancesForExpedientsWithActiveTasksCommand extends AbstractGetObjectBaseCommand implements Command {

	private static final long serialVersionUID = -1908847549444051495L;
	private String actorId;
	private List<String> ids;
	private boolean pooled;
	private boolean nomesAmbPendents; 
	private boolean nomesMeves;

	public GetRootProcessInstancesForExpedientsWithActiveTasksCommand() {}

	public GetRootProcessInstancesForExpedientsWithActiveTasksCommand(
			String actorId,
			List<String> ids,
			boolean pooled, 
			boolean nomesAmbPendents, 
			boolean nomesMeves) {
		super();
		this.actorId = actorId;
		this.ids = ids;
		this.pooled = pooled;
		this.nomesAmbPendents = nomesAmbPendents;  
		this.nomesMeves = nomesMeves;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		
		setJbpmContext(jbpmContext);
		  
		String hqlPersonal =
		    "select  " + 
		    "    ti.processInstance.id, " +
		    "    ti.processInstance.superProcessToken.id, " +
		    "    ti.id " +
		    "from " +
		    "    org.jbpm.taskmgmt.exe.TaskInstance as ti " +
		    "where 1=1 " +
		    ((nomesMeves) ? " and ti.actorId = :actorId " : " and ti.actorId is not null ") +
		    ((nomesAmbPendents) ? " and ti.isSuspended = false and ti.isOpen = true " : " ");
	
		String hqlPooled =  
		    "select  " + 
		    "    ti.processInstance.id, " +
		    "    ti.processInstance.superProcessToken.id, " +
		    "    ti.id " +
		    "from " +
		    "    org.jbpm.taskmgmt.exe.TaskInstance as ti " +
		    "    join ti.pooledActors pooledActor " +
		    "where ti.actorId is null " +
		    ((nomesMeves) ? " and pooledActor.actorId = :actorId " : " and pooledActor.actorId is not null ") +  
		    ((nomesAmbPendents) ? " and ti.isSuspended = false and ti.isOpen = true " : " ");
		  
		String hql = pooled ? hqlPooled : hqlPersonal;
		
		Query query = jbpmContext.getSession().createQuery(hql);
		if (nomesMeves) query.setString("actorId", actorId);
		
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
		List<String> listadoExpedientes = new ArrayList<String>();
		// Quitamos los expedientes sin tareas activas
    	for (String id: ids) {
	    	for (Object[] fila : llistaActorId) {
	    		if (new Long(id).equals(fila[0]) && !listadoTask.contains(fila[2])) {
	    			listadoTask.add((Long)fila[2]);
	    			listadoExpedientes.add(id);
	    		}
	    	}	
    	}	

	    return listadoExpedientes;
	}

	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	@Override
	public String toString() {
		return "GetRootProcessInstancesForExpedientsWithActiveTasksCommand [actorId=" + actorId + ", ids=" + ids + ", pooled=" + pooled + ", nomesAmbPendents=" + nomesAmbPendents + ", nomesMeves=" + nomesMeves + "]";
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "actorId=" + actorId;
	}

	public GetRootProcessInstancesForExpedientsWithActiveTasksCommand actorId(String actorId) {
		setActorId(actorId);
	    return this;
	}
}
