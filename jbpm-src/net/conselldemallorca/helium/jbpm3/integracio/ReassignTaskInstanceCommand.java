/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.identity.assignment.ExpressionAssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per cancel·lar una tasca jBPM
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ReassignTaskInstanceCommand extends AbstractBaseCommand {

	private long id;
	private String expression;



	public ReassignTaskInstanceCommand(
			long id,
			String expression) {
		super();
		this.id = id;
		this.expression = expression;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance taskInstance = jbpmContext.getTaskInstance(id);
		ExpressionAssignmentHandler assignmentHandler = new ExpressionAssignmentHandler();
		assignmentHandler.setExpression("<expression>" + expression + "</expression>");
		assignmentHandler.assign(new ProxyAssignable(taskInstance), new ExecutionContext(taskInstance.getToken()));
		return taskInstance;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public ReassignTaskInstanceCommand id(long id) {
		setId(id);
	    return this;
	}

	@SuppressWarnings("serial")
	private class ProxyAssignable implements Assignable {
		private TaskInstance taskInstance;
		public ProxyAssignable(TaskInstance taskInstance) {
			taskInstance.setActorId(null, false);
			taskInstance.setPooledActors(new String[0]);
			this.taskInstance = taskInstance;
		}
		public void setActorId(String actorId) {
			taskInstance.setActorId(actorId, false);
		}
		public void setPooledActors(String[] pooledActors) {
			taskInstance.setPooledActors(pooledActors);
		}
	}

	private static final long serialVersionUID = -1908847549444051495L;

}
