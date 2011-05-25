/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per reprendre una tasca jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ResumeTaskInstanceCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	

	public ResumeTaskInstanceCommand(
			long id) {
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance taskInstance = jbpmContext.getTaskInstance(id);
		taskInstance.resume();
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

	//methods for fluent programming
	public ResumeTaskInstanceCommand id(long id) {
		setId(id);
	    return this;
	}

}
