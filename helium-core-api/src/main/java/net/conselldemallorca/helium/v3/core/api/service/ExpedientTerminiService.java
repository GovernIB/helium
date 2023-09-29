/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.CampFormProperties;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;

/**
 * Servei encarregat de gestionar els terminis dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTerminiService {

	/**
	 * Inicia un termini a un procés d'un expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param terminiId
	 *            Atribut id del termini.
	 * @param data
	 *            Data per a iniciar el termini.
	 * @param esDataFi
	 *            Indica que la data és la data de final i no la d'inici.
	 * @return el termini iniciat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public TerminiIniciatDto iniciar(
			Long expedientId,
			String processInstanceId,
			Long terminiId,
			Date data,
			boolean esDataFi) throws NoTrobatException, PermisDenegatException;

	/**
	 * Modifica un termini iniciat.
	 *  
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param terminiIniciatId
	 *            Atribut id del termini iniciat.
	 * @param data
	 *            Data per a modificar el termini.
	 * @param anys
	 *            Anys del termini.
	 * @param mesos
	 *            Mesos del termini.
	 * @param dies
	 *            Dies del termini.
	 * @param esDataFi
	 *            Indica que la data és la data de final i no la d'inici.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public void modificar(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date inicio,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) throws NoTrobatException, PermisDenegatException;

	/**
	 * Suspen un termini iniciat.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param terminiIniciatId
	 *            Atribut id del termini iniciat.
	 * @param data
	 *            Data de suspensió del termini.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public void suspendre(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date data) throws NoTrobatException, PermisDenegatException;

	/**
	 * Repren un termini en pausa.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param terminiIniciatId
	 *            Atribut id del termini iniciat.
	 * @param data
	 *            Data de represa del termini.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public void reprendre(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date data) throws NoTrobatException, PermisDenegatException;

	/**
	 * Cancela un termini iniciat.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param terminiIniciatId
	 *            Atribut id del termini iniciat.
	 * @param data
	 *            Data de cancelació del termini.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public void cancelar(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId,
			Date data) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els terminis definits a una instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @return la llista de terminis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public List<TerminiDto> findAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els terminis iniciats d'una instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @return la llista de terminis iniciats.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public List<TerminiIniciatDto> iniciatFindAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId);

	/**
	 * Retorna un termini iniciat d'una instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param terminiIniciatId
	 *            Atribut id del termini iniciat.
	 * @return el termini iniciat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public TerminiIniciatDto iniciatFindAmbId(
			Long expedientId,
			String processInstanceId,
			Long terminiIniciatId) throws NoTrobatException;

	public List<FestiuDto> festiuFindAmbAny(
			int any);

	public void festiuCreate(
			String data) throws Exception;

	public void festiuDelete(
			String data) throws ValidacioException, Exception;

    public Map<String, CampFormProperties> getTerminisFormProperties(Long expedientTipusId, String estatCodi);
}
