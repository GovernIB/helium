package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndicadorDesc {
	private String codi;
	private String nom;
	private String descripcio;
	private Format format;
}
