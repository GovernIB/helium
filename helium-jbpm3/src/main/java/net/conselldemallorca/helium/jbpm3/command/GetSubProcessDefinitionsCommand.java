/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.ProcessState;

/**
 * Command per obtenir els subprocessos donada la id d'una definició de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetSubProcessDefinitionsCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public GetSubProcessDefinitionsCommand() {}

	public GetSubProcessDefinitionsCommand(long id){
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		List<ProcessDefinition> subprocesses = new ArrayList<ProcessDefinition>();
		ProcessDefinition rootProcessDefinition = jbpmContext.getGraphSession().getProcessDefinition(id);
		if (rootProcessDefinition != null) {
			for (Node node: rootProcessDefinition.getNodes()) {
				if (node instanceof ProcessState) {
					ProcessState ps = (ProcessState)node;
					if (ps.getSubProcessDefinition() != null)
						subprocesses.add(retrieveProcessDefinition(ps.getSubProcessDefinition()));
				}
			}
		}
		return subprocesses;
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
	public GetSubProcessDefinitionsCommand id(long id) {
		setId(id);
	    return this;
	}

}
