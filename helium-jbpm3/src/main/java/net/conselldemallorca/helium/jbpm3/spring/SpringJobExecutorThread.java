package net.conselldemallorca.helium.jbpm3.spring;

import java.util.Collection;
import java.util.Date;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.job.Job;
import org.jbpm.job.executor.JobExecutor;
import org.jbpm.job.executor.JobExecutorThread;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Extension of the default jBPM Job executor thread.
 * 
 * The only difference is that operations which need to use the data store, are now wrapped
 * in a Spring transaction by using a transaction template.
 * 
 * @author Joram Barrez
 */
public class SpringJobExecutorThread extends JobExecutorThread {

	private TransactionTemplate transactionTemplate;

	public SpringJobExecutorThread(String name,
                            JobExecutor jobExecutor,
                            JbpmConfiguration jbpmConfiguration,
                            TransactionTemplate transactionTemplate,
                            int idleInterval,
                            int maxIdleInterval,
                            long maxLockTime,
                            int maxHistory) {
		super(name, jobExecutor, jbpmConfiguration, idleInterval, maxIdleInterval, maxLockTime, maxHistory);
		this.transactionTemplate = transactionTemplate;
	}

	/* WRAPPED OPERATIONS */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Collection acquireJobs() {
		return (Collection) transactionTemplate.execute(new TransactionCallback() {
			
			public Object doInTransaction(TransactionStatus transactionStatus) {
				return SpringJobExecutorThread.super.acquireJobs();
			}
			
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void executeJob(final Job job) {
		transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus transactionStatus) {
				try {
					SpringJobExecutorThread.super.executeJob(job);
					try {
						String processInstanceId = new Long(job.getProcessInstance().getId()).toString();
						Jbpm3HeliumBridge.getInstance().expedientLuceneIndexUpdate(processInstanceId);
					} catch (Exception ex) {
						log.error("Error al indexar l'expedient (processInstanceId=" + job.getProcessInstance().getId() + ")", ex);
					}
				} catch (Exception ex) {
					log.error("Error al executar el job (" + job.toStringLongFormat() + ")", ex);
				}
				return null;
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Date getNextDueDate() {
		return (Date) transactionTemplate.execute(new TransactionCallback() {
			
			public Object doInTransaction(TransactionStatus transactionStatus) {
				return SpringJobExecutorThread.super.getNextDueDate();
			}
			
		});
	}

	private static Log log = LogFactory.getLog(SpringJobExecutorThread.class);

}
