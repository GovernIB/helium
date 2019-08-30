/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

/**
 * Command per afegir una validació a una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioCampCommand {

	private Long campId;
	private String expressio;
	private String missatge;



	public ValidacioCampCommand() {}

	public Long getCampId() {
		return campId;
	}
	public void setCampId(Long campId) {
		this.campId = campId;
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
