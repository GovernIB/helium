package net.conselldemallorca.helium.jbpm3.spring;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
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
	private TransactionTemplate transactionTemplate;
	public static Map<Long, String> jobErrors = new HashMap<Long, String>();
	private String expedientTipus = null;
	private ExpedientDto exp = null;
	
	public SpringJobExecutorThread(String name,
                            JobExecutor jobExecutor,
                            JbpmConfiguration jbpmConfiguration,
                            TransactionTemplate transactionTemplate,
                            int idleInterval,
                            int maxIdleInterval,
                            long maxLockTime,
                            int maxHistory
                          ) {
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
		final String jName = (String) transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus transactionStatus) {
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
				return jobName;
			}
		});
		try {
			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus transactionStatus) {
					try {
						if (Jbpm3HeliumBridge.getInstanceService().mesuraIsActiu()) {
							exp = Jbpm3HeliumBridge.getInstanceService().getExpedientArrelAmbProcessInstanceId(String.valueOf(job.getProcessInstance().getId()));
							if (exp != null)
								expedientTipus = exp.getTipus().getNom();
							Jbpm3HeliumBridge.getInstanceService().mesuraIniciar(jName, "timer", expedientTipus, null, null);
						}
						SpringJobExecutorThread.super.executeJob(job);
						try {
							String processInstanceId = new Long(job.getProcessInstance().getId()).toString();
							Jbpm3HeliumBridge.getInstanceService().expedientReindexar(processInstanceId);
						} catch (Exception ex) {
							logger.error("Error al indexar l'expedient (processInstanceId=" + job.getProcessInstance().getId() + ")", ex);
						}
					} catch (Exception ex) {
						String errorDesc = "Se ha producido un error al ejecutar el timer " + jName + ".";			
						StringWriter errors = new StringWriter();
						ex.printStackTrace(new PrintWriter(errors));
						String errorFull = errors.toString();	
						jobErrors.put(job.getId(), errorFull);
						errorFull = errorFull.replace("'", "&#8217;").replace("\"", "&#8220;").replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
						errorFull = StringEscapeUtils.escapeJavaScript(errorFull);
						// He configurat la transacció de updateExpedientError per a que es desi tot i fer rollback...		
						Jbpm3HeliumBridge.getInstanceService().updateExpedientError(
								job.getId(), 
								String.valueOf(job.getProcessInstance().getId()), 
								errorDesc, 
								errorFull);
						logger.error("Error al executar el job " + jName + " de l'expedient (id=" + exp.getId() + ", identificador=" + exp.getIdentificador() + ", processInstanceId=" + job.getProcessInstance().getId() + ")", ex);
						
						// Vaig a provocar la excepció des d'aquí, per a forçar el rollback...
						JbpmException e = new JbpmException(ex.getMessage(), ex.getCause());
						throw e;
					}
					return null;
				}
			});
		} catch (JbpmException ex) {
			String errorDesc = "Se ha producido un error al ejecutar la transacción del timer " + jName + ".";			
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			String errorFull = jobErrors.get(job.getId());
			if (errorFull == null)
				errorFull = errors.toString();			
			errorFull = errorFull.replace("'", "&#8217;").replace("\"", "&#8220;").replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
			errorFull = StringEscapeUtils.escapeJavaScript(errorFull);
			Jbpm3HeliumBridge.getInstanceService().updateExpedientError(
					job.getId(),
					String.valueOf(job.getProcessInstance().getId()), 
					errorDesc, 
					errorFull);
			String error = "Error al executar la transaccio del job '" + jName + "' con processInstanceId=" + job.getProcessInstance().getId();
			if (exp != null) {
				error += " de l'expedient (id=" + exp.getId() + ", identificador=" + exp.getIdentificador() + ")";
			}
			logger.error(error, ex);
			throw ex;
		}
		Jbpm3HeliumBridge.getInstanceService().mesuraCalcular(jName, "timer", expedientTipus, null, null);
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
