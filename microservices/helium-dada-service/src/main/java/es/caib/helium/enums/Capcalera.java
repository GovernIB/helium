package es.caib.helium.enums;

import lombok.Getter;

@Getter
public enum Capcalera {

	id("_id"),
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
