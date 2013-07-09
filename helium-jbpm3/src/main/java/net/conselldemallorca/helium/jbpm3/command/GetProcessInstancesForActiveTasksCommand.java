/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.List;

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

	public GetProcessInstancesForActiveTasksCommand() {}

	public GetProcessInstancesForActiveTasksCommand(String actorId) {
		super();
		this.actorId = actorId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object execute(JbpmContext jbpmContext) throws Exception {
		setJbpmContext(jbpmContext);
		long t0 = System.currentTimeMillis();
		Query queryActorId = jbpmContext.getSession().createQuery(
				"select " +
				"    distinct ti.processInstance.id, " +
				"    ti.processInstance.superProcessToken.id " +
				"from " +
				"    org.jbpm.taskmgmt.exe.TaskInstance as ti " +
				"where " +
				"    ti.actorId = :actorId " +
				"and ti.isSuspended = false " +
				"and ti.isOpen = true");
		queryActorId.setString("actorId", actorId);
		List llistaActorId = queryActorId.list();
		System.out.println(">>> queryActorId (" + (System.currentTimeMillis() - t0) + " ms): " + llistaActorId.size());
		t0 = System.currentTimeMillis();
		Query queryPooledActors = jbpmContext.getSession().createQuery(
				"select " +
				"    distinct ti.processInstance.id, " +
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
		List llistaPooledActors = queryPooledActors.list();
		System.out.println(">>> queryPooledActors (" + (System.currentTimeMillis() - t0) + " ms): " + llistaPooledActors.size());
		llistaActorId.addAll(llistaPooledActors);
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

	public GetProcessInstancesForActiveTasksCommand actorId(String actorId) {
		setActorId(actorId);
	    return this;
	}

}
