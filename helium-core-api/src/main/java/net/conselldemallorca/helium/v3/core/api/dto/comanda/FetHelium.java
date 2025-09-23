package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetHelium implements Fet {

	private String codi;
	private Double valor;
	
	public String getCodi() {
		return codi;
	}
	
	public Double getValor() {
		return valor;
	}
}
