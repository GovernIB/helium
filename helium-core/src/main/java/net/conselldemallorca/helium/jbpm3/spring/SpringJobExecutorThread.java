package net.conselldemallorca.helium.jbpm3.spring;

import java.util.Collection;
import java.util.Date;

import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.TascaService;

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
	
	private static final Log logger = LogFactory.getLog(TascaService.class);
	
	private ExpedientService expedientService;
	
	private TransactionTemplate transactionTemplate;
	
	public SpringJobExecutorThread(String name,
                            JobExecutor jobExecutor,
                            JbpmConfiguration jbpmConfiguration,
                            TransactionTemplate transactionTemplate,
                            int idleInterval,
                            int maxIdleInterval,
                            long maxLockTime,
                            int maxHistory,
                            ExpedientService expedientService
                          ) {
		super(name, jobExecutor, jbpmConfiguration, idleInterval, maxIdleInterval, maxLockTime, maxHistory);
		this.transactionTemplate = transactionTemplate;
		this.expedientService = expedientService;
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
				SpringJobExecutorThread.super.executeJob(job);
				return null;
			}
			
		});
		try{
			expedientService.luceneUpdateIndexExpedient(String.valueOf(job.getProcessInstance().getId()));
		} catch (Exception e) {
			logger.error("TIMER-LUCENE: Error al indexar l'expedient ProcessInstance num. " + job.getProcessInstance().getId());
//			ProcessInstance pi = job.getProcessInstance();
//			ExpedientDto exp = expedientService.findExpedientAmbProcessInstanceId(String.valueOf(pi.getId()));
//			System.out.println("TIMER-LUCENE: Error al indexar l'expedient:");
//			System.out.println("\t - ProcessInstance num. " + pi.getId());
//			System.out.println("\t - Número d'expedient " + exp.getNumero());
//			System.out.println("\t - Títol d'expedient " + exp.getTitol());
//			System.out.println("\t - Tipus d'expedient " + exp.getTipus().getNom());
//			StringWriter out = new StringWriter();
//			e.printStackTrace(new PrintWriter(out));
//			System.out.println(out.toString());
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Date getNextDueDate() {
		return (Date) transactionTemplate.execute(new TransactionCallback() {
			
			public Object doInTransaction(TransactionStatus transactionStatus) {
				return SpringJobExecutorThread.super.getNextDueDate();
			}
			
		});
	}
}
