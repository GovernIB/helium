package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class TokenInfo {

	private long id;
	private String name;
	private Date start;
	private Date end;
	private Node node;
	private Date nodeEnter;
	private ProcessInstance processInstance;
	private Token parent;
	private Map<String, Token> children;
//	private List<Comment> comments;
	private ProcessInstance subProcessInstance;
	private boolean isAbleToReactivateParent;
	private boolean isTerminationImplicit;
	private boolean isSuspended;
	private String lock;
	
	public TokenInfo(
			long id, 
			String name, 
			Date start, 
			Date end, 
			Node node, 
			Date nodeEnter,
			ProcessInstance processInstance, 
			Token parent, 
			Map<String, Token> children, 
			ProcessInstance subProcessInstance, 
			boolean isAbleToReactivateParent,
			boolean isTerminationImplicit, 
			boolean isSuspended, 
			String lock) {
		super();
		this.id = id;
		this.name = name;
		this.start = start;
		this.end = end;
		this.node = node;
		this.nodeEnter = nodeEnter;
		this.processInstance = processInstance;
		this.parent = parent;
		this.children = children;
		this.subProcessInstance = subProcessInstance;
		this.isAbleToReactivateParent = isAbleToReactivateParent;
		this.isTerminationImplicit = isTerminationImplicit;
		this.isSuspended = isSuspended;
		this.lock = lock;
	}
	
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Date getStart() {
		return start;
	}
	public Date getEnd() {
		return end;
	}
	public NodeInfo getNode() {
		return ConversioTipusInfoHelper.toNodeInfo(node);
	}
	public Date getNodeEnter() {
		return nodeEnter;
	}
	public ProcessInstanceInfo getProcessInstance() {
		return ConversioTipusInfoHelper.toProcessInstanceInfo(processInstance);
	}
	public TokenInfo getParent() {
		return ConversioTipusInfoHelper.toTokenInfo(parent);
	}
	public Map<String, TokenInfo> getChildren() {
		Map<String, TokenInfo> tokensFills = new HashMap<String, TokenInfo>();
		for (Entry<String, Token> entry: children.entrySet()) {
			tokensFills.put(entry.getKey(), ConversioTipusInfoHelper.toTokenInfo(entry.getValue()));
		}
		return tokensFills;
	}
	public ProcessInstanceInfo getSubProcessInstance() {
		return ConversioTipusInfoHelper.toProcessInstanceInfo(subProcessInstance);
	}
	public boolean isAbleToReactivateParent() {
		return isAbleToReactivateParent;
	}
	public boolean isTerminationImplicit() {
		return isTerminationImplicit;
	}
	public boolean isSuspended() {
		return isSuspended;
	}
	public String getLock() {
		return lock;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
