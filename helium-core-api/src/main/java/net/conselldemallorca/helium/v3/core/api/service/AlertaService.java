/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;

/**
 * Servei de gestió d'alertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AlertaService {

	public int countActivesAmbEntornIUsuari(
			Long entornId,
			String usuariCodi,
			boolean llegides,
			boolean noLlegides);

	public AlertaDto marcarLlegida(Long alertaId) throws NoTrobatException;

	public AlertaDto marcarNoLlegida(Long alertaId) throws NoTrobatException;

	public AlertaDto marcarEsborrada(Long alertaId) throws NoTrobatException;

}
