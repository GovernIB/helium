package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.def.Action;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class EventInfo {

	private long id = 0;
	private String eventType = null;
	//private GraphElement graphElement = null; ???
	private List<Action> actions = null;
	
	public EventInfo(
			long id, 
			String eventType, 
			List<Action> actions) {
		super();
		this.id = id;
		this.eventType = eventType;
		this.actions = actions;
	}

	public long getId() {
		return id;
	}

	public String getEventType() {
		return eventType;
	}

	public List<ActionInfo> getActions() {
		List<ActionInfo> actionsInfo = new ArrayList<ActionInfo>();
		for (Action action: actions) {
			actionsInfo.add(ConversioTipusInfoHelper.toActionInfo(action));
		}
		return actionsInfo;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
