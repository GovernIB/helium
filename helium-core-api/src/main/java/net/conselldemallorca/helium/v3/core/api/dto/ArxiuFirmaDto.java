/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.List;

/**
 * Informació de firma provinent de l'arxiu i detalls provinents
 * del plugin de validació de firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuFirmaDto {

	private NtiTipoFirmaEnumDto tipus;
	private ArxiuFirmaPerfilEnumDto perfil;
	private String fitxerNom;
	private byte[] contingut;
	private String tipusMime;
	private String csvRegulacio;
	private boolean autofirma;
	private List<ArxiuFirmaDetallDto> detalls;

	public NtiTipoFirmaEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(NtiTipoFirmaEnumDto tipus) {
		this.tipus = tipus;
	}
	public ArxiuFirmaPerfilEnumDto getPerfil() {
		return perfil;
	}
	public void setPerfil(ArxiuFirmaPerfilEnumDto perfil) {
		this.perfil = perfil;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public void setFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public void setTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}
	public String getCsvRegulacio() {
		return csvRegulacio;
	}
	public void setCsvRegulacio(String csvRegulacio) {
		this.csvRegulacio = csvRegulacio;
	}
	public boolean isAutofirma() {
		return autofirma;
	}
	public void setAutofirma(boolean autofirma) {
		this.autofirma = autofirma;
	}
	public List<ArxiuFirmaDetallDto> getDetalls() {
		return detalls;
	}
	public void setDetalls(List<ArxiuFirmaDetallDto> detalls) {
		this.detalls = detalls;
	}

	public String getContingutComString() {
		if (contingut != null) {
			return new String(contingut);
		}
		return null;
	}

}
