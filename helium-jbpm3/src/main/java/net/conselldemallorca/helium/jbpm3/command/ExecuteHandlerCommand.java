/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.instantiation.ProcessClassLoader;

import net.conselldemallorca.helium.jbpm3.api.HeliumApi;
import net.conselldemallorca.helium.jbpm3.api.HeliumApiImpl;

/**
 * Command per executar una acció global a dins una instància de procés jBPM
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExecuteHandlerCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String handlerName;
	private Map<String, String> dades;

	public ExecuteHandlerCommand(
			long id,
			String handlerName,
			Map<String, String> dades) {
		super();
		this.id = id;
		this.handlerName = handlerName;
		this.dades = dades;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance pi = null;
		ProcessDefinition pd  = null;
		pi = jbpmContext.getProcessInstance(id);
		pd = pi.getProcessDefinition();
		ExecutionContext context = new ExecutionContext(pi.getRootToken());
		ProcessClassLoader classLoader = (ProcessClassLoader)JbpmConfiguration.getProcessClassLoader(context.getProcessDefinition());
		Class handlerClass = classLoader.findClass(handlerName);
		Constructor<?> ctor = handlerClass.getConstructor();
		Object handler = ctor.newInstance();

		if (dades != null) {
			for (Map.Entry<String, String> dada : dades.entrySet()) {
				if (!StringUtils.isEmpty(dada.getValue())) {
					String setterName = "set" + StringUtils.capitalize(dada.getKey());
					Method setterMethod = handlerClass.getMethod(setterName, String.class);
					setterMethod.invoke(handler, dada.getValue());
				}
			}
		}

		Method executeMethod = handlerClass.getMethod("execute", HeliumApi.class);
		executeMethod.invoke(handler, new HeliumApiImpl(context));
		return null;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	public Map<String, String> getDades() {
		return dades;
	}

	public void setDades(Map<String, String> dades) {
		this.dades = dades;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id + ", handlerName=" + handlerName;
	}

}
