/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per canviar el nom a una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ChangeTaskNameCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String name;

	public ChangeTaskNameCommand(
			long id,
			String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance ti = jbpmContext.getTaskInstance(id);
		ti.setName(name);
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public ChangeTaskNameCommand id(long id) {
		setId(id);
	    return this;
	}

}
