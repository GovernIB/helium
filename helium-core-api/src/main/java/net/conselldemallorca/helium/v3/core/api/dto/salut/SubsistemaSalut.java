package net.conselldemallorca.helium.v3.core.api.dto.salut;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubsistemaSalut extends EstatSalut {
	private String codi;
	private Long totalOk;
	private Long totalError;
}
