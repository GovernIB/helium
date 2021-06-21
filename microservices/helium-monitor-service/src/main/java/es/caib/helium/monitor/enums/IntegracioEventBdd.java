package es.caib.helium.monitor.enums;

import lombok.Getter;

@Getter
public enum IntegracioEventBdd {

	CODI("codi"),
	ENTORN_ID("entornId"),
	DATA("data"),
	DESCRIPCIO("descripcio"),
	TEMPS_RESPOSTA("tempsResposta"),
	TIPUS("tipus"),
	ESTAT("estat");
	
	private String camp;
	
	private IntegracioEventBdd(String camp) {
		this.camp = camp;
	}
}
