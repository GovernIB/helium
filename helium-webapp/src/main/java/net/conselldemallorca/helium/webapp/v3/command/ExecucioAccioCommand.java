/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

/**
 * Command pel l'execució d'una acció
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExecucioAccioCommand {

	private String accioId;



	public ExecucioAccioCommand() {}

	public String getAccioId() {
		return accioId;
	}
	public void setAccioId(String accioId) {
		this.accioId = accioId;
	}

}
