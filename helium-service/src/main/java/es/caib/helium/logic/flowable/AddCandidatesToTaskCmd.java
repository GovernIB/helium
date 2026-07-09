package es.caib.helium.logic.flowable;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.compatibility.Flowable5CompatibilityHandler;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.cmd.AddIdentityLinkCmd;
import org.flowable.engine.impl.cmd.NeedsActiveTaskCmd;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.Flowable5Util;
import org.flowable.engine.impl.util.IdentityLinkUtil;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.identitylink.service.IdentityLinkService;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.Arrays;

/**
 * CMD per assignar de forma massiva a multiples usuaris a una tasca
 */
public class AddCandidatesToTaskCmd extends NeedsActiveTaskCmd<Void> {
	private static final long serialVersionUID = 1L;

	protected String[] identityId;
	protected String group;

	public AddCandidatesToTaskCmd(String taskId) {
		super(taskId);
		validateParams(taskId);
		this.taskId = taskId;
		this.identityId = null;
		this.group = null;
	}

	public AddCandidatesToTaskCmd(String taskId, String[] identityId, String group) {
		super(taskId);
		validateParams(taskId);
		this.taskId = taskId;
		this.identityId = identityId;
		this.group = group;
	}

	protected void validateParams(String taskId) {
		if (taskId == null) {
			throw new FlowableIllegalArgumentException("taskId is null");
		}
	}

	@Override
	protected Void execute(CommandContext commandContext, TaskEntity task) {
		ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);
		IdentityLinkService identityLinkService = processEngineConfiguration.getIdentityLinkServiceConfiguration()
			.getIdentityLinkService();

		if(identityId == null) {
			identityLinkService.deleteIdentityLinksByTaskId(taskId);
			return null;
		}

		if (task.getProcessDefinitionId() != null && Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, task.getProcessDefinitionId())) {
			Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
			for(String identity : identityId)
				compatibilityHandler.addIdentityLink(taskId, identity, AddIdentityLinkCmd.IDENTITY_USER, IdentityLinkType.CANDIDATE);
			return null;
		}

		for(String identity : identityId) {
			IdentityLinkEntity identityLinkEntity =  identityLinkService.createTaskIdentityLink(task.getId(), identity, null, IdentityLinkType.CANDIDATE);
			IdentityLinkUtil.handleTaskIdentityLinkAddition(task, identityLinkEntity);
		}

		if(this.group != null) {
//			CommandContextUtil.getHistoryManager(commandContext).createGroupIdentityLinkComment(task, this.group, IdentityLinkType.CANDIDATE, true);
			IdentityLinkEntity identityLinkEntity = identityLinkService.createTaskIdentityLink(task.getId(), null, this.group, IdentityLinkType.CANDIDATE);
			IdentityLinkUtil.handleTaskIdentityLinkAddition(task, identityLinkEntity);
		}

		return null;
	}
}
