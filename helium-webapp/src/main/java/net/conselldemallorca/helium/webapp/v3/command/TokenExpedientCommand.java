/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;


/**
 * Command per gestionar els documents d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TokenExpedientCommand {

	private String nodeRetrocedir;
	private boolean cancelar;

	public TokenExpedientCommand() {}

	public String getNodeRetrocedir() {
		return nodeRetrocedir;
	}

	public void setNodeRetrocedir(String nodeRetrocedir) {
		this.nodeRetrocedir = nodeRetrocedir;
	}

	public boolean isCancelar() {
		return cancelar;
	}

	public void setCancelar(boolean cancelar) {
		this.cancelar = cancelar;
	}

}
