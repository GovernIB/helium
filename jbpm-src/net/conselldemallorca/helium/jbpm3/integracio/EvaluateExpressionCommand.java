/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Map;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.jpdl.el.impl.JbpmVariableResolver;

/**
 * Command per evaluar una expression a dins una instància de procés jBPM
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class EvaluateExpressionCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long pid;
	private long tid = -1;
	private String expression;
	private Map<String, Object> valors;

	public EvaluateExpressionCommand(
			long pid,
			String expression) {
		super();
		this.pid = pid;
		this.expression = expression;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance pi = jbpmContext.getProcessInstance(pid);
		ExecutionContext ec = new ExecutionContext(pi.getRootToken());
		if (tid != -1)
			ec.setTaskInstance(jbpmContext.getTaskInstance(tid));
		if (valors == null || valors.size() == 0) {
			return JbpmExpressionEvaluator.evaluate(
					expression,
					ec);
		} else {
			return JbpmExpressionEvaluator.evaluate(
					expression,
					ec,
					new HeliumVariableResolver(valors),
					JbpmExpressionEvaluator.getUsedFunctionMapper());
		}
	}

	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public long getTid() {
		return tid;
	}
	public void setTid(long tid) {
		this.tid = tid;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public Map<String, Object> getValors() {
		return valors;
	}
	public void setValors(Map<String, Object> valors) {
		this.valors = valors;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "pid=" + pid;
	}

	//methods for fluent programming
	public EvaluateExpressionCommand pid(long pid) {
		setPid(pid);
	    return this;
	}

	private class HeliumVariableResolver extends JbpmVariableResolver {
		private Map<String, Object> valors;
		public HeliumVariableResolver(Map<String, Object> valors) {
			this.valors = valors;
		}
		public Object resolveVariable(String name) throws ELException {
			if (valors != null) {
				Object valor = valors.get(name);
				if (valor != null)
					return valor;
			}
			return super.resolveVariable(name);
		}
	}

}
