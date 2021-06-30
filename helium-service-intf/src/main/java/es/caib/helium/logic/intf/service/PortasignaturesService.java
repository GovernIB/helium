/**
 * 
 */
package es.caib.helium.logic.intf.service;

import es.caib.helium.logic.intf.dto.PortasignaturesDto;


/**
 * Servei per gestionar el callback del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortasignaturesService {

	/**
	 * Mètode per processar íntegrament el callback del portasignatures per acceptar
	 * o rebutjar documents i actualitzar la informació de les peticions al portasignatures.
	 * 
	 * @param documentId
	 * @param rebujat
	 * @param motiuRebuig
	 * @return
	 */
	public boolean processarDocumentCallback(
			Integer documentId,
			boolean rebujat,
			String motiuRebuig);

	/** Mètode per consultar la informació guardada d'una petició al portasignatura
	 * a partir del seu documentId.
	 * @param documentId
	 * @return
	 */
	public PortasignaturesDto getByDocumentId(Integer documentId);
}
