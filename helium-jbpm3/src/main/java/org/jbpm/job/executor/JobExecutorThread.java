package org.jbpm.job.executor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.StaleStateException;
import org.hibernate.exception.LockAcquisitionException;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.JobSession;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.job.Job;
import org.jbpm.job.Timer;
import org.jbpm.persistence.JbpmPersistenceException;
import org.jbpm.persistence.db.StaleObjectLogConfigurer;
import org.jbpm.svc.Services;
import org.jbpm.taskmgmt.exe.TaskInstance;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

public class JobExecutorThread extends Thread {

	private static Log log = LogFactory.getLog(JobExecutorThread.class);
	
	public JobExecutorThread( String name,
			JobExecutor jobExecutor,
			JbpmConfiguration jbpmConfiguration,
			int idleInterval,
			int maxIdleInterval,
			long maxLockTime,
			int maxHistory
			) {
		super(name);
		this.jobExecutor = jobExecutor;
		this.jbpmConfiguration = jbpmConfiguration;
		this.idleInterval = idleInterval;
		this.maxIdleInterval = maxIdleInterval;
		this.maxLockTime = maxLockTime;
	}

	final JobExecutor jobExecutor; 
	final JbpmConfiguration jbpmConfiguration;
	
	final int idleInterval;
	final int maxIdleInterval;
	final long maxLockTime;

	int currentIdleInterval;
	volatile boolean isActive = true;
	
	protected static Map<Long, String[]> jobErrors = new HashMap<Long, String[]>();
	protected static Map<Long, String> jobUser = new HashMap<Long, String>();

	@SuppressWarnings("rawtypes")
	public void run() {
		currentIdleInterval = idleInterval;
		
		while (Jbpm3HeliumBridge.getInstanceService() == null){
			try {
				log.info("Job executor: esperant a obtenir el InstanceService");
				Thread.sleep(500);
			} catch (Exception ex) {}
		}
		
		// Inicialitzam les definicions de procés per a poder tenir accés als seus handlers
		Jbpm3HeliumBridge.getInstanceService().initializeDefinicionsProces();
				
		while (isActive) {
			try {
				Collection acquiredJobs = acquireJobs();
				if (! acquiredJobs.isEmpty()) {
					Iterator iter = acquiredJobs.iterator();
					while (iter.hasNext() && isActive) {
						Job job = (Job) iter.next();
						boolean error = false;
						Throwable terror = null;
						try {
							executeJob(job);
						}
						// on exception, call returns normally
						catch (Exception e) {
							error = true;
							terror = e;
						} catch (Error e) {
							error = true;
							terror = e;
						}
						if (error) {
							saveJobException(job, terror);
							try{ 
								executeJob(job); 
							}catch(Exception ex){
								log.error("error no controlat en el job executor thread.", ex);
							}
							break;
						}
					}
				} else { // no jobs acquired
					if (isActive) {
						long waitPeriod = getWaitPeriod();
						if (waitPeriod>0) {
							synchronized(jobExecutor) {
								jobExecutor.wait(waitPeriod);
							}
						}
					}
				}

				// no exception so resetting the currentIdleInterval
				currentIdleInterval = idleInterval;

			} catch (InterruptedException e) {
				log.info((isActive? "active" : "inactive")+" job executor thread '"+getName()+"' got interrupted");
			} catch (Exception e) {
				log.error("exception in job executor thread. waiting "+currentIdleInterval+" milliseconds", e);
				try {
					synchronized(jobExecutor) {
						jobExecutor.wait(currentIdleInterval);
					}
				} catch (InterruptedException e2) {
					log.debug("delay after exception got interrupted", e2);
				}
				// after an exception, the current idle interval is doubled to prevent 
				// continuous exception generation when e.g. the db is unreachable
				currentIdleInterval <<= 1;
				if (currentIdleInterval > maxIdleInterval || currentIdleInterval < 0) {
					currentIdleInterval = maxIdleInterval;
				}
			}
		}
		log.info(getName()+" leaves cyberspace");
	}
	
