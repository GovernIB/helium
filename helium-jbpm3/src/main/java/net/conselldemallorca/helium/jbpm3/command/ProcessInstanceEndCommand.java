/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.Date;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per a retrocedir la finalització d'una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcessInstanceEndCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long[] ids;
	private Date dataFinalitzacio;

	public ProcessInstanceEndCommand(long[] ids, Date dataFinalitzacio){
		super();
		this.ids = ids;
		this.dataFinalitzacio = dataFinalitzacio;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		for (long id: ids) {
			ProcessInstance processInstance = jbpmContext.getProcessInstance(id);
			for (TaskInstance taskInstance: processInstance.getTaskMgmtInstance().getTaskInstances()) {
				if (taskInstance.isSuspended())
					taskInstance.resume();
				if (taskInstance.isOpen()) {
					taskInstance.setSignalling(false);
					taskInstance.cancel();
				}
					
			}
			processInstance.setEnd(dataFinalitzacio);
		}
		return null;
	}

	public void setIds(long[] ids) {
		this.ids = ids;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "ids=" + ids;
	}
}
