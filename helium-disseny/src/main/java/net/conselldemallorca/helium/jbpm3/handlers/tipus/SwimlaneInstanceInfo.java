package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class SwimlaneInstanceInfo {

	private long id = 0;
//	private int version = 0;
	private String name = null;
	private String actorId = null;
	private Set<PooledActor> pooledActors = null;
	private Swimlane swimlane = null;
	private TaskMgmtInstance taskMgmtInstance = null;
	
	public SwimlaneInstanceInfo(
			long id, 
//			int version, 
			String name, 
			String actorId, 
			Set<PooledActor> pooledActors,
			Swimlane swimlane, 
			TaskMgmtInstance taskMgmtInstance) {
		super();
		this.id = id;
//		this.version = version;
		this.name = name;
		this.actorId = actorId;
		this.pooledActors = pooledActors;
		this.swimlane = swimlane;
		this.taskMgmtInstance = taskMgmtInstance;
	}

	public long getId() {
		return id;
	}

//	public int getVersion() {
//		return version;
//	}

	public String getName() {
		return name;
	}

	public String getActorId() {
		return actorId;
	}

	public Set<PooledActorInfo> getPooledActors() {
		Set<PooledActorInfo> pooledActorsInfo = new HashSet<PooledActorInfo>();
		for (PooledActor pa: pooledActors) {
			pooledActorsInfo.add(ConversioTipusInfoHelper.toPooledActorInfo(pa));
		}
		return pooledActorsInfo;
	}

	public SwimlaneInfo getSwimlane() {
		return ConversioTipusInfoHelper.toSwimlaneInfo(swimlane);
	}

	public TaskMgmtInstanceInfo getTaskMgmtInstance() {
		return ConversioTipusInfoHelper.toTaskMgmtInstanceInfo(taskMgmtInstance);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
