/**
 * 
 */
package es.caib.helium.jbpm3.command;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per canviar el nom a una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GuardarErrorFinalitzacioCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String errorFinalitzacio;

	public GuardarErrorFinalitzacioCommand(
			long id,
			String errorFinalitzacio) {
		super();
		this.id = id;
		this.errorFinalitzacio = errorFinalitzacio;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance ti = jbpmContext.getTaskInstance(id);
		ti.setErrorFinalitzacio(errorFinalitzacio);
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getErrorFinalitzacio() {
		return errorFinalitzacio;
	}

	public void setErrorFinalitzacio(String errorFinalitzacio) {
		this.errorFinalitzacio = errorFinalitzacio;
	}

	@Override
	public String getAdditionalToStringInformation() {
		return "id=" + id + ", errorFinalitzacio=" + errorFinalitzacio;
	}

	//methods for fluent programming
	public GuardarErrorFinalitzacioCommand id(long id, String errorFinalitzacio) {
		setId(id);
		setErrorFinalitzacio(errorFinalitzacio);
	    return this;
	}

}
