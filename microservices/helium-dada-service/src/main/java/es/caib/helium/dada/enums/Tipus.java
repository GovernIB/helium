package es.caib.helium.dada.enums;

import lombok.Getter;

/**
 * Enum representant els diferentS tipus que pot tenir una dada
 */
@Getter
public enum Tipus {

	LONG("LONG"),
	STRING("STRING"),
	DATE("DATE"),
	FLOAT("FLOAT"),
	TERMINI("TERMINI"),
	PRICE("PRICE"),
	INTEGER("INTEGER"),
	BOOLEAN("BOOLEAN"),
	TEXTAREA("TEXTAREA"),
	REGISTRE("REGISTRE"),
	SELECCIO("SELECCIO"),
	SUGGEST("SUGGEST"),
	ACCIO("ACCIO");

	private String valor; 
	
	private Tipus(String valor) {
		this.valor = valor;
	}
}
