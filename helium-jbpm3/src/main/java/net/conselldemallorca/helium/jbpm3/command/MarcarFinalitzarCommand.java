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
public class MarcarFinalitzarCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private Date marcadaFinalitzar;
	private String outcome;

	public MarcarFinalitzarCommand(
			long id,
			Date marcadaFinalitzar,
			String outcome) {
		super();
		this.id = id;
		this.marcadaFinalitzar = marcadaFinalitzar;
		this.outcome = outcome;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance ti = jbpmContext.getTaskInstance(id);
		ti.setMarcadaFinalitzar(this.marcadaFinalitzar);
		ti.setIniciFinalitzacio(null);
		ti.setErrorFinalitzacio(null);
		ti.setSelectedOutcome(this.outcome);
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public Date getMarcadaFinalitzar() {
		return marcadaFinalitzar;
	}
	public void setMarcadaFinalitzar(Date marcadaFinalitzar) {
		this.marcadaFinalitzar = marcadaFinalitzar;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	@Override
	public String getAdditionalToStringInformation() {
		return "id=" + id + ", outcome=" + outcome;
	}

	//methods for fluent programming
	public MarcarFinalitzarCommand id(long id, String outcome) {
		setId(id);
		setOutcome(outcome);
	    return this;
	}

}
