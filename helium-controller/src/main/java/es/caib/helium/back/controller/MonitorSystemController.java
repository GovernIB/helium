package es.caib.helium.back.controller;

import es.caib.helium.back.helper.MonitorHelper;
import net.minidev.json.JSONValue;
import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Controlador per la gestió d'perfils
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/monitor")
public class MonitorSystemController extends BaseController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return "v3/monitor";
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ResponseBody
	public String monitor(HttpServletRequest request, String familia) {
		return JSONValue.toJSONString(ejecutar(request));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, JSONArray> ejecutar(HttpServletRequest request) {
		Map<String, JSONArray> mjson = new LinkedHashMap<String,JSONArray>();
		JSONArray sistema = new JSONArray();
		JSONArray hilo = new JSONArray();
		JSONArray cputime = new JSONArray();
		JSONArray estado = new JSONArray();
		JSONArray espera = new JSONArray();
		JSONArray blockedtime = new JSONArray();
		
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		sistema.put(getMessage(request, "expedient.monitor.procesadores")+": " + Runtime.getRuntime().availableProcessors());
		sistema.put(getMessage(request, "expedient.monitor.memoria_disponible")+": " + MonitorHelper.humanReadableByteCount(Runtime.getRuntime().freeMemory()));
		sistema.put(getMessage(request, "expedient.monitor.memoria_maxima")+": " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Ilimitada" : MonitorHelper.humanReadableByteCount(Runtime.getRuntime().maxMemory())));
		sistema.put(getMessage(request, "expedient.monitor.memoria_total")+": " + MonitorHelper.humanReadableByteCount(Runtime.getRuntime().totalMemory()));
		sistema.put(getMessage(request, "expedient.monitor.os-name")+": " + MonitorHelper.getName());
		sistema.put(getMessage(request, "expedient.monitor.os-arch") + ": " + MonitorHelper.getArch());
		sistema.put(getMessage(request, "expedient.monitor.os-version") + ": " + MonitorHelper.getVersion());
		sistema.put(getMessage(request, "expedient.monitor.carga_cpu") + ": " + MonitorHelper.getCPULoad());
		
		for (File root : File.listRoots()) {
			sistema.put(getMessage(request, "expedient.monitor.space.total") + " " + root.getAbsolutePath()+": " + MonitorHelper.humanReadableByteCount(root.getTotalSpace()));
			sistema.put(getMessage(request, "expedient.monitor.space.free") + " " + root.getAbsolutePath()+": " + MonitorHelper.humanReadableByteCount(root.getFreeSpace()));
		}
        
		int numDeadlocked = 0; 
		if (bean.findMonitorDeadlockedThreads() != null) {
			numDeadlocked = bean.findMonitorDeadlockedThreads().length;
		}
		sistema.put(getMessage(request, "expedient.monitor.deadlocked")+": " + numDeadlocked);
		sistema.put(getMessage(request, "expedient.monitor.daemon_thread")+": " + bean.getDaemonThreadCount());
		
		bean.resetPeakThreadCount();
		
		if (bean.isThreadCpuTimeSupported()) {
			long[] ids = bean.getAllThreadIds();
			ThreadInfo[] info = bean.getThreadInfo(ids);
			Set hs = new HashSet();
			for (int a = 0; a < ids.length; ++a) {
				hs.add(bean.getThreadCpuTime(ids[a]));
			}
			long tiempoCPUTotal =  ((Long)Collections.max(hs)).longValue();
			for (int a = 0; a < ids.length; ++a) {
				String nombre = (info[a].getLockName() == null ? info[a].getThreadName() : info[a].getLockName());
				if (!"main".equals(nombre)) {
					hilo.put(nombre);
					long tiempoCPU = (long) ((float)100*((float) bean.getThreadCpuTime(ids[a]) / (float) tiempoCPUTotal));
					cputime.put(((tiempoCPU>100)?100:tiempoCPU) + " %");
					estado.put(getMessage(request, "expedient.monitor."+info[a].getThreadState()));
					espera.put(((info[a].getWaitedTime() == -1)? 0:info[a].getWaitedTime()) + " ns");
					blockedtime.put(((info[a].getBlockedTime() == -1)? 0:info[a].getBlockedTime()) + " ns");
				}
			}
		}
		
		mjson.put("sistema", sistema);
		mjson.put("hilo", hilo);
		mjson.put("cputime", cputime);
		mjson.put("estado", estado);
		mjson.put("espera", espera);
		mjson.put("blockedtime", blockedtime);
		return mjson;
	}
}
