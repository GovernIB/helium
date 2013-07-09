/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.CampNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TascaService {

	/**
	 * Retorna les dades d'una instància de tasca.
	 * 
	 * @param tascaId
	 * @return
	 * @throws TaskInstanceNotFoundException
	 */
	public List<TascaDadaDto> findDadesPerTasca(
			String tascaId) throws TascaNotFoundException;

	/**
	 * Consulta els possibles valors per a un camp de tipus selecció
	 * del formulari de la tasca.
	 * 
	 * @param tascaId
	 * @param campId
	 * @return
	 * @throws TaskInstanceNotFoundException
	 * @throws CampNotFoundException
	 */
	public List<SeleccioOpcioDto> findOpcionsSeleccioPerCampTasca(
			String tascaId,
			Long campId) throws TaskInstanceNotFoundException, CampNotFoundException;

}
