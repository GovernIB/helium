/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;

/**
 * Command per evaluar una expression a dins una instància de procés jBPM
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class EvaluateExpressionCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String expression;

	public EvaluateExpressionCommand(
			long id,
			String expression) {
		super();
		this.id = id;
		this.expression = expression;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance pi = jbpmContext.getProcessInstance(id);
		return JbpmExpressionEvaluator.evaluate(expression, new ExecutionContext(pi.getRootToken())); 
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
	public EvaluateExpressionCommand id(long id) {
		setId(id);
	    return this;
	}

}
