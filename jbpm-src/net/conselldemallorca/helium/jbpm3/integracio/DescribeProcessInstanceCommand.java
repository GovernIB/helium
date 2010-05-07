/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Command per esborrar una instància de procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DescribeProcessInstanceCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String description;

	public DescribeProcessInstanceCommand() {}

	public DescribeProcessInstanceCommand(long id){
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance processInstance = jbpmContext.getProcessInstance(id);
		if (processInstance != null)
			processInstance.setKey(description);
		return null;
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
	    return "id=" + id;
	}

	// methods for fluent programming
	public DescribeProcessInstanceCommand id(long id, String description) {
		setId(id);
		setDescription(description);
	    return this;
	}

}
