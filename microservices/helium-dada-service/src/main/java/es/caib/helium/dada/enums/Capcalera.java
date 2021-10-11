package es.caib.helium.dada.enums;

import es.caib.helium.client.dada.dades.enums.ColleccionsMongo;
import lombok.Getter;

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
	
	private Capcalera(String camp) {
		this.camp = camp;
	}
}
