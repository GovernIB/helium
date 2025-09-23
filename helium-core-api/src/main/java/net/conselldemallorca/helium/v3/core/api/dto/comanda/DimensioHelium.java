package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DimensioHelium implements Dimensio {
	private String codi;
	private String valor;
	
	public String getCodi() {
		return codi;
	}
	
	public String getValor() {
		return valor;
	}
	
}
