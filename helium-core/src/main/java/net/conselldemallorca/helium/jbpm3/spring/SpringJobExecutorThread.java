package net.conselldemallorca.helium.jbpm3.spring;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.service.AdminService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.TascaService;

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

	private static final Log logger = LogFactory.getLog(TascaService.class);

	private ExpedientService expedientService;
	private AdminService adminService;
	
	private TransactionTemplate transactionTemplate;
	
	public SpringJobExecutorThread(String name,
                            JobExecutor jobExecutor,
                            JbpmConfiguration jbpmConfiguration,
                            TransactionTemplate transactionTemplate,
                            int idleInterval,
                            int maxIdleInterval,
                            long maxLockTime,
                            int maxHistory,
                            ExpedientService expedientService,
                            AdminService adminService
                          ) {
		super(name, jobExecutor, jbpmConfiguration, idleInterval, maxIdleInterval, maxLockTime, maxHistory);
		this.transactionTemplate = transactionTemplate;
		this.expedientService = expedientService;
		this.adminService = adminService;
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
		
		((MesuresTemporalsHelper)adminService.getMesuresTemporalsHelper()).mesuraIniciar(jobName, "timer");
		try {
			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus transactionStatus) {
					SpringJobExecutorThread.super.executeJob(job);
					return null;
				}
			});
		} catch (JbpmException ex) {
			ambErrors = true;
			ProcessInstance pi = job.getProcessInstance();
			ExpedientDto exp = expedientService.findExpedientAmbProcessInstanceId(
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
				expedientService.luceneUpdateIndexExpedient(String.valueOf(job.getProcessInstance().getId()));
			} catch (Exception ex) {
				ProcessInstance pi = job.getProcessInstance();
				ExpedientDto exp = expedientService.findExpedientAmbProcessInstanceId(
						String.valueOf(pi.getId()));
				logger.error("Error al executar job al reindexar l'expedient (id=" + exp.getId() + ", identificador=" + exp.getIdentificadorLimitat() + ", processInstanceId=" + job.getProcessInstance().getId() + ")", ex);
			}
		}
		((MesuresTemporalsHelper)adminService.getMesuresTemporalsHelper()).mesuraCalcular(jobName);
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
