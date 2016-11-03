package net.conselldemallorca.helium.jbpm3.command;

import java.util.Map;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * Command per substituir o afegir la llista de handlers a una definició de procés existent.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UpdateHandlersCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = 4153011174099818900L;
	private long id;
	private Map<String, byte[]> handlers;

	public UpdateHandlersCommand(
			long id, 
			Map<String, byte[]> handlers) {
		super();
		this.id = id;
		this.setHandlers(handlers);
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessDefinition processDefinition = jbpmContext.getGraphSession().getProcessDefinition(id);
		for (String key : handlers.keySet())
			processDefinition.getFileDefinition().addFile(key, handlers.get(key));
		jbpmContext.getGraphSession().saveProcessDefinition(processDefinition);
		return processDefinition;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public Map<String, byte[]> getHandlers() {
		return handlers;
	}

	public void setHandlers(Map<String, byte[]> handlers) {
		this.handlers = handlers;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	
	public UpdateHandlersCommand id(long id) {
		setId(id);
	    return this;
	}

	public UpdateHandlersCommand handlers(Map<String, byte[]> handlers) {
		setHandlers(handlers);
	    return this;
	}

}
