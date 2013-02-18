package net.conselldemallorca.helium.jbpm3.spring;

import org.jbpm.JbpmConfiguration;
import org.jbpm.job.executor.LockMonitorThread;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Extension of the default jBPM Job executor thread, analogous to the
 * {@link SpringJobExecutorThread}.
 * 
 * @author Joram Barrez
 */
public class SpringLockMonitorThread extends LockMonitorThread {

	private TransactionTemplate transactionTemplate;

	public SpringLockMonitorThread(JbpmConfiguration jbpmConfiguration,
			int lockMonitorInterval, int maxLockTime, int lockBufferTime, TransactionTemplate transactionTemplate) {
		super(jbpmConfiguration, lockMonitorInterval, maxLockTime,lockBufferTime);
		this.transactionTemplate = transactionTemplate;
	}

	/** Needs to be wrapped in a transaction */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void unlockOverdueJobs() {
		transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus transactionStatus) {
				SpringLockMonitorThread.super.unlockOverdueJobs();
				return null;
			}
		});
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

}
