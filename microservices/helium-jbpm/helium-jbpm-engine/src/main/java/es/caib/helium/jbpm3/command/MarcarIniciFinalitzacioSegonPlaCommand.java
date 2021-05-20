/**
 * 
 */
package es.caib.helium.jbpm3.command;

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
	private Date iniciFinalitzacio;

	public MarcarIniciFinalitzacioSegonPlaCommand(
			long id,
			Date iniciFinalitzacio) {
		super();
		this.id = id;
		this.setIniciFinalitzacio(iniciFinalitzacio);
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance ti = jbpmContext.getTaskInstance(id);
		ti.setIniciFinalitzacio(iniciFinalitzacio);
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public Date getIniciFinalitzacio() {
		return iniciFinalitzacio;
	}

	public void setIniciFinalitzacio(Date iniciFinalitzacio) {
		this.iniciFinalitzacio = iniciFinalitzacio;
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
