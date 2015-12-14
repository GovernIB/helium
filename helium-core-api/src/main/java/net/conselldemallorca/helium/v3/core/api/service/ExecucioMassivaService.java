/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaService {

	public void crearExecucioMassiva(ExecucioMassivaDto dto) throws Exception;
	
	public Object deserialize(byte[] bytes) throws Exception;
	
	public byte[] serialize(Object obj) throws Exception;

	public void cancelarExecucio(Long id) throws Exception;

	public String getJsonExecucionsMassivesByUser(int numResults, boolean viewAll);
}
