/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

/**
 * Command per afegir una validació a una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioTascaCommand {

	private Long tascaId;
	private String expressio;
	private String missatge;



	public ValidacioTascaCommand() {}

	public Long getTascaId() {
		return tascaId;
	}
	public void setTascaId(Long tascaId) {
		this.tascaId = tascaId;
	}
	public String getExpressio() {
		return expressio;
	}
	public void setExpressio(String expressio) {
		this.expressio = expressio;
	}
	public String getMissatge() {
		return missatge;
	}
	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}

}
