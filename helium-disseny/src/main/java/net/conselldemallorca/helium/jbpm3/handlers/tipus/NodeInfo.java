package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class NodeInfo {

	private long id = 0;
	private String name;
	private String description;
	private ProcessDefinition processDefinition;
	// private Map<String, EventInfo> events;
	// private List exceptionHandlers;
	private List<Transition> leavingTransitions;
	private Set<Transition> arrivingTransitions;
	private Action action;
	// private SuperState superState;
	private boolean isAsync;
	private boolean isAsyncExclusive;
	
	public NodeInfo(
			long id, 
			String name, 
			String description, 
			ProcessDefinition processDefinition,
			List<Transition> leavingTransitions, 
			Set<Transition> arrivingTransitions, 
			Action action,
			boolean isAsync, 
			boolean isAsyncExclusive) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.processDefinition = processDefinition;
		this.leavingTransitions = leavingTransitions;
		this.arrivingTransitions = arrivingTransitions;
		this.action = action;
		this.isAsync = isAsync;
		this.isAsyncExclusive = isAsyncExclusive;
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

	public ProcessDefinitionInfo getProcessDefinition() {
		return ConversioTipusInfoHelper.toProcessDefinitionInfo(processDefinition);
	}

	public List<TransitionInfo> getLeavingTransitions() {
		List<TransitionInfo> leavingTransitionsInfo = new ArrayList<TransitionInfo>();
		for (Transition t: leavingTransitions) {
			leavingTransitionsInfo.add(ConversioTipusInfoHelper.toTransitionInfo(t));
		}
		return leavingTransitionsInfo;
	}

	public Set<TransitionInfo> getArrivingTransitions() {
		Set<TransitionInfo> arrivingTransitionsInfo = new HashSet<TransitionInfo>();
		for (Transition t: arrivingTransitions) {
			arrivingTransitionsInfo.add(ConversioTipusInfoHelper.toTransitionInfo(t));
		}
		return arrivingTransitionsInfo;
	}

	public ActionInfo getAction() {
		return ConversioTipusInfoHelper.toActionInfo(action);
	}

	public boolean isAsync() {
		return isAsync;
	}

	public boolean isAsyncExclusive() {
		return isAsyncExclusive;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
