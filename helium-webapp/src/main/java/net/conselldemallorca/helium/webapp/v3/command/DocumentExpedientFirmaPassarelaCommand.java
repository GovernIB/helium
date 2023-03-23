/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Create;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Update;

/**
 * Command pel formulari de firma per passarel·la a la gestió de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentExpedientFirmaPassarelaCommand {

	@NotEmpty(groups = {FirmaPassarela.class}) 
	@Size(max=256, groups = {FirmaPassarela.class})
	private String motiu;
	@Size(max=256, groups = {FirmaPassarela.class})
	private String lloc;
	
	public String getMotiu() {
		return motiu;
	}
	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}
	public String getLloc() {
		return lloc;
	}
	public void setLloc(String lloc) {
		this.lloc = lloc;
	}
	
	public interface FirmaPassarela {}

}
