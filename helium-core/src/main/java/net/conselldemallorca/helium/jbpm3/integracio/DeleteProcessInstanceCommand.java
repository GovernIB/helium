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
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DeleteProcessInstanceCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public DeleteProcessInstanceCommand() {}

	public DeleteProcessInstanceCommand(long id){
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance processInstance = jbpmContext.getProcessInstance(id);
		if (processInstance != null)
			jbpmContext.getGraphSession().deleteProcessInstance(processInstance);
		return null;
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
	public DeleteProcessInstanceCommand id(long id) {
		setId(id);
	    return this;
	}

}
