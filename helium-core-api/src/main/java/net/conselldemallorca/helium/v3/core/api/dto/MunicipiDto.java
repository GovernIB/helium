package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Objecte que representa un municipi provinent d'una font externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MunicipiDto implements Serializable {

	private String codi;
	private String nom;
	private String codiEntitatGeografica;
	private Long codiProvincia;
	
	public MunicipiDto() {
		
	}
	
	public MunicipiDto(
			String codi,
			String nom) {
		this.codi = codi;
		this.nom = nom;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodiEntitatGeografica() {
		return codiEntitatGeografica;
	}
	public void setCodiEntitatGeografica(String codiEntitatGeografica) {
		this.codiEntitatGeografica = codiEntitatGeografica;
	}
	public Long getCodiProvincia() {
		return codiProvincia;
	}
	public void setCodiProvincia(Long codiProvincia) {
		this.codiProvincia = codiProvincia;
	}
	
	public String getCodiDir3() {
		return codi + "-" + codiEntitatGeografica;
	}

	private static final long serialVersionUID = -6781006082031161827L;

}
