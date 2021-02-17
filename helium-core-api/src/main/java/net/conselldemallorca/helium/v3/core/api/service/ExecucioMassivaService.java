/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.exception.ExecucioMassivaException;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;

/**
 * Servei per a gestionar l'execució de les accions massives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public interface ExecucioMassivaService {

	public void crearExecucioMassiva(ExecucioMassivaDto dto) throws NoTrobatException, ValidacioException;
	
	public Object deserialize(byte[] bytes);
	
	public byte[] serialize(Object obj);

	/** Mètode per cancelar totes les execucions massives d'expedients per a una execució massiva
	 * 
	 * @param id Identificador de l'execució massiva.
	 * @return Retorna el número d'execucions massives d'expedients en estat pendent cancel·lats.
	 */
	public int cancelarExecucioMassiva(Long id);
	
	
	/**
	 * Mètode per rependre totes les execucions massives d'expediens per una execució massiva prèviament cancel·lada.
	 * @param id
	 */
	public void rependreExecucioMassiva(Long id);
	
	void rependreExecucioMassivaExpedient(Long id);

	/** Cancel·la l'execució d'una execució massiva d'expedient específica
	 * 
	 * @param id
	 * @throws ValidacioException
	 */
	public void cancelarExecucioMassivaExpedient(Long id) throws ValidacioException;

	/** Per obtenir les dades JSON de les execucions massives
	 * 
	 * @param numResults Número de pàgina començant per la 0 i retornant 10 resultats.
	 * @param nivell Per indicar si veure dades a nivell d'usuari o a nivell d'administrador. Possibles valors "admin" o "user"
	 * @return
	 * @throws NoTrobatException
	 */
	public String getJsonExecucionsMassives(int numResults, String nivell) throws NoTrobatException;
	
	public String getExecucioMassivaDetall(Long execucioMassivaId) throws NoTrobatException, SistemaExternException;
	
	public Long getExecucionsMassivesActiva(Long ultimaExecucioMassiva);
	
	public void executarExecucioMassiva(Long ome_id) throws NoTrobatException, ValidacioException, ExecucioMassivaException;
	
	public void generaInformeError(Long ome_id, String error) throws NoTrobatException;
	
	public void actualitzaUltimaOperacio(Long ome_id) throws NoTrobatException, ExecucioMassivaException;
}
