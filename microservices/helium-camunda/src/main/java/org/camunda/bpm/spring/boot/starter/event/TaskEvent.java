package org.camunda.bpm.spring.boot.starter.event;

import org.camunda.bpm.engine.delegate.DelegateTask;

import java.util.Date;

public class TaskEvent {
    protected String assignee;
    protected String caseDefinitionId;
    protected String caseExecutionId;
    protected String caseInstanceId;
    protected Date createTime;
    protected String deleteReason;
    protected String description;
    protected Date dueDate;
    protected String eventName;
    protected String executionId;
    protected Date followUpDate;
    protected String id;
    protected String name;
    protected String owner;
    protected int priority;
    protected String processDefinitionId;
    protected String processInstanceId;
    protected String taskDefinitionKey;
    protected String tenantId;

    public TaskEvent(DelegateTask delegateTask) {
        this.assignee = delegateTask.getAssignee();
        this.caseDefinitionId = delegateTask.getCaseDefinitionId();
        this.caseExecutionId = delegateTask.getCaseExecutionId();
        this.caseInstanceId = delegateTask.getCaseInstanceId();
        this.createTime = delegateTask.getCreateTime();
        this.deleteReason = delegateTask.getDeleteReason();
        this.description = delegateTask.getDescription();
        this.dueDate = delegateTask.getDueDate();
        this.eventName = delegateTask.getEventName();
        this.executionId = delegateTask.getExecutionId();
        this.followUpDate = delegateTask.getFollowUpDate();
        this.id = delegateTask.getId();
        this.name = delegateTask.getName();
        this.owner = delegateTask.getOwner();
        this.priority = delegateTask.getPriority();
        this.processDefinitionId = delegateTask.getProcessDefinitionId();
        this.processInstanceId = delegateTask.getProcessInstanceId();
        this.taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        this.tenantId = delegateTask.getTenantId();
    }

    public String getAssignee() {
        return this.assignee;
    }

    public String getCaseDefinitionId() {
        return this.caseDefinitionId;
    }

    public String getCaseExecutionId() {
        return this.caseExecutionId;
    }

    public String getCaseInstanceId() {
        return this.caseInstanceId;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public String getDeleteReason() {
        return this.deleteReason;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public String getEventName() {
        return this.eventName;
    }

    public String getExecutionId() {
        return this.executionId;
    }

    public Date getFollowUpDate() {
        return this.followUpDate;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getOwner() {
        return this.owner;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getProcessDefinitionId() {
        return this.processDefinitionId;
    }

    public String getProcessInstanceId() {
        return this.processInstanceId;
    }

    public String getTaskDefinitionKey() {
        return this.taskDefinitionKey;
    }

    public String getTenantId() {
        return this.tenantId;
    }
}
