package net.conselldemallorca.helium.v3.core.api.dto.comanda;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Temps {
	private Date data;
	private int anualitat;
	private int trimestre;
}
