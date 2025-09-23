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
public class RegistresEstadistics {
	private Temps temps;
	private List<RegistreEstadistic> fets;

}
