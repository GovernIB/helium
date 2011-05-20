package org.jbpm.command;

import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;

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
      Expedient expedientIniciant = ExpedientIniciant.getExpedient();
      if (expedientIniciant != null)
    	  expedientIniciant.setProcessInstanceId(new Long(processInstance.getId()).toString());
      /*if (startTransitionName == null || startTransitionName.length() == 0)
        processInstance.signal();
      else
        processInstance.signal(startTransitionName);*/
    }
    return object;
  }


@Override
  public String getAdditionalToStringInformation()
  {
    return super.getAdditionalToStringInformation();
  }

}
