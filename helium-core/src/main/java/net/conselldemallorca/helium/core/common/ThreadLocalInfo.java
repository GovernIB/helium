/**
 * 
 */
package net.conselldemallorca.helium.core.common;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;

/**
 * Emmagatzema informació diverse a dins el thread local.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ThreadLocalInfo {

	private static ThreadLocal<Expedient> expedientThreadLocal = new ThreadLocal<Expedient>();
	private static ThreadLocal<Long> entornIdThreadLocal = new ThreadLocal<Long>();
	private static ThreadLocal<List<String>> processInstanceFinalitzatIdsThreadLocal = new ThreadLocal<List<String>>();

	public static void setExpedient(Expedient expedient) {
		expedientThreadLocal.set(expedient);
	}
	public static Expedient getExpedient() {
		return expedientThreadLocal.get();
	}
	public static void setEntornId(Long entornId) {
		entornIdThreadLocal.set(entornId);
	}
	public static Long getEntornId() {
		return entornIdThreadLocal.get();
	}
	public static void addProcessInstanceFinalitzatIds(String processInstanceId) {
		List<String> ids = processInstanceFinalitzatIdsThreadLocal.get();
		if (ids == null) {
			ids = new ArrayList<String>();
			processInstanceFinalitzatIdsThreadLocal.set(ids);
		}
		ids.add(processInstanceId);
	}
	public static List<String> getProcessInstanceFinalitzatIds() {
		List<String> ids = processInstanceFinalitzatIdsThreadLocal.get();
		if (ids == null) {
			ids = new ArrayList<String>();
			processInstanceFinalitzatIdsThreadLocal.set(ids);
		}
		return ids;
	}
	public static void clearProcessInstanceFinalitzatIds() {
		List<String> ids = processInstanceFinalitzatIdsThreadLocal.get();
		if (ids != null) {
			ids.clear();
		} else {
			ids = new ArrayList<String>();
			processInstanceFinalitzatIdsThreadLocal.set(ids);
		}
	}

}
