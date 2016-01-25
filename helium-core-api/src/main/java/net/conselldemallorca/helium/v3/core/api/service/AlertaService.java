package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;



public interface AlertaService {
	public AlertaDto marcarLlegida(Long alertaId);
	public AlertaDto marcarNoLlegida(Long alertaId);
	public AlertaDto marcarEsborrada(Long alertaId);
}
