package es.caib.helium.integracio.enums.validacio;

import lombok.Getter;

@Getter
public enum Response {

	ESTAT_OK("OK"),
	ESTAT_ERROR("ERROR");
	
	private String estat;
	
	private Response(String estat) {
		this.estat = estat;
	}
}
