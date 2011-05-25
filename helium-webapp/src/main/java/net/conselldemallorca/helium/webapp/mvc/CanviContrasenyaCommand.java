/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

/**
 * Command pel canvi de contrasenya del perfil
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
