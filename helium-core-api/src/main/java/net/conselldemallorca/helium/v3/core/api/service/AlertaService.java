package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;



public interface AlertaService {
	
	public static final int ALERTAS_NO_LLEGIDES = 0;
	public static final int ALERTAS_LLEGIDES = 1;
	public static final int ALERTAS_TODAS = 2;

	public AlertaDto marcarLlegida(Long alertaId) throws NoTrobatException;
	public AlertaDto marcarNoLlegida(Long alertaId) throws NoTrobatException;;
	public AlertaDto marcarEsborrada(Long alertaId) throws NoTrobatException;;
}
