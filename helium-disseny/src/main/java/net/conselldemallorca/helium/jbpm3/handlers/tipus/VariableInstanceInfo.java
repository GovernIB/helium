package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class VariableInstanceInfo {

//	private long id = 0;
//	private int version = 0;
	private String name;
	private Token token;
//	private TokenVariableMap tokenVariableMap;
	private ProcessInstance processInstance;
//	private Converter converter;
	private Object value;
	
	public VariableInstanceInfo(
			String name, 
			Token token, 
			ProcessInstance processInstance,
			Object value) {
		super();
		this.name = name;
		this.token = token;
		this.processInstance = processInstance;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public TokenInfo getToken() {
		return ConversioTipusInfoHelper.toTokenInfo(token);
	}

	public ProcessInstanceInfo getProcessInstance() {
		return ConversioTipusInfoHelper.toProcessInstanceInfo(processInstance);
	}

	public Object getValue() {
		return value;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
