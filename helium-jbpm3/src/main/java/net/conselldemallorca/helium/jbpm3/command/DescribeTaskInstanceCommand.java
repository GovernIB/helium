/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per a descriure una instància de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DescribeTaskInstanceCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String description;

	public DescribeTaskInstanceCommand(
			long id,
			String description) {
		super();
		this.id = id;
		this.description = description;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance taskInstance = jbpmContext.getTaskInstance(id);
		taskInstance.setDescription(description);
		return taskInstance;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id + ", description=" + description;
	}

}
