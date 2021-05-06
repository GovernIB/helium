package es.caib.helium.enums;

import lombok.Getter;

@Getter
public enum ValorsValidacio {
	
	TITOL_MAX_LENGTH(255),
	NUMERO_MAX_LENGTH(64);
	
	private final int valor;
	
	private ValorsValidacio(int valor) {
		this.valor = valor;
	}

}
