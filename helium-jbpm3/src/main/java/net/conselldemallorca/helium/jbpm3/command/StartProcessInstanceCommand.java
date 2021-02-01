package net.conselldemallorca.helium.jbpm3.command;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;

import org.jbpm.JbpmContext;
import org.jbpm.command.Command;
import org.jbpm.command.NewProcessInstanceCommand;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;

/**
 * Graph command to start a new process and signal it immidiatly. The transition
 * named in <code>startTransitionName</code> is used (or the default transition
 * if it is null).
 * 
 * The result of this command, if requested, is a {@link Long} value containing
 * the process instance id.
 * 
 * @author Jim Rigsbee, Tom Baeyens, Bernd Ruecker
 */
public class StartProcessInstanceCommand extends NewProcessInstanceCommand implements Command {
	private static final long serialVersionUID = -3126723819384649394L;

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Object object = super.execute(jbpmContext);
		if (object instanceof ProcessInstance) {
			ProcessInstance processInstance = (ProcessInstance) object;
			Task startTask = processInstance.getProcessDefinition().getTaskMgmtDefinition().getStartTask();
			if (startTask != null && startTask.getSwimlane() != null) {
				SwimlaneInstance si = new SwimlaneInstance(startTask.getSwimlane());
				si.setActorId(getActorId());
				processInstance.getTaskMgmtInstance().addSwimlaneInstance(si);
			}
			ExpedientDto expedientIniciant = Jbpm3HeliumBridge.getInstanceService().getExpedientIniciant();
			if (expedientIniciant != null) {
				expedientIniciant.setProcessInstanceId(new Long(processInstance.getId()).toString());
				if (expedientIniciant.isAmbRetroaccio())
					jbpmContext.addAutoSaveProcessInstance(processInstance);
			}
		}
		return object;
	}

	@Override
	public String getAdditionalToStringInformation() {
		return super.getAdditionalToStringInformation();
	}

}
