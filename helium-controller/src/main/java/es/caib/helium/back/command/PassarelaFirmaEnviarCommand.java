/**
 * 
 */
package es.caib.helium.back.command;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Command per a enviar documents a la passarela de firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PassarelaFirmaEnviarCommand {

	@NotEmpty @Size(max=256)
	private String motiu;
	@NotEmpty @Size(max=256)
	private String lloc;
	
	private String documentId;

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

	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
