package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistreEstadistic {
	private List<Dimensio> dimensions;
	private List<Fet> fets;
}
