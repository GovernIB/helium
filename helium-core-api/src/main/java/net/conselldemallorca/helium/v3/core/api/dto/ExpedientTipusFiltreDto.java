/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per al filtre d'anotacions de distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusFiltreDto {


	private String codiSIA;
	private String numRegistre;
	private String codiTipologia;
	private String nomTipologia;
	private String rol;
	
	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}
	
	
	public String getNomTipologia() {
		return nomTipologia;
	}

	public void setNomTipologia(String nomTipologia) {
		this.nomTipologia = nomTipologia;
	}

	public String getCodiTipologia() {
		return codiTipologia;
	}

	public void setCodiTipologia(String codiTipologia) {
		this.codiTipologia = codiTipologia;
	}

	private Long expedientTipusId;


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}

	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	public String getCodiSIA() {
		return codiSIA;
	}

	public void setCodiSIA(String codiSIA) {
		this.codiSIA = codiSIA;
	}

	public String getNumRegistre() {
		return numRegistre;
	}

	public void setNumRegistre(String numRegistre) {
		this.numRegistre = numRegistre;
	}

	
}
