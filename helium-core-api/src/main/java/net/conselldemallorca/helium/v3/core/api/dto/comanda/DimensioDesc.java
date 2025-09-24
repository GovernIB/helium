package net.conselldemallorca.helium.v3.core.api.dto.comanda;

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
public class DimensioDesc {
	private String codi;
	private String nom;
	private String descripcio;
	private List<String> valors;
}
