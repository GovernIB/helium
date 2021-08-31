package es.caib.helium.dada.enums;

import lombok.Getter;

/**
 * Enum representant els diferentgs tipus que pot tenir una dada
 */
@Getter
public enum Tipus {

	LONG("Long"),
	STRING("String"),
	DATE("Date"),
	FLOAT("Float"),
	TERMINI("Termini"),
	PRICE("Preu"),
	INTEGER("Integer"),
	BOOLEAN("Boolean"),
	TEXTAREA("TextArea"),
	REGISTRE("Registre"),
	SELECCIO("Seleccio"),
	SUGGEST("Suggest"),
	ACCIO("Accio");

	private String valor; 
	
	private Tipus(String valor) {
		this.valor = valor;
	}
}
