/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import net.conselldemallorca.helium.jbpm3.api.HeliumActionHandler;
import net.conselldemallorca.helium.jbpm3.api.HeliumApi;
import net.conselldemallorca.helium.jbpm3.api.HeliumApiImpl;
import net.conselldemallorca.helium.jbpm3.handlers.AccioExternaRetrocedirHandler;

/**
 * Command per executar un handler predefinit donada la seva classe i els paràmetres.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExecuteHadlerPredefinitCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String predefinitClasse;
	private Map<String, String> predefinitDades;
	private boolean isTaskInstance = false;
	private boolean goBack = false;
	private List<String> params;

	public ExecuteHadlerPredefinitCommand(
			long id,
			String predefinitClasse,
			Map<String, String> predefinitDades) {
		super();
		this.id = id;
		this.predefinitClasse = predefinitClasse;
		this.predefinitDades = predefinitDades;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		
		Class<?> clazz = Class.forName(predefinitClasse);
		Constructor<?> ctor = clazz.getConstructor();
		Object object = ctor.newInstance();
		if (object instanceof ActionHandler) {
			ActionHandler handler = (ActionHandler) object;
			// Dades
			PropertyAccessor pa = PropertyAccessorFactory.forDirectFieldAccess(object);
			Object valor;
			for (String camp : predefinitDades.keySet()) {
				valor = predefinitDades.get(camp);
				if (valor != null && 
						(!(valor instanceof String) || !((String) valor).isEmpty())) {
					pa.setPropertyValue(camp, predefinitDades.get(camp));
				}
			}
			ProcessInstance pi = jbpmContext.getProcessInstance(id);
			ExecutionContext ec = new ExecutionContext(pi.getRootToken());
			
			handler.execute(ec);
		}
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isTaskInstance() {
		return isTaskInstance;
	}
	public void setTaskInstance(boolean isTaskInstance) {
		this.isTaskInstance = isTaskInstance;
	}
	public boolean isGoBack() {
		return goBack;
	}
	public void setGoBack(boolean goBack) {
		this.goBack = goBack;
	}
	public List<String> getParams() {
		return params;
	}
	public void setParams(List<String> params) {
		this.params = params;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id + ", predefinitClasse=" + predefinitClasse;
	}

	public void executeGoBack(Action action, ExecutionContext context) throws Exception {
		if (action != null && action.getActionDelegation() != null) {
			ClassLoader surroundingClassLoader = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(JbpmConfiguration.getProcessClassLoader(context.getProcessDefinition()));
				Object actionHandler = action.getActionDelegation().getInstance();
				if (actionHandler instanceof AccioExternaRetrocedirHandler) {
					((AccioExternaRetrocedirHandler)actionHandler).retrocedir(context, params);
				} else if (actionHandler instanceof HeliumActionHandler) {
					HeliumApi heliumApi = new HeliumApiImpl(context);
					((HeliumActionHandler)actionHandler).retrocedir(heliumApi, params);
				}
			} finally {
				Thread.currentThread().setContextClassLoader(surroundingClassLoader);
			}
		}
	}

}
