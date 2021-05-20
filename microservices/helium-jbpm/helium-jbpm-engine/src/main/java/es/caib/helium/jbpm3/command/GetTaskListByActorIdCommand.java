/**
 * 
 */
package es.caib.helium.jbpm3.command;

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
public class GetTaskListByActorIdCommand extends AbstractGetObjectBaseCommand implements Command {

	private static final long serialVersionUID = -1908847549444051495L;
	private String actorId;

	public GetTaskListByActorIdCommand() {}

	public GetTaskListByActorIdCommand(String actorId) {
		super();
		this.actorId = actorId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object execute(JbpmContext jbpmContext) throws Exception {
		setJbpmContext(jbpmContext);
		Query queryActorId = jbpmContext.getSession().createQuery(
				"select " +
				"    ti " +
				"from " +
				"    org.jbpm.taskmgmt.exe.TaskInstance as ti " +
				"where " +
				"    ti.actorId = :actorId " +
				"and ti.isSuspended = false " +
				"and ti.isOpen = true");
		queryActorId.setString("actorId", actorId);
		List llistaActorIds = queryActorId.list();
		Query queryPooledActors = jbpmContext.getSession().createQuery(
				"select " +
				"    ti " +
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
		List llistaPooleAdctors = queryPooledActors.list();
	    return llistaActorIds.addAll(llistaPooleAdctors);
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

	public GetTaskListByActorIdCommand actorId(String actorId) {
		setActorId(actorId);
	    return this;
	}

}
