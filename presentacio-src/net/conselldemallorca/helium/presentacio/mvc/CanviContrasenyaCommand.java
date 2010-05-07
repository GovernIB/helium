/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

/**
 * Command pel canvi de contrasenya del perfil
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CanviContrasenyaCommand {

	private String contrasenya;
	private String repeticio;



	public CanviContrasenyaCommand() {}

	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}

	public String getRepeticio() {
		return repeticio;
	}
	public void setRepeticio(String repeticio) {
		this.repeticio = repeticio;
	}

}
