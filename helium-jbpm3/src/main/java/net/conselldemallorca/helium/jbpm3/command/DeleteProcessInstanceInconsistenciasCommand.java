/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Para borrar inconsistencias de PROVES
 */
public class DeleteProcessInstanceInconsistenciasCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private boolean met;

	public DeleteProcessInstanceInconsistenciasCommand() {}

	public DeleteProcessInstanceInconsistenciasCommand(long id, boolean met){
		super();
		this.id = id;
		this.met = met;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance processInstance = jbpmContext.getProcessInstance(id);
		List<Long> lista = new ArrayList<Long>();
		lista.add(id);
		if (processInstance != null) {
			if (met)
				jbpmContext.getGraphSession().deleteProcessInstanceInconsistencias(processInstance, true, true);
			else {
				jbpmContext.getGraphSession().deleteProcessInstanceInconsistencias2(processInstance, true, true);
			}
		}
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public DeleteProcessInstanceInconsistenciasCommand id(long id) {
		setId(id);
	    return this;
	}

	public boolean isMet() {
		return met;
	}

	public void setMet(boolean met) {
		this.met = met;
	}

}
