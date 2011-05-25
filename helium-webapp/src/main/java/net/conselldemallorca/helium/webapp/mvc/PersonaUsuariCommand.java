/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.dto.PersonaUsuariDto;

/**
 * Command pel manteniment de persones
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
