/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per a agafar una instància de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TakeTaskInstanceCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String actorId;

	public TakeTaskInstanceCommand(
			long id,
			String actorId) {
		super();
		this.id = id;
		this.actorId = actorId;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance taskInstance = jbpmContext.getTaskInstance(id);
		taskInstance.setActorId(actorId);
		return taskInstance;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id + ", actorId=" + actorId;
	}

}
