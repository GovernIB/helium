package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Node;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class ProcessDefinitionInfo {

	private long id = 0;
	private String name;
	private String description;
	// private Map<String, EventInfo> events;
	// private List exceptionHandlers;
	private int version;
	private boolean isTerminationImplicit;
	private Node startState;
	private List<Node> nodes;
	private Map<String, Action> actions;
	// private Map definitions;
	
	public ProcessDefinitionInfo(
			long id, 
			String name, 
			String description, 
			int version, 
			boolean isTerminationImplicit,
			Node startState, 
			List<Node> nodes, 
			Map<String, Action> actions) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.version = version;
		this.isTerminationImplicit = isTerminationImplicit;
		this.startState = startState;
		this.nodes = nodes;
		this.actions = actions;
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
	
	public int getVersion() {
		return version;
	}
	
	public boolean isTerminationImplicit() {
		return isTerminationImplicit;
	}
	
	public NodeInfo getStartState() {
		return ConversioTipusInfoHelper.toNodeInfo(startState);
	}
	
	public List<NodeInfo> getNodes() {
		List<NodeInfo> nodesInfo = new ArrayList<NodeInfo>();
		for (Node node: nodes) {
			nodesInfo.add(ConversioTipusInfoHelper.toNodeInfo(node));
		}
		return nodesInfo;
	}
	
	public Map<String, ActionInfo> getActions() {
		Map<String, ActionInfo> actionsInfo = new HashMap<String, ActionInfo>();
		for (Entry<String, Action> entry: actions.entrySet()) {
			actionsInfo.put(entry.getKey(), ConversioTipusInfoHelper.toActionInfo(entry.getValue()));
		}
		return actionsInfo;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
