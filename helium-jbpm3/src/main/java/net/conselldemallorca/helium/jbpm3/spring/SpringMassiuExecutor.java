package net.conselldemallorca.helium.jbpm3.spring;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SpringMassiuExecutor implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory	.getLog(SpringMassiuExecutor.class);

	protected String name;
	protected int nbrOfThreads;
	protected int idleInterval;
	protected int maxIdleInterval;
	protected int historyMaxSize;
	protected int lockMonitorInterval;
	protected int lockBufferTime;

	protected Map<String, Thread> threads = new HashMap<String, Thread>();
	protected SpringLockMonitorMassiuThread lockMonitorThread;

	protected boolean isStarted = false;

	protected static String hostName;

	public synchronized void start() {
		if (!isStarted) {
			
			for (int i = 0; i < nbrOfThreads; i++) {
				startThread();
			}

			lockMonitorThread = new SpringLockMonitorMassiuThread(lockMonitorInterval, lockBufferTime);
			isStarted = true;
		}
	}

	public synchronized List<Thread> stop() {
		List<Thread> stoppedThreads = new ArrayList<Thread>(threads.size());
		if (isStarted) {
			log.debug("Aturant Grup de Threads: '" + name + "'...");
			for (int i = 0; i < nbrOfThreads; i++) {
				stoppedThreads.add(stopThread());
			}
			lockMonitorThread.deactivate();
			isStarted = false;
		} else {
			log.debug("ignoring stop: thread group '" + name + "' not started");
		}
		return stoppedThreads;
	}

	public void stopAndJoin() throws InterruptedException {
		Iterator<Thread> iter = stop().iterator();
		while (iter.hasNext()) {
			Thread thread = iter.next();
			thread.join();
		}
		lockMonitorThread.join();
	}

	protected synchronized void startThread() {
		String threadName = getNextThreadName();
		Thread thread = createThread(threadName);
		threads.put(threadName, thread);
		log.debug("Iniciant un nou Thread de accions massives: '" + threadName + "'");
		thread.start();
	}

	protected Thread createThread(String threadName) {
		log.info("Creant Thread d'execució massiva " + threadName);
		// Per defecte, si no hi ha execucions massives programades esperarem 5s per a mirar si n'hi ha de noves
		int waitTime = 5000;
		// Per defecte, entre una execució massiva i una altra esperarem 100ms
		int timeBetweenExecutions = 100;
		try { 
			waitTime = Integer.parseInt(
					Jbpm3HeliumBridge.getInstanceService().getHeliumProperty("app.massiu.periode.noves")); 
		} catch (Exception ex) {}
		try {
			timeBetweenExecutions = Integer.parseInt(
					Jbpm3HeliumBridge.getInstanceService().getHeliumProperty("app.massiu.periode.execucions")); 
		} catch (Exception ex) {}
		return new SpringMassiuExecutorThread(
					threadName, 
					this, 
					idleInterval, 
					maxIdleInterval,
					waitTime,
					timeBetweenExecutions);
	}

	protected String getNextThreadName() {
		return getThreadName(threads.size() + 1);
	}

	protected String getLastThreadName() {
		return getThreadName(threads.size());
	}

	private String getThreadName(int index) {
		return name + ":" + getHostName() + ":" + index;
	}

	private static String getHostName() {
		if (hostName == null) {
			try {
				hostName = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				hostName = "127.0.0.1";
			}
		}
		return hostName;
	}

	protected synchronized Thread stopThread() {
		String threadName = getLastThreadName();
		SpringMassiuExecutorThread thread = (SpringMassiuExecutorThread) threads.remove(threadName);
		log.debug("removing thread d'execució masiva '" + threadName + "'");
		thread.deactivate();
		return thread;
	}

	public void setHistoryMaxSize(int historyMaxSize) {
		this.historyMaxSize = historyMaxSize;
	}

	public int getHistoryMaxSize() {
		return historyMaxSize;
	}

	public void setIdleInterval(int idleInterval) {
		this.idleInterval = idleInterval;
	}

	public int getIdleInterval() {
		return idleInterval;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setMaxIdleInterval(int maxIdleInterval) {
		this.maxIdleInterval = maxIdleInterval;
	}

	public int getMaxIdleInterval() {
		return maxIdleInterval;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setSize(int nbrOfThreads) {
		this.nbrOfThreads = nbrOfThreads;
	}

	public int getSize() {
		return nbrOfThreads;
	}

	public void setThreads(Map<String, Thread> threads) {
		this.threads = threads;
	}

	public Map<String, Thread> getThreads() {
		return threads;
	}

	public void setLockBufferTime(int lockBufferTime) {
		this.lockBufferTime = lockBufferTime;
	}

	public int getLockBufferTime() {
		return lockBufferTime;
	}

	public void setLockMonitorInterval(int lockMonitorInterval) {
		this.lockMonitorInterval = lockMonitorInterval;
	}

	public int getLockMonitorInterval() {
		return lockMonitorInterval;
	}

	public void setNbrOfThreads(int nbrOfThreads) {
		this.nbrOfThreads = nbrOfThreads;
	}

	public int getNbrOfThreads() {
		return nbrOfThreads;
	}

}
