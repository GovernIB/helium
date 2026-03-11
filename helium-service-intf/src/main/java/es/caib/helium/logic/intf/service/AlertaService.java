package es.caib.helium.logic.intf.service;

import es.caib.helium.commons.dto.AlertaDto;
import es.caib.helium.commons.exception.NoTrobatException;



public interface AlertaService {
	public AlertaDto marcarLlegida(Long alertaId) throws NoTrobatException;
	public AlertaDto marcarNoLlegida(Long alertaId) throws NoTrobatException;;
	public AlertaDto marcarEsborrada(Long alertaId) throws NoTrobatException;;
}
