/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;

/**
 * Servei per a gestionar l'execució de les accions massives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaService {

	public void crearExecucioMassiva(ExecucioMassivaDto dto) throws Exception;
	
	public Object deserialize(byte[] bytes) throws Exception;
	
	public byte[] serialize(Object obj) throws Exception;

	public void cancelarExecucio(Long id) throws Exception;

	public String getJsonExecucionsMassivesByUser(int numResults, boolean viewAll);
	
	public String getExecucioMassivaDetall(Long execucioMassivaId);
	
	public Long getExecucionsMassivesActiva(Long ultimaExecucioMassiva);
	
	public void executarExecucioMassiva(Long ome_id) throws Exception;
	
	public void generaInformeError(Long ome_id, String error);
	
	public void actualitzaUltimaOperacio(Long ome_id);
}
