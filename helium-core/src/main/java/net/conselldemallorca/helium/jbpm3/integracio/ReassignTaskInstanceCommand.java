/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per reassignar una taskInstance de jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReassignTaskInstanceCommand extends AbstractBaseCommand {

	private long id;
	private String actorId;
	private String[] pooledActors;
	private String expression;



	public ReassignTaskInstanceCommand(
			long id) {
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance taskInstance = jbpmContext.getTaskInstance(id);
		if (actorId != null) {
			taskInstance.setActorId(actorId);
		} else if (pooledActors != null) {
			taskInstance.setActorId(null);
			taskInstance.setPooledActors(pooledActors);
		} else if (expression != null) {
			String exprTxt = "<expression>" + expression + "</expression>";
			ExpressionAssignmentHandler assignmentHandler = new ExpressionAssignmentHandler();
			assignmentHandler.setExpression(exprTxt);
			assignmentHandler.assign(new ProxyAssignable(taskInstance), new ExecutionContext(taskInstance.getToken()));
		}
		return taskInstance;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	public String[] getPooledActors() {
		return pooledActors;
	}
	public void setPooledActors(String[] pooledActors) {
		this.pooledActors = pooledActors;
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
