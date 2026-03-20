package es.caib.helium.back.command;

import javax.validation.constraints.NotBlank;

public class ExpedientEinesAnularCommand {

	@NotBlank
	private String motiu;

	public String getMotiu() {
		return motiu;
	}

	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}
	
	
}
