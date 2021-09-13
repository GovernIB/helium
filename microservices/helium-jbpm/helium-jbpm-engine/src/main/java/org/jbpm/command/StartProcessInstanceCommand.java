package org.jbpm.command;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;

import net.conselldemallorca.helium.api.dto.ExpedientDto;
import net.conselldemallorca.helium.api.dto.ProcesDto;
import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

/**
 * Graph command to start a new process and signal it immidiatly. The transition named in
 * <code>startTransitionName</code> is used (or the default transition if it is null).
 * 
 * The result of this command, if requested, is a {@link Long} value containing the process instance id.
 * 
 * @author Jim Rigsbee, Tom Baeyens, Bernd Ruecker
 */
public class StartProcessInstanceCommand extends NewProcessInstanceCommand implements Command
{

  private static final long serialVersionUID = -2428234069404269048L;

  public Object execute(JbpmContext jbpmContext) throws Exception
  {
    Object object = super.execute(jbpmContext);
    if (object instanceof ProcessInstance) {
      ProcessInstance processInstance = (ProcessInstance)object;
      Task startTask = processInstance.getProcessDefinition().getTaskMgmtDefinition().getStartTask();
      if (startTask != null && startTask.getSwimlane() != null) {
    	  SwimlaneInstance si = new SwimlaneInstance(startTask.getSwimlane());
    	  si.setActorId(getActorId());
    	  processInstance.getTaskMgmtInstance().addSwimlaneInstance(si);
      }
      ExpedientDto expedientIniciant = Jbpm3HeliumBridge.getInstanceService().getExpedientIniciant();
      if (expedientIniciant != null)
    	  expedientIniciant.setProcessInstanceId(new Long(processInstance.getId()).toString());
      /*if (startTransitionName == null || startTransitionName.length() == 0)
        processInstance.signal();
      else
        processInstance.signal(startTransitionName);*/
      // MS dona d'alta el procés al MS d'expedients, processos i tasques
    }
    return object;
  }


@Override
  public String getAdditionalToStringInformation()
  {
    return super.getAdditionalToStringInformation();
  }

}
