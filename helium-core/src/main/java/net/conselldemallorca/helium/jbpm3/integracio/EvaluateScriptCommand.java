/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Set;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Command per evaluar un script a dins una instància de procés jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EvaluateScriptCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String script;
	private Set<String> outputNames;

	public EvaluateScriptCommand(
			long id,
			String script,
			Set<String> outputNames) {
		super();
		this.id = id;
		this.script = script;
		this.outputNames = outputNames;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance pi = jbpmContext.getProcessInstance(id);
		Script scr = new Script();
		scr.setExpression(script);
		return scr.eval(
				scr.createInputMap(new ExecutionContext(pi.getRootToken())),
				outputNames);
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public Set<String> getOutputNames() {
		return outputNames;
	}
	public void setOutputNames(Set<String> outputNames) {
		this.outputNames = outputNames;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public EvaluateScriptCommand id(long id) {
		setId(id);
	    return this;
	}

}
