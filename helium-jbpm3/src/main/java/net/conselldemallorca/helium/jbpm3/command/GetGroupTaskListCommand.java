/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per obtenir el llistat de tasques de grup
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetGroupTaskListCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private String actorId;

	public GetGroupTaskListCommand() {}

	public GetGroupTaskListCommand(String actorId) {
		super();
		this.actorId = actorId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object execute(JbpmContext jbpmContext) throws Exception {
		setJbpmContext(jbpmContext);
		List actorIds = new ArrayList();
		actorIds.add(actorId);
	    List result = jbpmContext.getGroupTaskList(actorIds);
	    return result;
	    //return retrieveTaskInstanceDetails(result);
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
	public GetGroupTaskListCommand actorId(String actorId) {
		setActorId(actorId);
	    return this;
	}



	@SuppressWarnings("rawtypes")
	public List retrieveTaskInstanceDetails(List taskInstanceList) {
		for (Iterator iter = taskInstanceList.iterator(); iter.hasNext();) {
			retrieveTaskInstanceDetails((TaskInstance)iter.next());
		}
		return taskInstanceList;
	}

}
