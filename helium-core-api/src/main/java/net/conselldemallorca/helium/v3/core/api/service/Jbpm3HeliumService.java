/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.DefinicioProcesNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentGenerarException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.PersonaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiIniciatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiNotFoundException;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface Jbpm3HeliumService {

	/**
	 * Obté l'entorn actual.
	 * 
	 * @return l'entorn actual.
	 */
	public EntornDto getEntornActual();

	/**
	 * Obté l'expedient que s'està iniciant.
	 * 
	 * @return l'expedient que s'està iniciant o null no n'hi ha. 
	 */
	public ExpedientDto getExpedientIniciant();

	/**
	 * 
	 * @param executionContext
	 * @param expedientTipusCodi
	 * @param numero
	 * @return
	 */
	public ExpedientDto getExpedientAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero);

	/**
	 * Obté la definició de procés donada la seva clau jBPM i la versió.
	 * 
	 * @param jbpmKey la clau jBPM.
	 * @param version la versió.
	 * @return La definició de procés o null si no s'ha trobat.
	 */
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIVersio(
			String jbpmKey,
			int version);

	/**
	 * Obté la darrera versió d'una definició de procés donat el
	 * seu entorn i la clau jBPM.
	 * 
	 * @param entornId
	 * @param jbpmKey
	 * @return
	 */
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey);

	/**
	 * Obté la definició de procés donada una instància de procés.
	 * 
	 * @param processInstanceId Id de la instància de procés.
	 * @return la definició de procés.
	 */
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(
			String processInstanceId);

	/**
	 * Obté la informació d'una persona donat el seu codi.
	 * 
	 * @param codi El codi de la persona.
	 * @return La informació de la persona.
	 */
	public PersonaDto getPersonaAmbCodi(String codi);

	/**
	 * 
	 * @param entornId
	 * @param codi
	 * @return
	 */
	public AreaDto getAreaAmbEntornICodi(
			Long entornId,
			String codi);

	/**
	 * 
	 * @param entornId
	 * @param areaCodi
	 * @param carrecCodi
	 * @return
	 */
	public CarrecDto getCarrecAmbEntornIAreaICodi(
			Long entornId,
			String areaCodi,
			String carrecCodi);

	/**
	 * 
	 * @return
	 */
	public List<FestiuDto> findFestiusAll();

	/**
	 * 
	 * @param usuariCodi
	 * @return
	 */
	public ReassignacioDto findReassignacioActivaPerUsuari(String usuariCodi);

	/**
	 * 
	 * @param taskInstanceId
	 */
	public void esborrarAlertaAmbTaskInstanceId(long taskInstanceId);

	/**
	 * 
	 * @return
	 */
	public String getCurrentUserName();

	/**
	 * 
	 * @param expedientId
	 * @param estatCodi
	 * @throws ExpedientNotFoundException
	 * @throws EstatNotFoundException
	 */
	public void expedientModificarEstat(
			Long expedientId,
			String estatCodi) throws ExpedientNotFoundException, EstatNotFoundException;

	/**
	 * 
	 * @param expedientId
	 * @param comentari
	 * @throws ExpedientNotFoundException
	 */
	public void expedientModificarComentari(
			Long expedientId,
			String comentari) throws ExpedientNotFoundException;

	/**
	 * 
	 * @param expedientId
	 * @param posx
	 * @param posy
	 * @param referencia
	 * @throws ExpedientNotFoundException
	 */
	public void expedientModificarGeoref(
			Long expedientId,
			String posx, String posy, String referencia) throws ExpedientNotFoundException;

	/**
	 * 
	 * @param expedientId
	 * @param grupCodi
	 * @throws ExpedientNotFoundException
	 */
	public void expedientModificarGrup(
			Long expedientId,
			String grupCodi) throws ExpedientNotFoundException;

	/**
	 * 
	 * @param expedientId
	 * @param numero
	 * @throws ExpedientNotFoundException
	 */
	public void expedientModificarNumero(
			Long expedientId,
			String numero) throws ExpedientNotFoundException;

	/**
	 * 
	 * @param expedientId
	 * @param responsableCodi
	 * @throws ExpedientNotFoundException
	 * @throws PersonaNotFoundException
	 */
	public void expedientModificarResponsable(
			Long expedientId,
			String responsableCodi) throws ExpedientNotFoundException, PersonaNotFoundException;

	/**
	 * 
	 * @param expedientId
	 * @param titol
	 * @throws ExpedientNotFoundException
	 */
	public void expedientModificarTitol(
			Long expedientId,
			String titol) throws ExpedientNotFoundException;

	/**
	 * 
	 * @param expedientId
	 * @param motiu
	 * @throws ExpedientNotFoundException
	 */
	public void expedientAturar(
			Long expedientId,
			String motiu) throws ExpedientNotFoundException;

	/**
	 * 
	 * @param expedientId
	 * @throws ExpedientNotFoundException
	 */
	public void expedientReprendre(Long expedientId) throws ExpedientNotFoundException;

	/**
	 * 
	 * @param processInstanceId
	 * @param documentCodi
	 * @param dataDocument
	 * @param forsarAdjuntarAuto
	 * @throws DocumentNotFoundException
	 * @throws PlantillaNotFoundException
	 */
	public ArxiuDto generarAmbPlantilla(
			String processInstanceId,
			String documentCodi,
			Date dataDocument,
			boolean forsarAdjuntarAuto) throws DefinicioProcesNotFoundException, DocumentNotFoundException, DocumentGenerarException;

	/**
	 * 
	 * @param processInstanceId
	 * @param terminiCodi
	 * @return
	 * @throws TerminiNotFoundException
	 */
	public TerminiIniciatDto getTerminiIniciat(
			String processInstanceId,
			String terminiCodi) throws TerminiNotFoundException;

	/**
	 * 
	 * @param terminiIniciatId
	 * @param taskInstanceId
	 * @param timerId
	 * @throws TerminiIniciatNotFoundException
	 */
	public void configurarTerminiIniciatAmbDadesJbpm(
			Long terminiIniciatId,
			String taskInstanceId,
			Long timerId) throws TerminiIniciatNotFoundException;

	/**
	 * 
	 * @param processInstanceId
	 * @param dominiCodi
	 * @param dominiId
	 * @param parametres
	 * @return
	 */
	public List<DominiRespostaFilaDto> dominiConsultar(
			String processInstanceId,
			String dominiCodi,
			String dominiId,
			Map<String, Object> parametres);

	/**
	 * 
	 * @param processInstanceId
	 * @param enumeracioCodi
	 * @return
	 */
	public List<EnumeracioValorDto> enumeracioConsultar(
			String processInstanceId,
			String enumeracioCodi);

	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	public String getHeliumProperty(String propertyName);

}
