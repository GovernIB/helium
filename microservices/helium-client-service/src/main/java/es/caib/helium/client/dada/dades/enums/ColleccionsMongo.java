package es.caib.helium.client.dada.dades.enums;

import lombok.Getter;

/**
 * Enum representant el nom de les diferents col·leccions que empra el servei
 * Si s'afegeixen col·leccions a MongoDB afegir-les aquí també.
 */
@Getter
public enum ColleccionsMongo {

	EXPEDIENT("expedient"),
	DADA("dada"),
	DOCUMENT("document");
	
	private String nom;

	
	ColleccionsMongo(String nom) {
		this.nom = nom;
	}

}
