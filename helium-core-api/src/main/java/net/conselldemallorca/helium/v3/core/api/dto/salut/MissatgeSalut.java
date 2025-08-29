package net.conselldemallorca.helium.v3.core.api.dto.salut;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MissatgeSalut {
	private Date data;
	private String nivell;
	private String missatge;
}
