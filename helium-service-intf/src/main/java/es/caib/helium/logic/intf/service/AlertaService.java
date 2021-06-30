package es.caib.helium.logic.intf.service;

import es.caib.helium.logic.intf.dto.AlertaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;



public interface AlertaService {
	
	public static final int ALERTAS_NO_LLEGIDES = 0;
	public static final int ALERTAS_LLEGIDES = 1;
	public static final int ALERTAS_TODAS = 2;

	public AlertaDto marcarLlegida(Long alertaId) throws NoTrobatException;
	public AlertaDto marcarNoLlegida(Long alertaId) throws NoTrobatException;;
	public AlertaDto marcarEsborrada(Long alertaId) throws NoTrobatException;;
}
