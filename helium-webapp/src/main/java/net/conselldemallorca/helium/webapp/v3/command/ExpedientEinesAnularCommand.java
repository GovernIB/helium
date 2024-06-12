package net.conselldemallorca.helium.webapp.v3.command;

import org.hibernate.validator.constraints.NotBlank;

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
