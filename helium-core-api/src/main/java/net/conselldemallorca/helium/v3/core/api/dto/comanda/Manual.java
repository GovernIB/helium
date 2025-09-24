package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Manual {
	private String nom;
	private String path;
}