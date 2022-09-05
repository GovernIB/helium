/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;

/**
 * Command per modificar les dades d'integració amb Pinbal d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusIntegracioPinbalCommand {

	private boolean pinbalActiu;
	@Size(max = 44, groups = {Modificacio.class})
	private String pinbalNifCif;


	
	public boolean isPinbalActiu() {
		return pinbalActiu;
	}
	public void setPinbalActiu(boolean pinbalActiu) {
		this.pinbalActiu = pinbalActiu;
	}

	public String getPinbalNifCif() {
		return pinbalNifCif;
	}
	public void setPinbalNifCif(String pinbalNifCif) {
		this.pinbalNifCif = pinbalNifCif;
	}
	public static ExpedientTipusIntegracioPinbalCommand toCommand(ExpedientTipusDto dto) {
		ExpedientTipusIntegracioPinbalCommand command = new ExpedientTipusIntegracioPinbalCommand();
		command.setPinbalActiu(dto.isPinbalActiu());
		command.setPinbalNifCif(dto.getPinbalNifCif());
		return command;
	}
	
	public interface Modificacio {}

}
