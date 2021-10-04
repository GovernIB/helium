package es.caib.helium.dada.enums;

import lombok.Getter;

/**
 * Enum representant els camps de la col·lecció {@link es.caib.helium.enums.Collections#DADA} a MongoDB
 * Si s'afegeixen més camps a la col·lecció també cal afegir-los aquí també.
 */
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
	
	Dada(String camp) {
		this.camp = camp;
	}
}
