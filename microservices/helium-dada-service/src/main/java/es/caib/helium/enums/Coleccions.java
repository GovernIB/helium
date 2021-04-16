package es.caib.helium.enums;

import lombok.Getter;

@Getter
public enum Coleccions {

	EXPEDIENT("expedient"),
	DADA("dada");
	
	private String nom;
	
	private Coleccions(String nom) {
		this.nom = nom;
	}
}
