package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.def.Node;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class TransitionInfo {

	private long id = 0;
	private String name;
	private String description;
	private Node from;
	private Node to;
	private String condition;
	
	public TransitionInfo(
			long id, 
			String name, 
			String description, 
			Node from, 
			Node to, 
			String condition) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.from = from;
		this.to = to;
		this.condition = condition;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public NodeInfo getFrom() {
		return ConversioTipusInfoHelper.toNodeInfo(from);
	}

	public NodeInfo getTo() {
		return ConversioTipusInfoHelper.toNodeInfo(to);
	}

	public String getCondition() {
		return condition;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
