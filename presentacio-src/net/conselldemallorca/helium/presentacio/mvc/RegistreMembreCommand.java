/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

/**
 * Command per afegir un membre a un camp de tipus registre
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class RegistreMembreCommand {

	private Long registreId;
	private Long membreId;
	private boolean obligatori;
	private boolean llistar;



	public RegistreMembreCommand() {}

	public Long getRegistreId() {
		return registreId;
	}
	public void setRegistreId(Long registreId) {
		this.registreId = registreId;
	}
	public Long getMembreId() {
		return membreId;
	}
	public void setMembreId(Long membreId) {
		this.membreId = membreId;
	}
	public boolean isObligatori() {
		return obligatori;
	}
	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
	}
	public boolean isLlistar() {
		return llistar;
	}
	public void setLlistar(boolean llistar) {
		this.llistar = llistar;
	}

}
