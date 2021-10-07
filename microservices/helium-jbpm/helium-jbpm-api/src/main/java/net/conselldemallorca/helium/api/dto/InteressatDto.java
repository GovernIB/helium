/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InteressatDto {
	
	private Long id;
	private String codi;
	private String nif;
	private String dir3Codi;
	private String nom;
	private String llinatge1;  
	private String llinatge2;  

	private String email;  
	private String telefon;
	private Long expedientId;
	private InteressatTipusEnumDto tipus;
	
	private Boolean entregaPostal;
	private DadesEnviamentDto.EntregaPostalTipus entregaTipus;
	private String linia1;
	private String linia2;
	private String codiPostal;
	private Boolean entregaDeh;
	private Boolean entregaDehObligat;


	public Boolean getEntregaPostal() {
		return entregaPostal != null ? entregaPostal : false;
	}

	public Boolean getEntregaDeh() {
		return entregaDeh != null ? entregaDeh : false;
	}

	public Boolean getEntregaDehObligat() {
		return entregaDehObligat != null ? entregaDehObligat : false;
	}

	public String getFullNom() {
		StringBuilder fullNom = new StringBuilder(nom);
		if (llinatge1 != null)
			fullNom.append(" ").append(llinatge1);
		if (llinatge2 != null)
			fullNom.append(" ").append(llinatge2);
		return fullNom.toString();
	}
	
	public String getFullInfo() {
		String codiDocument;
		if (tipus != null && InteressatTipusEnumDto.ADMINISTRACIO.equals(tipus))
			codiDocument = dir3Codi;
		else
			codiDocument = nif;
		return codiDocument + " - " + getFullNom();
	}
}
