package net.conselldemallorca.helium.webapp.v3.helper;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

/**
 * Monitor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("restriction")
public class MonitorHelper {
	private static Boolean actiu = null;
	private static long prevUpTime, prevProcessCpuTime;

	private static RuntimeMXBean rmBean;

	
	
	private static com.sun.management.OperatingSystemMXBean sunOSMBean;
	public static com.sun.management.OperatingSystemMXBean getSunOSMBean() {
		return sunOSMBean;
	}
	
	public static String getArch() {
		String resultat;
		try {
			resultat = sunOSMBean.getArch();
		} catch (Exception e) {
			resultat = "No disponible";
		}
		return resultat;	
	}
	
	public static String getName() {
		String resultat;
		try {
			resultat = sunOSMBean.getName();
		} catch (Exception e) {
			resultat = "No disponible";
		}
		return resultat;	
	}
	
	public static String getVersion() {
		String resultat;
		try {
			resultat = sunOSMBean.getVersion();
		} catch (Exception e) {
			resultat = "No disponible";
		}
		return resultat;
	}

	private static Result result;

	public static Boolean getActiu() {
		return actiu;
	}

	private static class Result {
		long upTime = -1L;
		long processCpuTime = -1L;
		float cpuUsage = 0;
		int nCPUs;
	}

	static {
		try {
			rmBean = ManagementFactory.getRuntimeMXBean();
			// reperisco l'MBean relativo al sunOS
			sunOSMBean = ManagementFactory.newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(), ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, com.sun.management.OperatingSystemMXBean.class);

			result = new Result();
			result.nCPUs = sunOSMBean.getAvailableProcessors();
			result.upTime = rmBean.getUptime();
			result.processCpuTime = 0;
			if (sunOSMBean != null) {
				result.processCpuTime = sunOSMBean.getProcessCpuTime();
			}
		} catch (Exception e) {
			System.err.println(MonitorHelper.class.getSimpleName() + " exception: " + e.getMessage());
		}
	}

	private static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

	public static long[] getThreadsIds() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.getAllThreadIds();
	}

	public MonitorHelper(String sactiu) {
		super();
		if (!"true".equalsIgnoreCase(sactiu))
			actiu = false;
		else
			actiu = true;
	}

	public static String humanReadableByteCount(long bytes) {
		boolean si = true;
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static String getCPULoad() {
		String resultat;
		try {
			result.upTime = rmBean.getUptime();
			result.processCpuTime = sunOSMBean.getProcessCpuTime();

			if (result.upTime > 0L && result.processCpuTime >= 0L)
				updateCPUInfo();
			resultat = result.cpuUsage + "%";
		} catch (Exception e) {
			resultat = "No disponible";
		}
		return resultat;
	}

	public static void updateCPUInfo() {
		if (prevUpTime > 0L && result.upTime > prevUpTime) {
			long elapsedCpu = result.processCpuTime - prevProcessCpuTime;
			long elapsedTime = result.upTime - prevUpTime;
			result.cpuUsage = Math.round(Math.min(100F, elapsedCpu / (elapsedTime * 10000F * result.nCPUs)));
		}

		prevUpTime = result.upTime;
		prevProcessCpuTime = result.processCpuTime;
	}

	/** Get CPU time in nanoseconds. */
	public static long getCpuTime() {
		if (!bean.isThreadCpuTimeSupported())
			return 0L;
		long time = 0L;
		for (long i : getThreadsIds()) {
			long t = bean.getThreadCpuTime(i);
			if (t != -1)
				time += t;
		}
		return time;
	}

	public static long getCpuTimePercent() {
		if (!bean.isThreadCpuTimeSupported())
			return 0L;
		long time = 0L;
		for (long i : getThreadsIds()) {
			long t = bean.getThreadCpuTime(i);
			if (t != -1)
				time += t;
		}
		return time;
	}

	/** Get user time in nanoseconds. */
	public static long getUserTime() {
		if (!bean.isThreadCpuTimeSupported())
			return 0L;
		long time = 0L;
		for (long i : getThreadsIds()) {
			long t = bean.getThreadUserTime(i);
			if (t != -1)
				time += t;
		}
		return time;
	}

	/** Get system time in nanoseconds. */
	public static long getSystemTime() {
		if (!bean.isThreadCpuTimeSupported())
			return 0L;
		long time = 0L;
		for (long i : getThreadsIds()) {
			long tc = bean.getThreadCpuTime(i);
			long tu = bean.getThreadUserTime(i);
			if (tc != -1 && tu != -1)
				time += (tc - tu);
		}
		return time;
	}
}
