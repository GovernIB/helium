/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;


/**
 * Servei encarregat de gestionar el registre d'accions realitzades
 * a damunt un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientRegistreService {

	/**
	 * Consulta els logs d'un expedient ordenats per data.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param detall
	 *            Indica si s'ha de retornar la informació detallada o no.
	 * @return els logs de l'expedient organitzats per instància de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public SortedSet<Entry<InstanciaProcesDto, List<ExpedientLogDto>>> registreFindLogsOrdenatsPerData(
			Long expedientId,
			boolean detall) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Consulta els canvis d'estat d'un expedient ordenats per data.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @return els logs de l'expedient pels canvis d'estat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 */
	public List<ExpedientLogDto> registreFindExpedientCanvisEstat(
			Long expedientId) throws NoTrobatException;
	/**
	 * Obté les tasques associades als logs de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @return el llistat de tasques.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public Map<String, ExpedientTascaDto> registreFindTasquesPerLogExpedient(
			Long expedientId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Fa un retrocés de l'expedient de totes les modificacions fetes a partir
	 * del log especificat.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param logId
	 *            Atribut id del log d'expedient.
	 * @param retrocedirPerTasques
	 *            Indica si el retrocés es per tasques.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void registreRetrocedir(
			Long expedientId,
			Long logId,
			boolean retrocedirPerTasques) throws NoTrobatException, PermisDenegatException;

	/**
	 * Elimina tots els logs associats a un expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void registreBuidarLog(
			Long expedientId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els logs associats a una tasca de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param logId
	 *            Atribut id del log d'expedient.
	 * @return la llista de logs.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientLogDto> registreFindLogsTascaOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Obté els logs associats a una acció de retrocés ordenats per data.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param logId
	 *            Atribut id del log d'expedient.
	 * @return la llista de logs.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientLogDto> registreFindLogsRetroceditsOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna la informació d'un registre de log de l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param logId
	 *            Atribut id del log d'expedient.
	 * @return la informació del log.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientLogDto registreFindLogById(
			//Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException;

}
