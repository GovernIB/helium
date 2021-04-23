package es.caib.helium.enums;

import lombok.Getter;

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
