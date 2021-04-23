package es.caib.helium.enums;

import lombok.Getter;

@Getter
public enum Collections {

	EXPEDIENT("expedient"),
	DADA("dada");
	
	private String nom;
	
	private Collections(String nom) {
		this.nom = nom;
	}
}
