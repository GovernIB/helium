package es.caib.helium.enums;

import lombok.Getter;

/**
 * Enum representant el nom de les diferents col·leccions que empra el servei
 * Si s'afegeixen col·leccions a MongoDB afegir-les aquí també.
 */
@Getter
public enum Collections {

	EXPEDIENT("expedient"),
	DADA("dada");
	
	private String nom;
	
	private Collections(String nom) {
		this.nom = nom;
	}
}