	@SuppressWarnings("rawtypes")
	protected Collection acquireJobs() {
		Collection acquiredJobs;
		synchronized (jobExecutor) {
			log.debug("acquiring jobs for execution...");
			List jobsToLock = Collections.EMPTY_LIST;
			List<Job> jobs = new ArrayList<Job>();
			JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
			try {
				JobSession jobSession = jbpmContext.getJobSession();
				String lockOwner = getName();
				log.debug("querying for acquirable job...");
				Job job = jobSession.getFirstAcquirableJob(lockOwner);
				if (job!=null) {
					Hibernate.initialize(job.getProcessInstance());
					if (job.isExclusive()) {
						log.debug("found exclusive " + job);
						ProcessInstance processInstance = job.getProcessInstance();
						log.debug("finding other exclusive jobs for " + processInstance);
						jobsToLock = jobSession.findExclusiveJobs(lockOwner, processInstance);
						log.debug("trying to obtain exclusive locks on " + jobsToLock + " for " + processInstance);
					} else {
						log.debug("trying to obtain lock on " + job);
						jobsToLock = Collections.singletonList(job);
					}

					Date lockTime = new Date();
					for (Iterator iter = jobsToLock.iterator(); iter.hasNext();) {
						boolean deleted = false;
						job = (Job) iter.next();
						if (jobErrors.get(job.getId()) != null) {
							int retries = job.getRetries() - 1;
							if (retries <= 0) {
								jobSession.deleteJob(job);
								log.error("Job (" + job.getId() + ") eliminat.");
								deleted = true;
							} else {
								job.setRetries(job.getRetries() - 1);
								jobSession.saveJob(job);
							}
							jobErrors.remove(job.getId());
						}
						if (!deleted) {
							job.setLockOwner(lockOwner);
							job.setLockTime(lockTime);
							Hibernate.initialize(job.getProcessInstance().getExpedient().getEntorn());
							Hibernate.initialize(job.getProcessInstance().getExpedient().getTipus());
							jobs.add(job);
							
							if (job instanceof Timer) {
								Timer timer = (Timer)job;
								
								if (job.getTaskInstance() == null) {
									Long tiId = 0L;
									String user = null;
									if (timer.getGraphElement() != null && timer.getGraphElement() instanceof TaskNode) {
										TaskNode tn = (TaskNode)timer.getGraphElement();
										for (TaskInstance ti: job.getProcessInstance().getTaskMgmtInstance().getTaskInstances()) {
											if (ti.getToken().equals(job.getToken()) && ti.getTask() != null && ti.getTask().getTaskNode() != null && ti.getTask().getTaskNode().equals(tn)) {
												if (ti.getId() > tiId) {
													if (ti.getActorId() != null) {
														tiId = ti.getId();
														user = ti.getActorId();
													} else if (ti.getPooledActors() != null && !ti.getPooledActors().isEmpty()) {
														tiId = ti.getId();
														user = ti.getPooledActors().iterator().next().getActorId();
													}
												}
												jobUser.put(job.getId(), user);
											}
										}
									}
								}
							}
						}
					}
				} else {
					log.debug("no acquirable jobs in job table");
				}
			} finally {
				try {
					jbpmContext.close();
//					acquiredJobs = jobsToLock;
					acquiredJobs = jobs;
					log.debug("obtained lock on jobs: "+acquiredJobs);
				}
				catch (JbpmPersistenceException e) {
					// if this is a stale object exception, keep it quiet
					if (Services.isCausedByStaleState(e)) {
						log.debug("optimistic locking failed, couldn't obtain lock on jobs "+jobsToLock);
						acquiredJobs = Collections.EMPTY_LIST;
					} else {
						throw e;
					}
				}
			}
		}
		return acquiredJobs;
	}

