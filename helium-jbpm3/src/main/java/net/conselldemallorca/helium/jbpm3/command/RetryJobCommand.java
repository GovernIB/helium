/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.db.JobSession;
import org.jbpm.job.Job;

/**
 * Command per a agafar una instància de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RetryJobCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long jobId;

	public RetryJobCommand(long jobId) {
		super();
		this.jobId = jobId;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		JobSession jobSession = jbpmContext.getJobSession();
		Job job = jobSession.loadJob(jobId);
	
		job.setRetries(job.getRetries() - 1);
		jobSession.saveJob(job);
		return null;
	}

	public long getJobId() {
		return jobId;
	}
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "jobId=" + jobId;
	}

}
