package net.conselldemallorca.helium.jbpm3.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.job.executor.LockMonitorThread;

public class SpringLockMonitorMassiuThread extends Thread {

	int lockMonitorInterval;
	int maxLockTime;
	int lockBufferTime;

	boolean isActive = true;

	public SpringLockMonitorMassiuThread(int lockMonitorInterval, int lockBufferTime) {
		this.lockMonitorInterval = lockMonitorInterval;
		this.lockBufferTime = lockBufferTime;
	}

	public void run() {
		try {
			while (isActive) {
				try {
					if (lockMonitorInterval > 0) {
						sleep(lockMonitorInterval);
					}
				} catch (InterruptedException e) {
					log.info("lock monitor thread '" + getName()
							+ "' got interrupted");
				} catch (Exception e) {
					log.error("exception in lock monitor thread. waiting "
							+ lockMonitorInterval + " milliseconds", e);
					try {
						sleep(lockMonitorInterval);
					} catch (InterruptedException e2) {
						log.debug("delay after exception got interrupted", e2);
					}
				}
			}
		} catch (Exception e) {
			log.error("exception in lock monitor thread", e);
		} finally {
			log.info(getName() + " leaves cyberspace");
		}
	}

	/**
	 * @deprecated As of jBPM 3.2.3, replaced by {@link #deactivate()}
	 */
	public void setActive(boolean isActive) {
		if (isActive == false)
			deactivate();
	}

	/**
	 * Indicates that this thread should stop running. Execution will cease
	 * shortly afterwards.
	 */
	public void deactivate() {
		if (isActive) {
			isActive = false;
			interrupt();
		}
	}

	private static Log log = LogFactory.getLog(LockMonitorThread.class);
}
