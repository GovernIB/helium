/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.logging.log.MessageLog;

/**
 * Command per a afegir un log associats a una instància de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AddTaskInstanceMessageLogCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String message;

	public AddTaskInstanceMessageLogCommand(
			long id,
			String message) {
		super();
		this.id = id;
		this.message = message;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		MessageLog log = new MessageLog(message);
		log.setToken(jbpmContext.getTaskInstance(id).getToken());
		ProcessInstance processInstance = jbpmContext.getTaskInstance(id).getProcessInstance();
		processInstance.getLoggingInstance().addLog(log);
		jbpmContext.save(processInstance);
		return new Long(log.getId());
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id ;
	}

}
