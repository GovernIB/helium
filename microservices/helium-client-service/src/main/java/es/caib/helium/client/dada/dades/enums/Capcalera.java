package es.caib.helium.client.dada.dades.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representant els camps de la col·lecció {@link ColleccionsMongo#EXPEDIENT} a MongoDB
 * S'anomena capçalera perquè fa referencia a les dades de capçalera.
 * Si s'afegeixen més camps a la col·lecció també cal afegir-los aquí també.
 */
@Getter
public enum Capcalera {

	ID("id"),
	EXPEDIENT_ID("expedientId"),
	ENTORN_ID("entornId"),
	TIPUS_ID("tipusId"),
	NUMERO("numero"),
	TITOL("titol"),
	PROCES_PRINCIPAL_ID("procesPrincipalId"),
	ESTAT_ID("estatId"),
	DATA_INICI("dataInici"),
	DATA_FI("dataFi"),
	DADES("dades");
	
	private String camp;

	// Map per trovar la Capcalera a partir del valor String
	private static final Map<String, Capcalera> lookup = new HashMap<>();
	static {
		for (var col : Capcalera.values()) {
			lookup.put(col.getCamp(), col);
		}
	}
	
	Capcalera(String camp) {
		this.camp = camp;
	}


	public static Capcalera getByNom(String nom) {
		try {
			return lookup.get(nom);
		} catch (Exception ex) {
			return null;
		}
	}
}
