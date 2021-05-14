package es.caib.helium.enums;

import lombok.Getter;

/**
 * Enum representant els diferentgs tipus que pot tenir una dada
 */
@Getter
public enum Tipus {
	
	Long("Long"),
	String("String"),
	Date("Date"),
	Float("Float"),
	Termini("Termini"),
	Preu("Preu"),
	Integer("Integer"),
	Boolean("Boolean"),
	Registre("Registre");
	
	private String valor; 
	
	private Tipus(String valor) {
		this.valor = valor;
	}
}
