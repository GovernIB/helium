/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Create;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Update;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDocumentCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDocumentCommand.Modificacio;

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
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
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
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public interface FirmaPassarela {}

}
