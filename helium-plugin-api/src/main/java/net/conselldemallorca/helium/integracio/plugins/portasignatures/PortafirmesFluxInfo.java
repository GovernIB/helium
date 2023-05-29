package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.io.Serializable;

public class PortafirmesFluxInfo implements Serializable {

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
	
	private static final long serialVersionUID = -1665824823934702923L;

}
