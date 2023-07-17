package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

public class PortafirmesFluxInfoDto implements Serializable {

	private String nom;
	private String descripcio;

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	
	private static final long serialVersionUID = 3290339330233626534L;
}
