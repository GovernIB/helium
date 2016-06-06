package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;



public interface AlertaService {
	public AlertaDto marcarLlegida(Long alertaId) throws NoTrobatException;
	public AlertaDto marcarNoLlegida(Long alertaId) throws NoTrobatException;;
	public AlertaDto marcarEsborrada(Long alertaId) throws NoTrobatException;;
}
