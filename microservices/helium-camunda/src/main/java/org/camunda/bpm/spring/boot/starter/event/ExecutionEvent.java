package org.camunda.bpm.spring.boot.starter.event;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public class ExecutionEvent {
    protected String activityInstanceId;
    protected String businessKey;
    protected String currentActivityId;
    protected String currentActivityName;
    protected String currentTransitionId;
    protected String eventName;
    protected String id;
    protected String parentActivityInstanceId;
    protected String parentId;
    protected String processBusinessKey;
    protected String processDefinitionId;
    protected String processInstanceId;
    protected String tenantId;

    public ExecutionEvent(DelegateExecution delegateExecution) {
        this.activityInstanceId = delegateExecution.getActivityInstanceId();
        this.businessKey = delegateExecution.getBusinessKey();
        this.currentActivityId = delegateExecution.getCurrentActivityId();
        this.currentActivityName = delegateExecution.getCurrentActivityName();
        this.currentTransitionId = delegateExecution.getCurrentTransitionId();
        this.eventName = delegateExecution.getEventName();
        this.id = delegateExecution.getId();
        this.parentActivityInstanceId = delegateExecution.getParentActivityInstanceId();
        this.parentId = delegateExecution.getParentId();
        this.processBusinessKey = delegateExecution.getProcessBusinessKey();
        this.processDefinitionId = delegateExecution.getProcessDefinitionId();
        this.processInstanceId = delegateExecution.getProcessInstanceId();
        this.tenantId = delegateExecution.getTenantId();
    }

    public String getActivityInstanceId() {
        return this.activityInstanceId;
    }

    public String getBusinessKey() {
        return this.businessKey;
    }

    public String getCurrentActivityId() {
        return this.currentActivityId;
    }

    public String getCurrentActivityName() {
        return this.currentActivityName;
    }

    public String getCurrentTransitionId() {
        return this.currentTransitionId;
    }

    public String getEventName() {
        return this.eventName;
    }

    public String getId() {
        return this.id;
    }

    public String getParentActivityInstanceId() {
        return this.parentActivityInstanceId;
    }

    public String getParentId() {
        return this.parentId;
    }

    public String getProcessBusinessKey() {
        return this.processBusinessKey;
    }

    public String getProcessDefinitionId() {
        return this.processDefinitionId;
    }

    public String getProcessInstanceId() {
        return this.processInstanceId;
    }

    public String getTenantId() {
        return this.tenantId;
    }
}
