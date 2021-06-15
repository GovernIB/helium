package net.conselldemallorca.helium.jbpm3.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.job.executor.JobExecutor;
import org.jbpm.job.executor.LockMonitorThread;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Extension of the default jBPM job executor, able to execute jobs in transactions defined by Spring.
 * 
 * @author Joram Barrez
 */
@SuppressWarnings("serial")
public class SpringJobExecutor extends JobExecutor {

	/** Logger for this class */
	private static final Log LOG = LogFactory.getLog(SpringJobExecutor.class);
	
	/** Used to wrap jBPM calls in an ongoing transaction */
	private TransactionTemplate transactionTemplate;

	/** 
	 * Need to override the start method of the {@link org.jbpm.job.executor.JobExecutor},
	 * since the {@link LockMonitorThread} is started there. 
	 * We want to wrap the calls of this thread in transaction too, hence the override.
	 * (better design of jBPM would be that there is a seperate method to start it ...)
	 */
	@Override
	public synchronized void start() {
		if (!isStarted) {

			for (int i = 0; i < nbrOfThreads; i++) {
				startThread();
			}

			lockMonitorThread = new SpringLockMonitorThread(jbpmConfiguration,
					lockMonitorInterval, maxLockTime, lockBufferTime,
					transactionTemplate);

			isStarted = true;
		}
	}
	
	protected Thread createThread(String threadName) {
		LOG.info("Creating JobExecutor thread " + threadName);
		return new SpringJobExecutorThread(
				threadName,
				this,
				jbpmConfiguration,
				transactionTemplate, 
				idleInterval,
				maxIdleInterval,
				maxLockTime,
				historyMaxSize);
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

}
