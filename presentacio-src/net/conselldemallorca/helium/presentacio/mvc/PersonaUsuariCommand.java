/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.model.dto.PersonaUsuariDto;

/**
 * Command pel manteniment de persones
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class PersonaUsuariCommand extends PersonaUsuariDto {

	private static final long serialVersionUID = 1L;
	private String repeticio;
	public String getRepeticio() {
		return repeticio;
	}
	public void setRepeticio(String repeticio) {
		this.repeticio = repeticio;
	}

}
