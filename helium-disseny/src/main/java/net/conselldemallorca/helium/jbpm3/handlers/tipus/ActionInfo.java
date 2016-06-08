package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.def.ProcessDefinition;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class ActionInfo {
	private long id;
	private String name;
	private boolean isPropagationAllowed;
	private boolean isAsync;
	private boolean isAsyncExclusive;
	// private ActionInfo referencedAction;
	// private Delegation actionDelegation;
	private String actionExpression;
	// private Event event = null;
	private ProcessDefinition processDefinition;
	
	public ActionInfo(
			long id, 
			String name, 
			boolean isPropagationAllowed, 
			boolean isAsync, 
			boolean isAsyncExclusive,
			String actionExpression, 
			ProcessDefinition processDefinition) {
		super();
		this.id = id;
		this.name = name;
		this.isPropagationAllowed = isPropagationAllowed;
		this.isAsync = isAsync;
		this.isAsyncExclusive = isAsyncExclusive;
		this.actionExpression = actionExpression;
		this.processDefinition = processDefinition;
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isPropagationAllowed() {
		return isPropagationAllowed;
	}
	
	public boolean isAsync() {
		return isAsync;
	}
	
	public boolean isAsyncExclusive() {
		return isAsyncExclusive;
	}
	
	public String getActionExpression() {
		return actionExpression;
	}
	
	public ProcessDefinitionInfo getProcessDefinition() {
		return ConversioTipusInfoHelper.toProcessDefinitionInfo(processDefinition);
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
