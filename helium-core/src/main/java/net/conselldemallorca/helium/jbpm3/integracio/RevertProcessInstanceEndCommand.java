/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Command per a retrocedir la finalització d'una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RevertProcessInstanceEndCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public RevertProcessInstanceEndCommand(long id){
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance processInstance = jbpmContext.getProcessInstance(id);
		processInstance.setEnd(null);
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

}
