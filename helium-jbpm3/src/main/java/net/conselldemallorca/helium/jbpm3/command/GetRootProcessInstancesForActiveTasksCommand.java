/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

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
public class GetRootProcessInstancesForActiveTasksCommand extends AbstractGetObjectBaseCommand implements Command {

	private static final long serialVersionUID = -1908847549444051495L;
	private String actorId;

	public GetRootProcessInstancesForActiveTasksCommand() {}

	public GetRootProcessInstancesForActiveTasksCommand(String actorId) {
		super();
		this.actorId = actorId;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		setJbpmContext(jbpmContext);
		Query queryActorId = jbpmContext.getSession().createQuery(
				"select " +
				"    ti.processInstance.id, " +
				"    ti.processInstance.superProcessToken.id " +
				"from " +
				"    org.jbpm.taskmgmt.exe.TaskInstance as ti " +
				"where " +
				"    ti.actorId = :actorId " +
				"and ti.isSuspended = false " +
				"and ti.isOpen = true");
		queryActorId.setString("actorId", actorId);
		List<Object[]> llistaActorId = queryActorId.list();
		Query queryPooledActors = jbpmContext.getSession().createQuery(
				"select " +
				"    ti.processInstance.id, " +
				"    ti.processInstance.superProcessToken.id " +
				"from " +
				"    org.jbpm.taskmgmt.exe.TaskInstance ti " +
				"join " +
				"    ti.pooledActors pooledActor " +
				"where " +
				"    pooledActor.actorId = :actorId " +
				"and ti.actorId is null " +
				"and ti.isSuspended = false " +
				"and ti.isOpen = true");
		queryPooledActors.setString("actorId", actorId);
		List<Object[]> llistaPooledActors = queryPooledActors.list();
		llistaActorId.addAll(llistaPooledActors);
		// Cercar els processInstanceIds arrel
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
						"    t.id, " +
						"    t.processInstance.id, " +
						"    t.processInstance.superProcessToken.id " +
						"from " +
						"    org.jbpm.graph.exe.Token as t " +
						"where " +
						"    t.id in :superProcessTokenIds");
				queryProcessInstancesPare.setParameterList(
						"superProcessTokenIds",
						superProcessTokenIds);
				List<Object[]> llistaProcessInstancesPare = queryProcessInstancesPare.list();
				for (Object[] reg1: llistaActorId) {
					if (reg1[1] != null) {
						for (Object[] reg2: llistaProcessInstancesPare) {
							if (reg2[0].equals(reg1[1])) {
								reg1[0] = reg2[1];
								reg1[1] = reg2[2];
								break;
							}
						}
					}
				}
			}
		} while (superProcessTokenIds.size() > 0);
	    return llistaActorId;
	}

	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "actorId=" + actorId;
	}

	public GetRootProcessInstancesForActiveTasksCommand actorId(String actorId) {
		setActorId(actorId);
	    return this;
	}

}
