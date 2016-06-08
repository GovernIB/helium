package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

import net.conselldemallorca.helium.jbpm3.helper.ConversioTipusInfoHelper;

public class TimerInfo {

	private long id;
	private int version;
	private Date dueDate;
	private ProcessInstance processInstance;
	private Token token;
	private TaskInstance taskInstance;
	private boolean isSuspended;
	private boolean isExclusive;
	private String lockOwner;
	private Date lockTime;
	private String exception;
	private int retries = 1;
	private String configuration;
	private String name;
	private String repeat;
	private String transitionName = null;
	private Action action = null;
//	GraphElement graphElement = null;
	
	public TimerInfo(long id, int version, Date dueDate, ProcessInstance processInstance, Token token,
			TaskInstance taskInstance, boolean isSuspended, boolean isExclusive, String lockOwner, Date lockTime,
			String exception, int retries, String configuration, String name, String repeat, String transitionName,
			Action action) {
		super();
		this.id = id;
		this.version = version;
		this.dueDate = dueDate;
		this.processInstance = processInstance;
		this.token = token;
		this.taskInstance = taskInstance;
		this.isSuspended = isSuspended;
		this.isExclusive = isExclusive;
		this.lockOwner = lockOwner;
		this.lockTime = lockTime;
		this.exception = exception;
		this.retries = retries;
		this.configuration = configuration;
		this.name = name;
		this.repeat = repeat;
		this.transitionName = transitionName;
		this.action = action;
	}
	
	public long getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public ProcessInstanceInfo getProcessInstance() {
		return ConversioTipusInfoHelper.toProcessInstanceInfo(processInstance);
	}

	public TokenInfo getToken() {
		return ConversioTipusInfoHelper.toTokenInfo(token);
	}

	public TaskInstanceInfo getTaskInstance() {
		return ConversioTipusInfoHelper.toTaskInstanceInfo(taskInstance);
	}

	public boolean isSuspended() {
		return isSuspended;
	}

	public boolean isExclusive() {
		return isExclusive;
	}

	public String getLockOwner() {
		return lockOwner;
	}

	public Date getLockTime() {
		return lockTime;
	}

	public String getException() {
		return exception;
	}

	public int getRetries() {
		return retries;
	}

	public String getConfiguration() {
		return configuration;
	}

	public String getName() {
		return name;
	}

	public String getRepeat() {
		return repeat;
	}

	public String getTransitionName() {
		return transitionName;
	}

	public ActionInfo getAction() {
		return ConversioTipusInfoHelper.toActionInfo(action);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
