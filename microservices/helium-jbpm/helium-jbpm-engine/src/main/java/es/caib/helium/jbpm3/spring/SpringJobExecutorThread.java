package es.caib.helium.jbpm3.spring;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.jbpm.job.ExecuteActionJob;
import org.jbpm.job.ExecuteNodeJob;
import org.jbpm.job.Job;
import org.jbpm.job.Timer;
import org.jbpm.job.executor.JobExecutor;
import org.jbpm.job.executor.JobExecutorThread;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

// TODO: Mirar Metrics filter plugin
// https://www.elastic.co/guide/en/logstash/current/plugins-filters-metrics.html

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
		final ProcessInstanceExpedient expedient = job.getProcessInstance().getExpedient();
		try {
			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus transactionStatus) {
					// TODO: Substutuir les mètriques
					Long metricId = Jbpm3HeliumBridge.getInstanceService().startMetric(
							JobExecutorThread.class,
							"job",
							expedient.getEntorn().getId(),
							expedient.getTipus().getId());
//					MetricRegistry metricRegistry = Jbpm3HeliumBridge.getInstanceService().getMetricRegistry();
//			        final com.codahale.metrics.Timer timerTotal = metricRegistry.timer(
//							MetricRegistry.name(
//									JobExecutorThread.class,
//									"job"));
//					final com.codahale.metrics.Timer.Context contextTotal = timerTotal.time();
//					Counter countTotal = metricRegistry.counter(
//							MetricRegistry.name(
//									JobExecutorThread.class,
//									"job.count"));
//					countTotal.inc();
//					final com.codahale.metrics.Timer timerEntorn = metricRegistry.timer(
//							MetricRegistry.name(
//									JobExecutorThread.class,
//									"job",
//									expedient.getEntorn().getCodi()));
//					final com.codahale.metrics.Timer.Context contextEntorn = timerEntorn.time();
//					Counter countEntorn = metricRegistry.counter(
//							MetricRegistry.name(
//									JobExecutorThread.class,
//									"job.count",
//									expedient.getEntorn().getCodi()));
//					countEntorn.inc();
//					final com.codahale.metrics.Timer timerTipexp = metricRegistry.timer(
//							MetricRegistry.name(
//									JobExecutorThread.class,
//									"job",
//									expedient.getEntorn().getCodi(),
//									expedient.getTipus().getCodi()));
//					final com.codahale.metrics.Timer.Context contextTipexp = timerTipexp.time();
//					Counter countTipexp = metricRegistry.counter(
//							MetricRegistry.name(
//									JobExecutorThread.class,
//									"job.count",
//									expedient.getEntorn().getCodi(),
//									expedient.getTipus().getCodi()));
//					countTipexp.inc();
					Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();
					try {
						final String user = jobUser.get(job.getId()); 
						if (user != null) {
					        Principal principal = new Principal() {
								public String getName() {
									return user;
								}
							};
							Authentication authentication =  new UsernamePasswordAuthenticationToken(principal, null);
							Object credentials = "N/A";
							List<String> rols = Jbpm3HeliumBridge.getInstanceService().getRolsByCodi(user);
							List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
							if (rols != null && !rols.isEmpty()) {
								for (String rol: rols) {
									authorities.add(new SimpleGrantedAuthority(rol));
								}
								authentication =  new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
							}
							SecurityContextHolder.getContext().setAuthentication(authentication);
						}
//						if (Jbpm3HeliumBridge.getInstanceService().mesuraIsActiu()) {
//							Jbpm3HeliumBridge.getInstanceService().mesuraIniciar(jName, "timer", expedient.getTipus().getNom(), null, null);
//						}
						
						SpringJobExecutorThread.super.executeJob(job);
						String processInstanceId = new Long(job.getProcessInstance().getId()).toString();
						Jbpm3HeliumBridge.getInstanceService().expedientReindexar(processInstanceId);
					} catch (Exception ex) {
						saveJobError(job.getId(), ex, "S'ha produït un error al executar el timer " + jName + ".");
						// Vaig a provocar la excepció des d'aquí, per a forçar el rollback...
						JbpmException e = new JbpmException(ex.getMessage(), ex.getCause());
						throw e;
					} finally {
						SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
//						contextTotal.stop();
//						contextEntorn.stop();
//						contextTipexp.stop();
						Jbpm3HeliumBridge.getInstanceService().endMetric(metricId);
					}
					return null;
				}
			});
		} catch (Exception ex) {
			String[] error = jobErrors.get(job.getId());
			String errorDesc = "";
			String errorFull = "";
			if (error == null || error.length < 2) {
				errorDesc = "S'ha produït un error al executar la transacció del timer " + jName + ".";
				errorFull = saveJobError(job.getId(), ex, errorDesc);
			} else {
				errorDesc = error[0];
				errorFull = error[1];
			}
			Jbpm3HeliumBridge.getInstanceService().updateExpedientError(
					job.getId(),
					expedient.getId(), 
					errorDesc, 
					errorFull);
			String msgError = 	"Error al executar la transaccio del job '" + jName + 
								"' con processInstanceId=" + job.getProcessInstance().getId() + 
								" de l'expedient (id=" + expedient.getId() + ", identificador=" + expedient.getIdentificador() + ")";
			logger.error(msgError, ex);
			JbpmException e = new JbpmException(ex.getMessage(), ex.getCause());
			throw e;
		}
//		Jbpm3HeliumBridge.getInstanceService().mesuraCalcular(jName, "timer", expedient.getTipus().getNom(), null, null);
	}

	public void decrementJobRetries(Job job) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Date getNextDueDate() {
		return (Date) transactionTemplate.execute(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus transactionStatus) {
				return SpringJobExecutorThread.super.getNextDueDate();
			}
		});
	}
	
	private String saveJobError(Long jobId, Throwable ex, String errorDesc) {
		StringWriter errors = new StringWriter();
		logger.error(new PrintWriter(errors));
		String errorFull = errors.toString();	
		errorFull = errorFull.replace("'", "&#8217;").replace("\"", "&#8220;").replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		errorFull = StringEscapeUtils.escapeJavaScript(errorFull);
		jobErrors.put(jobId, new String[] {errorDesc, errorFull});
		return errorFull;
	}
}
