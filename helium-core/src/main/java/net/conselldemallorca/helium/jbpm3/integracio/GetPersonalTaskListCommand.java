/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Iterator;
import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.command.Command;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per obtenir la llista de tasques personals
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetPersonalTaskListCommand extends AbstractGetObjectBaseCommand implements Command {

	private static final long serialVersionUID = -1908847549444051495L;
	private String actorId;

	public GetPersonalTaskListCommand() {}

	public GetPersonalTaskListCommand(String actorId) {
		super();
		this.actorId = actorId;
	}

	@SuppressWarnings("unchecked")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		setJbpmContext(jbpmContext);
	    List result = jbpmContext.getTaskList(actorId);
	    //return retrieveTaskInstanceDetails(result);
	    return result;
	}

	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "actorId=" + actorId;
	}

	//methods for fluent programming
	public GetPersonalTaskListCommand actorId(String actorId) {
		setActorId(actorId);
	    return this;
	}



	@SuppressWarnings("unchecked")
	public List retrieveTaskInstanceDetails(List taskInstanceList) {
		for (Iterator iter = taskInstanceList.iterator(); iter.hasNext();) {
			retrieveTaskInstanceDetails((TaskInstance)iter.next());
		}
		return taskInstanceList;
	}

}
