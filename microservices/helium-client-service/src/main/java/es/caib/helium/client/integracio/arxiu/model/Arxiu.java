package es.caib.helium.client.integracio.arxiu.model;

import lombok.Data;

@Data
public class Arxiu {

	private String nom;
	private byte[] contingut;
	private String tipusMime;
	
	public long getTamany() {
		return (contingut != null) ? contingut.length : 0;
	}

	public String getExtensio() {
		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nom.length() - 1) {
			return nom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}
}
