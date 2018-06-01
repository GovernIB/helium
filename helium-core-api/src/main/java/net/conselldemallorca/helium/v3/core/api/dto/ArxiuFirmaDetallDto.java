/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

/**
 * Detalls de firma provinents del plugin de validació de firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuFirmaDetallDto {

	private Date data;
	private String responsableNif;
	private String responsableNom;
	private String emissorCertificat;

	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getResponsableNif() {
		return responsableNif;
	}
	public void setResponsableNif(String responsableNif) {
		this.responsableNif = responsableNif;
	}
	public String getResponsableNom() {
		return responsableNom;
	}
	public void setResponsableNom(String responsableNom) {
		this.responsableNom = responsableNom;
	}
	public String getEmissorCertificat() {
		return emissorCertificat;
	}
	public void setEmissorCertificat(String emissorCertificat) {
		this.emissorCertificat = emissorCertificat;
	}

}
