/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;


/**
 * Servei per consultar dades de reindexació com els expedients amb error o pendents
 * de reindexació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientReindexacioService {


	/** Consulta el número d'expedients penedents de reindexació no anul·lats per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 */
	public Long consultaCountPendentsReindexacio(long expedientTipusId);

	/** Consulta el número d'expedients amb error de reindexació no anul·lats per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 */
	public Long consultaCountErrorsReindexacio(long expedientTipusId);

	/** Consulta els identificadors dels expedients amb error de reindexació no anul·lats per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 */
	public List<Long> consultaIdsErrorReindexació(long expedientTipusId);

	/** Consulta els identificadors dels expedients pendents de reindexació no anul·lats per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 */
	public List<Long> consultaIdsPendentReindexació(long expedientTipusId);

	/** Consulta els identificadors dels expedients amb error o pendents de reindexació.
	 * 
	 * @param expedientTipusId Tipus d'expedient per filtrar
	 * @return Llistat d'identificadors.
	 */
	public List<Long> consultaIdsExpedients(long expedientTipusId);

	
	/** Mètode per consultar el número d'expedients diferents pendents de reindexació. */
	public Long countPendentReindexacioAsincrona();

	/** Mètode per consultar les dades de reindexació en forma de llista per tipus d'expedient.
	 * Pot filtrar per entorn per usuaris no administradors.
	 * 
	 * @return Llista List<ExpedientTipusDto, Long errors, Long reindexacions>
	 */
	public List<Object[]> getDades(Long entornId);
}
