package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.Token;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class ProcessInstanceInfo {

	private long id;
	private int version;
	private String key;
	private Date start;
	private Date end;
	private ProcessDefinition processDefinition;
	private Token rootToken;
	private Token superProcessToken;
	private boolean isSuspended;
	// private Map instances;
	// private Map transientInstances;
	// private List<RuntimeAction> runtimeActions;
	// private List cascadeProcessInstances;
	
	public ProcessInstanceInfo(
			long id, 
			int version, 
			String key, 
			Date start, 
			Date end,
			ProcessDefinition processDefinition, 
			Token rootToken, 
			Token superProcessToken, 
			boolean isSuspended) {
		super();
		this.id = id;
		this.version = version;
		this.key = key;
		this.start = start;
		this.end = end;
		this.processDefinition = processDefinition;
		this.rootToken = rootToken;
		this.superProcessToken = superProcessToken;
		this.isSuspended = isSuspended;
	}

	public long getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public String getKey() {
		return key;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public ProcessDefinitionInfo getProcessDefinition() {
		return ConversioTipusInfoHelper.toProcessDefinitionInfo(processDefinition);
	}

	public TokenInfo getRootToken() {
		return ConversioTipusInfoHelper.toTokenInfo(rootToken);
	}

	public TokenInfo getSuperProcessToken() {
		return ConversioTipusInfoHelper.toTokenInfo(superProcessToken);
	}

	public boolean isSuspended() {
		return isSuspended;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
