/**
 * 
 */
package es.caib.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * Command per obtenir les accions globals d'una instància de procés jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ListActionsCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public ListActionsCommand(
			long id) {
		super();
		this.id = id;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object execute(JbpmContext jbpmContext) throws Exception {
		List<String> accions = new ArrayList<String>();
		ProcessDefinition processDefinition = jbpmContext.getGraphSession().getProcessDefinition(id);
		if (processDefinition != null) {
			Map actions = processDefinition.getActions();
			for (String accio: (Set<String>)actions.keySet())
				accions.add(accio);
		}
		return accions;
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
	public ListActionsCommand id(long id) {
		setId(id);
	    return this;
	}

}
