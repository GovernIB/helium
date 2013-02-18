/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.Date;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.job.Timer;

/**
 * Command que suspen un timer
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SuspendProcessInstanceTimerCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private Date dueDate;



	public SuspendProcessInstanceTimerCommand(
			long id) {
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Timer timer = jbpmContext.getJobSession().loadTimer(id);
		timer.setSuspended(true);
		if (dueDate != null)
			timer.setDueDate(dueDate);
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public SuspendProcessInstanceTimerCommand id(long id) {
		setId(id);
	    return this;
	}

}
