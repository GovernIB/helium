/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.Date;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Command per a retrocedir la finalització d'una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcessInstanceEndCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private Date dataFinalitzacio;

	public ProcessInstanceEndCommand(long id, Date dataFinalitzacio){
		super();
		this.id = id;
		this.dataFinalitzacio = dataFinalitzacio;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance processInstance = jbpmContext.getProcessInstance(id);
		processInstance.setEnd(dataFinalitzacio);
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

}
