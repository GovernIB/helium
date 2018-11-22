package net.conselldemallorca.helium.webapp.v3.command;

import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;

public class ExpedientTipusEstadisticaCommand {
	
	private Integer anyInicial;
	private Integer anyFinal;
	private Long expedientTipusId;
	private MostrarAnulatsDto mostrarAnulats = MostrarAnulatsDto.NO;
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public MostrarAnulatsDto getMostrarAnulats() {
		return mostrarAnulats;
	}
	public Integer getAnyInicial() {
		return anyInicial;
	}
	public void setAnyInicial(Integer anyInicial) {
		this.anyInicial = anyInicial;
	}
	public Integer getAnyFinal() {
		return anyFinal;
	}
	public void setAnyFinal(Integer anyFinal) {
		this.anyFinal = anyFinal;
	}
	public void setMostrarAnulats(MostrarAnulatsDto mostrarAnulats) {
		this.mostrarAnulats = mostrarAnulats;
	}
	
}
