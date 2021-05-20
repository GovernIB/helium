/**
 * 
 */
package es.caib.helium.jbpm3.command;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Command per a trobar els logs associats a una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DeleteProcessInstanceLogsCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	
	private ProcessInstance processInstance;

	public DeleteProcessInstanceLogsCommand(
			ProcessInstance processInstance) {
		super();
		this.processInstance = processInstance;
	}

//	@SuppressWarnings("rawtypes")
	public Object execute(JbpmContext jbpmContext) throws Exception {
		Query query = jbpmContext.getSession().createQuery(
				  "delete org.jbpm.logging.log.ProcessLog "
				+ "where id in ("
				+ "		select pl.id "
				+ "		from org.jbpm.logging.log.ProcessLog as pl "
				+ "		where pl.token.processInstance = :processInstance)");
		query.setEntity("processInstance", processInstance);
		query.executeUpdate();
//		Query query = jbpmContext.getSession().getNamedQuery("GraphSession.findLogsForProcessInstance");
//		query.setEntity("processInstance", processInstance);
//		List logs = query.list();
//	    Iterator iter = logs.iterator();
//	    while (iter.hasNext())
//	    {
//	      ProcessLog processLog = (ProcessLog)iter.next();
//	      jbpmContext.getSession().delete(processLog);
//	    }
		return null;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}
	public void setId(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "processInstance=" + processInstance ;
	}
}