	protected void executeJob(Job job) throws Exception {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			JobSession jobSession = jbpmContext.getJobSession();
			job = jobSession.loadJob(job.getId());
			
			try {
				if (jobErrors.get(job.getId()) != null) {
					job.setRetries(job.getRetries() - 1);
					jobErrors.remove(job.getId());
				}
				if (job.getRetries() <= 0) {
					jobSession.deleteJob(job);
					log.error("Job (" + job.getId() + ") eliminat.");
					return;
				}
			} catch (EntityNotFoundException enf) {
				return;
			}
			try {
				log.debug("executing " + job);
				if (job.execute(jbpmContext)) {
					jobSession.deleteJob(job);
				}
			} catch (Exception e) {
				if (isPersistenceException(e) || !isLockingException(e)) {
					throw e;
				} else {
					// prevent unsafe use of the session after an exception occurs
//					jbpmContext.setRollbackOnly();
					log.debug("exception while executing " + job, e);
					StaleObjectLogConfigurer.getStaleObjectExceptionsLog().error("failed to complete " + job);
				}
			} catch (Error e) {
//				jbpmContext.setRollbackOnly();
				throw e;
			}

			// if this job is locked too long
			long totalLockTimeInMillis = System.currentTimeMillis() - job.getLockTime().getTime(); 
			if (totalLockTimeInMillis>maxLockTime) {
				jbpmContext.setRollbackOnly();
			}
		} finally {
			try {
				jbpmContext.close();
			} catch (JbpmPersistenceException e) {
				// if this is a stale state exception, keep it quiet
				if (Services.isCausedByStaleState(e)) {
					log.debug("optimistic locking failed, couldn't complete job "+job);
				} else {
					throw e;
				}
			}
		}
	}
	
	protected void decrementJobRetries(Job job) {
		log.error("Job (" + job.getId() + ") >> decrementant reintents després d'un error.");
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		JobSession jobSession = jbpmContext.getJobSession();
		int retries = job.getRetries() - 1;
		if (retries <= 0) {
			jobSession.deleteJob(job);
			log.error("Job (" + job.getId() + ") eliminat.");
		} else { 
			job.setRetries(retries);
			jobSession.saveJob(job);
			log.error("Job (" + job.getId() + ") decrementat.");
		}
	}
//	private boolean jobHasBeenExecutedWithError(Job job) {
//		if (SpringJobExecutorThread.jobErrors.containsKey(job.getId())) 
//			return true;
//		return job.getException() != null;
//	}
//	
//	private String getJobException(Job job) {
//		if (SpringJobExecutorThread.jobErrors.containsKey(job.getId())) {
//			String excepcio = SpringJobExecutorThread.jobErrors.get(job.getId())[1];
//			SpringJobExecutorThread.jobErrors.remove(job.getId());
//			return excepcio;
//		}
//		return job.getException();
//	}
	
	protected void saveJobException(Job job, Throwable exception) {
		StringWriter out = new StringWriter();
		logger.error(new PrintWriter(out));
		job.setException(out.toString());
	}

	private static boolean isPersistenceException(Throwable throwable) {
		do {
			if (throwable instanceof HibernateException)
				return true;
			throwable = throwable.getCause();
		} while (throwable != null);
		return false;
	}
	
	private static boolean isLockingException(Throwable throwable) {
		for (Throwable t = throwable; t != null; t = t.getCause()) {
			if (t instanceof StaleStateException || t instanceof LockAcquisitionException) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	protected Date getNextDueDate() {
		Date nextDueDate = null;
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			JobSession jobSession = jbpmContext.getJobSession();
			Collection jobIdsToIgnore = jobExecutor.getMonitoredJobIds();
			Job job = jobSession.getFirstDueJob(getName(), jobIdsToIgnore);
			if (job!=null) {
				nextDueDate = job.getDueDate();
				jobExecutor.addMonitoredJobId(getName(), job.getId());
			}
		} finally {
			try {
				jbpmContext.close();
			} catch (JbpmPersistenceException e) {
				// if this is a stale object exception, keep it quiet
				if (Services.isCausedByStaleState(e)) {
					log.debug("optimistic locking failed, couldn't get next due date");
					nextDueDate = null;
				} else {
					throw e;
				}
			}
		}
		return nextDueDate;
	}

	protected long getWaitPeriod() {
		long interval = currentIdleInterval;
		Date nextDueDate = getNextDueDate();
		if (nextDueDate!=null) {
			long currentTime = System.currentTimeMillis();
			long nextDueTime = nextDueDate.getTime();
			if (nextDueTime < currentTime+currentIdleInterval) {
				interval = nextDueTime-currentTime;
			}
		}
		if (interval<0) {
			interval = 0;
		}
		return interval;
	}

	/**
	 * @deprecated As of jBPM 3.2.3, replaced by {@link #deactivate()}
	 */
	public void setActive(boolean isActive) {
		if (isActive == false) 
			deactivate();
	}

	/**
	 * Indicates that this thread should stop running.
	 * Execution will cease shortly afterwards.
	 */
	public void deactivate() {
		if (isActive) {
			isActive = false;
			interrupt();      
		}
	}
	
	private static final Log logger = LogFactory.getLog(JobExecutorThread.class);
}

