/**
 * 
 */
package es.caib.helium.jbpm3.command;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Command per esborrar una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignalProcessInstanceCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String startTransitionName;

	public SignalProcessInstanceCommand(long id){
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance processInstance = jbpmContext.getProcessInstance(id);
		if (processInstance != null) {
			if (startTransitionName == null || startTransitionName.length() == 0)
				processInstance.signal();
			else
				processInstance.signal(startTransitionName);
		}
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getStartTransitionName() {
		return startTransitionName;
	}
	public void setStartTransitionName(String startTransitionName) {
		this.startTransitionName = startTransitionName;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

}
