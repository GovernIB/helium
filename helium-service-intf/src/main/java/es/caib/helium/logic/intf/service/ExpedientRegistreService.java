/**
 * 
 */
package es.caib.helium.logic.intf.service;

import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.InformacioRetroaccioDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;


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
	public SortedSet<Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>> findInformacioRetroaccioExpedientOrdenatPerData(
			Long expedientId,
			boolean detall) throws NoTrobatException, PermisDenegatException;

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
	public Map<String, ExpedientTascaDto> findTasquesExpedientPerRetroaccio(
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
	public void executaRetroaccio(
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
	public void eliminaInformacioRetroaccio(
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
	public List<InformacioRetroaccioDto> findInformacioRetroaccioTascaOrdenatPerData(
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
	public List<InformacioRetroaccioDto> findInformacioRetroaccioAccioRetrocesOrdenatsPerData(
			Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna la informació d'un registre de log de l'expedient.
	 * 
	 * @param logId
	 *            Atribut id del log d'expedient.
	 * @return la informació del log.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public InformacioRetroaccioDto findInformacioRetroaccioById(
			//Long expedientId,
			Long logId) throws NoTrobatException, PermisDenegatException;

}
