/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.Date;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per canviar el nom a una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MarcarIniciFinalitzacioSegonPlaCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public MarcarIniciFinalitzacioSegonPlaCommand(
			long id) {
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance ti = jbpmContext.getTaskInstance(id);
		ti.setIniciFinalitzacio(new Date());
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
	public MarcarIniciFinalitzacioSegonPlaCommand id(long id) {
		setId(id);
	    return this;
	}

}
