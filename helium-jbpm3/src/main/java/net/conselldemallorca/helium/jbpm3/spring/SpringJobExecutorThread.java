package net.conselldemallorca.helium.jbpm3.spring;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.job.ExecuteActionJob;
import org.jbpm.job.ExecuteNodeJob;
import org.jbpm.job.Job;
import org.jbpm.job.Timer;
import org.jbpm.job.executor.JobExecutor;
import org.jbpm.job.executor.JobExecutorThread;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Extension of the default jBPM Job executor thread.
 * 
 * The only difference is that operations which need to use the data store, are now wrapped in a Spring transaction by using a transaction template.
 * 
 * @author Joram Barrez
 */
public class SpringJobExecutorThread extends JobExecutorThread {

	private static final Log logger = LogFactory.getLog(SpringJobExecutorThread.class);

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
				while (Jbpm3HeliumBridge.getInstanceService() == null){
//					System.out.println(">>>>>Jbpm3HeliumBridge.getInstanceService() == null. Retry (" + System.currentTimeMillis() + ")");
						try{
							Thread.sleep(500);
						} catch (Exception ex) {}
				}
				return SpringJobExecutorThread.super.acquireJobs();
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void executeJob(final Job job) {
		boolean ambErrors = false;

		String jobName = "JOB";
		if (job instanceof Timer) {
			if (((Timer) job).getName() != null)
				jobName += " " + ((Timer) job).getName();
		} else if (job instanceof ExecuteActionJob) {
			if (((ExecuteActionJob) job).getAction() != null && ((ExecuteActionJob) job).getAction().getName() != null)
				jobName += " " + ((ExecuteActionJob) job).getAction().getName();
		} else if (job instanceof ExecuteNodeJob) {
			if (((ExecuteNodeJob) job).getNode() != null && ((ExecuteNodeJob) job).getNode().getName() != null)
				jobName += " " + ((ExecuteNodeJob) job).getNode().getName();
		}
		
//		((MesuresTemporalsHelper)adminService.getMesuresTemporalsHelper()).mesuraIniciar(jobName, "timer");
		try {
			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus transactionStatus) {
					try {
						SpringJobExecutorThread.super.executeJob(job);
						try {
							String processInstanceId = new Long(job.getProcessInstance().getId()).toString();
							Jbpm3HeliumBridge.getInstanceService().expedientReindexar(processInstanceId);
						} catch (Exception ex) {
							logger.error("Error al indexar l'expedient (processInstanceId=" + job.getProcessInstance().getId() + ")", ex);
						}
					} catch (Exception ex) {
						logger.error("Error al executar el job (" + job.toStringLongFormat() + ")", ex);
					}
					return null;
				}
			});
		} catch (JbpmException ex) {
			ambErrors = true;
			ProcessInstance pi = job.getProcessInstance();
			ExpedientDto exp = Jbpm3HeliumBridge.getInstanceService().getExpedientArrelAmbProcessInstanceId(
					String.valueOf(pi.getId()));
			String errorDesc = "Se ha producido un error al ejecutar el timer " + jobName + ".";			
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			String errorFull = errors.toString();			
			errorFull = errorFull.replace("'", "&#8217;").replace("\"", "&#8220;").replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
			errorFull = StringEscapeUtils.escapeJavaScript(errorFull);
			expedientService.updateExpedientError(String.valueOf(pi.getId()), errorDesc, errorFull);
			logger.error("Error al executar job de l'expedient (id=" + exp.getId() + ", identificador=" + exp.getIdentificadorLimitat() + ", processInstanceId=" + job.getProcessInstance().getId() + ")", ex);
			throw ex;
		}
		if (!ambErrors) {
			try {
				Jbpm3HeliumBridge.getInstanceService().expedientReindexar(
						String.valueOf(job.getProcessInstance().getId()));
			} catch (Exception ex) {
				ProcessInstance pi = job.getProcessInstance();
				ExpedientDto exp = Jbpm3HeliumBridge.getInstanceService().getExpedientArrelAmbProcessInstanceId(
						String.valueOf(pi.getId()));
				logger.error("Error al executar job al reindexar l'expedient (id=" + exp.getId() + ", identificador=" + exp.getIdentificadorLimitat() + ", processInstanceId=" + job.getProcessInstance().getId() + ")", ex);
			}
		}
//		((MesuresTemporalsHelper)adminService.getMesuresTemporalsHelper()).mesuraCalcular(jobName, "timer");
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
