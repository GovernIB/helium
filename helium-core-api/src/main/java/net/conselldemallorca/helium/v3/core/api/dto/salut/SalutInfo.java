package net.conselldemallorca.helium.v3.core.api.dto.salut;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalutInfo {
	private String codi;
	private Date data;
	private EstatSalut estat;
	private EstatSalut bd;
	private List<IntegracioSalut> integracions;
	private List<DetallSalut> altres;
	private List<MissatgeSalut> missatges;
	private String versio;
	private List<SubsistemaSalut> subsistemes;
}
