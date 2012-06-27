/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per cancel·lar una tasca jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CancelTaskInstanceCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public CancelTaskInstanceCommand(
			long id) {
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance taskInstance = jbpmContext.getTaskInstance(id);
		boolean signalling = taskInstance.isSignalling();
		taskInstance.setSignalling(false);
		taskInstance.cancel();
		taskInstance.setSignalling(signalling);
		return taskInstance;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

}
