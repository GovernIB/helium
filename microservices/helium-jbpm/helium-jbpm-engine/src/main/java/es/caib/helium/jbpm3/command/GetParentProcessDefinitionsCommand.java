/**
 * 
 */
package es.caib.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * Command per obtenir la definició de procés de jBPM 3 donat el seu id
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetParentProcessDefinitionsCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public GetParentProcessDefinitionsCommand() {}

	public GetParentProcessDefinitionsCommand(long id){
		super();
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		List<ProcessDefinition> parents = new ArrayList<ProcessDefinition>();
		ProcessDefinition rootProcessDefinition = jbpmContext.getGraphSession().getProcessDefinition(id);
		if (rootProcessDefinition != null) {
			for (ProcessDefinition pd: (List<ProcessDefinition>)rootProcessDefinition.getParents()) {
				parents.add(retrieveProcessDefinition(pd));
			}
		}
		return parents;
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
	public GetParentProcessDefinitionsCommand id(long id) {
		setId(id);
	    return this;
	}

}
