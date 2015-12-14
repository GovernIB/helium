/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Command per a reprendre instàncies de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ResumeProcessInstancesCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long[] ids;
	

	public ResumeProcessInstancesCommand(
			long[] ids) {
		super();
		this.ids = ids;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		for (long id: ids) {
			ProcessInstance processInstance = jbpmContext.getProcessInstance(id);
			processInstance.resume();
		}
		return null;
	}

	public long[] getIds() {
		return ids;
	}
	public void setIds(long[] ids) {
		this.ids = ids;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "ids=" + ids;
	}

}
