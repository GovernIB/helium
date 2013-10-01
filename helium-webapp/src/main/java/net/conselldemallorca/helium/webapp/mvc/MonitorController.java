/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.service.MonitorHelper;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per a la pÃ gina inicial (index).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class MonitorController extends BaseController {

	public Map<String,JSONArray> ejecutar() {
		Map<String, JSONArray> mjson = new LinkedHashMap<String,JSONArray>();
		JSONArray sistema = new JSONArray();
		JSONArray cabecera = new JSONArray();
		JSONArray hilo = new JSONArray();
		JSONArray cputime = new JSONArray();
		JSONArray estado = new JSONArray();
		JSONArray espera = new JSONArray();
		JSONArray blockedtime = new JSONArray();
		
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		sistema.add(getMessage("expedient.monitor.procesadores")+": " + Runtime.getRuntime().availableProcessors());
		sistema.add(getMessage("expedient.monitor.memoria_disponible")+": " + MonitorHelper.humanReadableByteCount(Runtime.getRuntime().freeMemory()));
		sistema.add(getMessage("expedient.monitor.memoria_maxima")+": " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Ilimitada" : MonitorHelper.humanReadableByteCount(Runtime.getRuntime().maxMemory())));
		sistema.add(getMessage("expedient.monitor.memoria_total")+": " + MonitorHelper.humanReadableByteCount(Runtime.getRuntime().totalMemory()));
		sistema.add(getMessage("expedient.monitor.os-name")+": " + MonitorHelper.getName());
		sistema.add(getMessage("expedient.monitor.os-arch") + ": " + MonitorHelper.getArch());
		sistema.add(getMessage("expedient.monitor.os-version") + ": " + MonitorHelper.getVersion());
		sistema.add(getMessage("expedient.monitor.carga_cpu") + ": " + MonitorHelper.getCPULoad());
        
		int numDeadlocked = 0; 
		if (bean.findMonitorDeadlockedThreads() != null) {
			numDeadlocked = bean.findMonitorDeadlockedThreads().length;
		}
		sistema.add(getMessage("expedient.monitor.deadlocked")+": " + numDeadlocked);
		sistema.add(getMessage("expedient.monitor.daemon_thread")+": " + bean.getDaemonThreadCount());
		
		bean.resetPeakThreadCount();
		
		if (bean.isThreadCpuTimeSupported()) {
			long[] ids = bean.getAllThreadIds();
			ThreadInfo[] info = bean.getThreadInfo(ids);
			Set hs = new HashSet();
			for (int a = 0; a < ids.length; ++a) {
				hs.add(bean.getThreadCpuTime(ids[a]));
			}
			long tiempoCPUTotal = Collections.max(hs);
			for (int a = 0; a < ids.length; ++a) {
				String nombre = (info[a].getLockName() == null ? info[a].getThreadName() : info[a].getLockName());
				if (!"main".equals(nombre)) {
					hilo.add(nombre);
					long tiempoCPU = (long) ((float)100*((float) bean.getThreadCpuTime(ids[a]) / (float) tiempoCPUTotal));
					cputime.add(((tiempoCPU>100)?100:tiempoCPU) + " %");
					estado.add(getMessage("expedient.monitor."+info[a].getThreadState()));
					espera.add(((info[a].getWaitedTime() == -1)? 0:info[a].getWaitedTime()) + " ns");
					blockedtime.add(((info[a].getBlockedTime() == -1)? 0:info[a].getBlockedTime()) + " ns");
				}
			}
		}
		
		mjson.put("sistema", sistema);
		mjson.put("cabecera", cabecera);
		mjson.put("hilo", hilo);
		mjson.put("cputime", cputime);
		mjson.put("estado", estado);
		mjson.put("espera", espera);
		mjson.put("blockedtime", blockedtime);
		return mjson;
	}

	@RequestMapping(value = "/monitor/all", method = RequestMethod.GET)
	@ResponseBody
	public String monitor(HttpServletRequest request, String familia) {
		String resultat = "";
		if (MonitorHelper.getActiu()) {
			resultat = JSONValue.toJSONString(ejecutar());
		}
		return resultat;
	}
}
