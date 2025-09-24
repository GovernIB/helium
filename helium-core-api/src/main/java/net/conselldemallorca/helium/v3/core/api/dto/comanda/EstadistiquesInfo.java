package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstadistiquesInfo {
	private String codi;
	private String versio;
	private Date data;
	private List<DimensioDesc> dimensions;
	private List<IndicadorDesc> indicadors;
}
