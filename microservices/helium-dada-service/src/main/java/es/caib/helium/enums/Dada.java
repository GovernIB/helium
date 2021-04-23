package es.caib.helium.enums;

import lombok.Getter;

@Getter
public enum Dada {

	ID("_id"),
	CODI("codi"),
	TIPUS("tipus"),
	MULTIPLE("multiple"),
	VALOR("valor"),
	EXPEDIENT_ID("expedientId"),
	PROCES_ID("procesId");
	
	private String camp;
	
	private Dada(String camp) {
		this.camp = camp;
	}
}
