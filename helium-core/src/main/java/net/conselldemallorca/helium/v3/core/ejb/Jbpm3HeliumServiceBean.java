/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.OperacioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperExpedientDto;
import net.conselldemallorca.helium.v3.core.api.exception.AreaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.DefinicioProcesNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentGenerarException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.DominiConsultaException;
import net.conselldemallorca.helium.v3.core.api.exception.DominiNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EnumeracioNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.PersonaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.PluginException;
import net.conselldemallorca.helium.v3.core.api.exception.ProcessInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiIniciatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService.FiltreAnulat;
import net.conselldemallorca.helium.v3.core.api.service.Jbpm3HeliumService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
/**
 * @author sion
 * 
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class Jbpm3HeliumServiceBean implements Jbpm3HeliumService {
	@Autowired
	Jbpm3HeliumService delegate;

	/**
	 * Onte el codi d'usuari actual (autenticat).
	 * 
	 * @return el codi de l'usuari.
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getUsuariCodiActual() {
		return delegate.getUsuariCodiActual();
	}

	/**
	 * Obté l'entorn actual.
	 * 
	 * @return l'entorn actual.
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EntornDto getEntornActual() {
		return delegate.getEntornActual();
	}

	/**
	 * Obté l'expedient que s'està iniciant.
	 * 
	 * @return l'expedient que s'està iniciant o null no n'hi ha.
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto getExpedientIniciant() {
		return delegate.getExpedientIniciant();
	}

	/**
	 * Obté l'expedient donat l'entorn, el tipus d'expedient i el seu número.
	 * 
	 * @param entornId
	 * @param expedientTipusCodi
	 * @param numero
	 * @return
	 * @throws EntornNotFoundException
	 * @throws ExpedientTipusNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDto getExpedientAmbEntornITipusINumero(Long entornId, String expedientTipusCodi, String numero) throws EntornNotFoundException, ExpedientTipusNotFoundException {
		return delegate.getExpedientAmbEntornITipusINumero(entornId, expedientTipusCodi, numero);
	}

	/**
	 * Obté l'expedient arrel donada una instància de procés jBPM.
	 * 
	 * @param executionContext
	 * @param expedientTipusCodi
	 * @param numero
	 * @return
	 * @throws ProcessInstanceNotFoundException
	 */
	public ExpedientDto getExpedientArrelAmbProcessInstanceId(String processInstanceId) throws ProcessInstanceNotFoundException {
		return delegate.getExpedientArrelAmbProcessInstanceId(processInstanceId);
	}

	/**
	 * Obté l'entorn donada una instància de procés jBPM.
	 * 
	 * @param processInstanceId
	 * @return
	 * @throws ProcessInstanceNotFoundException
	 */
	public EntornDto getEntornAmbProcessInstanceId(String processInstanceId) throws ProcessInstanceNotFoundException {
		return delegate.getEntornAmbProcessInstanceId(processInstanceId);
	}

	/**
	 * Obté la definició de procés donada la seva clau jBPM i la versió.
	 * 
	 * @param jbpmKey
	 *            la clau jBPM.
	 * @param version
	 *            la versió.
	 * @return La definició de procés o null si no s'ha trobat.
	 */
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIVersio(String jbpmKey, int version) {
		return delegate.getDefinicioProcesAmbJbpmKeyIVersio(jbpmKey, version);
	}

	/**
	 * Obté la darrera versió d'una definició de procés donat el seu entorn i la clau jBPM.
	 * 
	 * @param entornId
	 * @param jbpmKey
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(Long entornId, String jbpmKey) throws EntornNotFoundException {
		return delegate.getDarreraVersioAmbEntornIJbpmKey(entornId, jbpmKey);
	}

	/**
	 * Obté la definició de procés donada una instància de procés.
	 * 
	 * @param processInstanceId
	 *            Id de la instància de procés.
	 * @return la definició de procés.
	 * @throws ProcessInstanceNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(String processInstanceId) throws ProcessInstanceNotFoundException {
		return delegate.getDefinicioProcesPerProcessInstanceId(processInstanceId);
	}

	/**
	 * Obté la informació d'una persona donat el seu codi.
	 * 
	 * @param codi
	 *            El codi de la persona.
	 * @return La informació de la persona o null si no existeix.
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PersonaDto getPersonaAmbCodi(String codi) {
		return delegate.getPersonaAmbCodi(codi);
	}

	/**
	 * Obté l'àrea donat un entorn i el codi.
	 * 
	 * @param entornId
	 * @param codi
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AreaDto getAreaAmbEntornICodi(Long entornId, String codi) throws EntornNotFoundException {
		return delegate.getAreaAmbEntornICodi(entornId, codi);
	}

	/**
	 * Obté el càrrec donat un entorn, el codi d'àrea i el codi de càrrec.
	 * 
	 * @param entornId
	 * @param areaCodi
	 * @param carrecCodi
	 * @return
	 * @throws EntornNotFoundException
	 * @throws AreaNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CarrecDto getCarrecAmbEntornIAreaICodi(Long entornId, String areaCodi, String carrecCodi) throws EntornNotFoundException, AreaNotFoundException {
		return delegate.getCarrecAmbEntornIAreaICodi(entornId, areaCodi, carrecCodi);
	}

	/**
	 * Obté el llistat de tots els festius.
	 * 
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FestiuDto> findFestiusAll() {
		return delegate.findFestiusAll();
	}

	/**
	 * 
	 * @param usuariCodi
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(String usuariCodi) {
		return delegate.findReassignacioActivaPerUsuariOrigen(usuariCodi);
	}

	/**
	 * Crea una alerta.
	 * 
	 * @param entornId
	 * @param expedientId
	 * @param data
	 * @param usuariCodi
	 * @param text
	 * @throws EntornNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void alertaCrear(Long entornId, Long expedientId, Date data, String usuariCodi, String text) throws EntornNotFoundException, ExpedientNotFoundException {
		delegate.alertaCrear(entornId, expedientId, data, usuariCodi, text);
	}

	/**
	 * Esborra totes les alertes relacionades amb una determinada instància de tasca.
	 * 
	 * @param taskInstanceId
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId) {
		delegate.alertaEsborrarAmbTaskInstanceId(taskInstanceId);
	}

	/**
	 * Modifica l'estat de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param estatCodi
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 * @throws EstatNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientModificarEstat(String processInstanceId, String estatCodi) throws ProcessInstanceNotFoundException, ExpedientNotFoundException, EstatNotFoundException {
		delegate.expedientModificarEstat(processInstanceId, estatCodi);
	}

	/**
	 * Modifica el comentari de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param comentari
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientModificarComentari(String processInstanceId, String comentari) throws ProcessInstanceNotFoundException, ExpedientNotFoundException {
		delegate.expedientModificarComentari(processInstanceId, comentari);
	}

	/**
	 * Modifica la georeferència de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param posx
	 * @param posy
	 * @param referencia
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientModificarGeoref(String processInstanceId, Double posx, Double posy, String referencia) throws ProcessInstanceNotFoundException, ExpedientNotFoundException {
		delegate.expedientModificarGeoref(processInstanceId, posx, posy, referencia);
	}

	/**
	 * Modifica el grup de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param grupCodi
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientModificarGrup(String processInstanceId, String grupCodi) throws ProcessInstanceNotFoundException, ExpedientNotFoundException {
		delegate.expedientModificarGrup(processInstanceId, grupCodi);
	}

	/**
	 * Modifica el número de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param numero
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientModificarNumero(String processInstanceId, String numero) throws ProcessInstanceNotFoundException, ExpedientNotFoundException {
		delegate.expedientModificarNumero(processInstanceId, numero);
	}

	/**
	 * Modifica el responsable de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param responsableCodi
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 * @throws PersonaNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientModificarResponsable(String processInstanceId, String responsableCodi) throws ProcessInstanceNotFoundException, ExpedientNotFoundException, PersonaNotFoundException {
		delegate.expedientModificarResponsable(processInstanceId, responsableCodi);
	}

	/**
	 * Modifica el titol de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param titol
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientModificarTitol(String processInstanceId, String titol) throws ProcessInstanceNotFoundException, ExpedientNotFoundException {
		delegate.expedientModificarTitol(processInstanceId, titol);
	}

	/**
	 * Atura la tramitació de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param motiu
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientAturar(String processInstanceId, String motiu) throws ProcessInstanceNotFoundException, ExpedientNotFoundException {
		delegate.expedientAturar(processInstanceId, motiu);
	}

	/**
	 * Repren la tramitació d'un expedient aturat.
	 * 
	 * @param processInstanceId
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientReprendre(String processInstanceId) throws ProcessInstanceNotFoundException, ExpedientNotFoundException {
		delegate.expedientReprendre(processInstanceId);
	}

	/**
	 * Reindexa l'expedient.
	 * 
	 * @param processInstanceId
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientReindexar(String processInstanceId) throws ProcessInstanceNotFoundException, ExpedientNotFoundException {
		delegate.expedientReindexar(processInstanceId);
	}

	/**
	 * Genera un document a partir d'una plantilla.
	 * 
	 * @param taskInstanceId
	 * @param processInstanceId
	 * @param documentCodi
	 * @param dataDocument
	 * @param forsarAdjuntarAuto
	 * @return
	 * @throws DefinicioProcesNotFoundException
	 * @throws DocumentNotFoundException
	 * @throws DocumentGenerarException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto documentGenerarAmbPlantilla(String taskInstanceId, String processInstanceId, String documentCodi, Date dataDocument, boolean forsarAdjuntarAuto) throws DefinicioProcesNotFoundException, DocumentNotFoundException, DocumentGenerarException {
		return delegate.documentGenerarAmbPlantilla(taskInstanceId, processInstanceId, documentCodi, dataDocument, forsarAdjuntarAuto);
	}

	/**
	 * Obté el termini donada una instància de procés i el codi del termini.
	 * 
	 * @param processInstanceId
	 * @param terminiCodi
	 * @return
	 * @throws ProcessInstanceNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiDto getTerminiAmbDefinicioProcesICodi(String processInstanceId, String terminiCodi) throws ProcessInstanceNotFoundException {
		return delegate.getTerminiAmbDefinicioProcesICodi(processInstanceId, terminiCodi);
	}

	/**
	 * Obté el termini iniciat donada una instància de procés i el codi del termini.
	 * 
	 * @param processInstanceId
	 * @param terminiCodi
	 * @return
	 * @throws ProcessInstanceNotFoundException
	 * @throws TerminiNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(String processInstanceId, String terminiCodi) throws ProcessInstanceNotFoundException, TerminiNotFoundException {
		return delegate.getTerminiIniciatAmbProcessInstanceITerminiCodi(processInstanceId, terminiCodi);
	}

	/**
	 * Configura una tasca o un timer amb un termini iniciat.
	 * 
	 * @param terminiIniciatId
	 * @param taskInstanceId
	 * @param timerId
	 * @throws TerminiIniciatNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void configurarTerminiIniciatAmbDadesJbpm(Long terminiIniciatId, String taskInstanceId, Long timerId) throws TerminiIniciatNotFoundException {
		delegate.configurarTerminiIniciatAmbDadesJbpm(terminiIniciatId, taskInstanceId, timerId);
	}

	/**
	 * Calcula la data d'inici d'un termini donada la data de fi.
	 * 
	 * @param fi
	 * @param anys
	 * @param mesos
	 * @param dies
	 * @param laborable
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Date terminiCalcularDataInici(Date fi, int anys, int mesos, int dies, boolean laborable) {
		return delegate.terminiCalcularDataInici(fi, anys, mesos, dies, laborable);
	}

	/**
	 * Calcula la data de fi d'un termini donada la data d'inici.
	 * 
	 * @param inici
	 * @param anys
	 * @param mesos
	 * @param dies
	 * @param laborable
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Date terminiCalcularDataFi(Date inici, int anys, int mesos, int dies, boolean laborable) {
		return delegate.terminiCalcularDataFi(inici, anys, mesos, dies, laborable);
	}

	/**
	 * Inicia un termini donat un interval.
	 * 
	 * @param terminiCodi
	 * @param processInstanceId
	 * @param inici
	 * @param anys
	 * @param mesos
	 * @param dies
	 * @param esDataFi
	 * @throws TerminiNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void terminiIniciar(String terminiCodi, String processInstanceId, Date inici, int anys, int mesos, int dies, boolean esDataFi) throws TerminiNotFoundException {
		delegate.terminiIniciar(terminiCodi, processInstanceId, inici, anys, mesos, dies, esDataFi);
	}

	/**
	 * Inicia un termini amb l'interval que tengui configurat.
	 * 
	 * @param terminiCodi
	 * @param processInstanceId
	 * @param data
	 * @param esDataFi
	 * @throws TerminiNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void terminiIniciar(String terminiCodi, String processInstanceId, Date data, boolean esDataFi) throws TerminiNotFoundException {
		delegate.terminiIniciar(terminiCodi, processInstanceId, data, esDataFi);
	}

	/**
	 * Cancela un termini iniciat.
	 * 
	 * @param terminiIniciatId
	 * @param data
	 * @throws TerminiIniciatNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void terminiCancelar(Long terminiIniciatId, Date data) throws TerminiIniciatNotFoundException {
		delegate.terminiCancelar(terminiIniciatId, data);
	}

	/**
	 * Posa un termini iniciat en pausa.
	 * 
	 * @param terminiIniciatId
	 * @param data
	 * @throws TerminiIniciatNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void terminiPausar(Long terminiIniciatId, Date data) throws TerminiIniciatNotFoundException {
		delegate.terminiPausar(terminiIniciatId, data);
	}

	/**
	 * Continua un termini pausat.
	 * 
	 * @param terminiIniciatId
	 * @param data
	 * @throws TerminiIniciatNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void terminiContinuar(Long terminiIniciatId, Date data) throws TerminiIniciatNotFoundException {
		delegate.terminiContinuar(terminiIniciatId, data);
	}

	/**
	 * Consulta un domini i retorna el resultat.
	 * 
	 * @param processInstanceId
	 * @param dominiCodi
	 * @param dominiId
	 * @param parametres
	 * @return
	 * @throws ExpedientNotFoundException
	 * @throws DominiNotFoundException
	 * @throws DominiConsultaException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DominiRespostaFilaDto> dominiConsultar(String processInstanceId, String dominiCodi, String dominiId, Map<String, Object> parametres) throws ExpedientNotFoundException, DominiNotFoundException, DominiConsultaException {
		return delegate.dominiConsultar(processInstanceId, dominiCodi, dominiId, parametres);
	}

	/**
	 * Retorna tots els valors d'una enumeració.
	 * 
	 * @param processInstanceId
	 * @param enumeracioCodi
	 * @return
	 * @throws ExpedientNotFoundException
	 * @throws EnumeracioNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EnumeracioValorDto> enumeracioConsultar(String processInstanceId, String enumeracioCodi) throws ExpedientNotFoundException, EnumeracioNotFoundException {
		return delegate.enumeracioConsultar(processInstanceId, enumeracioCodi);
	}

	/**
	 * Retorna els camps del formulari d'una tasca.
	 * 
	 * @param taskInstanceId
	 * @return
	 * @throws TaskInstanceNotFoundException
	 * @throws DefinicioProcesNotFoundException
	 * @throws TascaNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampTascaDto> findCampsPerTaskInstance(long taskInstanceId) throws TaskInstanceNotFoundException, DefinicioProcesNotFoundException, TascaNotFoundException {
		return delegate.findCampsPerTaskInstance(taskInstanceId);
	}

	/**
	 * Retorna els documents d'una tasca.
	 * 
	 * @param taskInstanceId
	 * @return
	 * @throws TaskInstanceNotFoundException
	 * @throws DefinicioProcesNotFoundException
	 * @throws TascaNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentTascaDto> findDocumentsPerTaskInstance(long taskInstanceId) throws TaskInstanceNotFoundException, DefinicioProcesNotFoundException, TascaNotFoundException {
		return delegate.findDocumentsPerTaskInstance(taskInstanceId);
	}

	/**
	 * Retorna el codi de variable jBPM per un codi de document.
	 * 
	 * @param documentCodi
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getCodiVariablePerDocumentCodi(String documentCodi) {
		return delegate.getCodiVariablePerDocumentCodi(documentCodi);
	}

	/**
	 * Obté informació d'un document d'un expedient d'Helium.
	 * 
	 * @param documentCodi
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto getDocumentInfo(Long documentStoreId) {
		return delegate.getDocumentInfo(documentStoreId);
	}

	/**
	 * Obté l'arxiu d'un document d'un expedient d'Helium.
	 * 
	 * @param documentStoreId
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getArxiuPerMostrar(Long documentStoreId) {
		return delegate.getArxiuPerMostrar(documentStoreId);
	}

	/**
	 * 
	 * @param processInstanceId
	 * @param documentCodi
	 * @param data
	 * @param arxiuNom
	 * @param arxiuContingut
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long documentExpedientGuardar(String processInstanceId, String documentCodi, Date data, String arxiuNom, byte[] arxiuContingut) {
		return delegate.documentExpedientGuardar(processInstanceId, documentCodi, data, arxiuNom, arxiuContingut);
	}

	/**
	 * 
	 * @param processInstanceId
	 * @param adjuntId
	 * @param adjuntTitol
	 * @param adjuntData
	 * @param arxiuNom
	 * @param arxiuContingut
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long documentExpedientAdjuntar(String processInstanceId, String adjuntId, String adjuntTitol, Date adjuntData, String arxiuNom, byte[] arxiuContingut) {
		return delegate.documentExpedientAdjuntar(processInstanceId, adjuntId, adjuntTitol, adjuntData, arxiuNom, arxiuContingut);
	}

	/**
	 * 
	 * @param taskInstanceId
	 * @param processInstanceId
	 * @param documentCodi
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void documentExpedientEsborrar(String taskInstanceId, String processInstanceId, String documentCodi) {
		delegate.documentExpedientEsborrar(taskInstanceId, processInstanceId, documentCodi);
	}

	/**
	 * 
	 * @param documentStoreId
	 * @param registreNumero
	 * @param registreData
	 * @param registreOficinaCodi
	 * @param registreOficinaNom
	 * @param registreEntrada
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void documentExpedientGuardarDadesRegistre(Long documentStoreId, String registreNumero, Date registreData, String registreOficinaCodi, String registreOficinaNom, boolean registreEntrada) {
		delegate.documentExpedientGuardarDadesRegistre(documentStoreId, registreNumero, registreData, registreOficinaCodi, registreOficinaNom, registreEntrada);
	}

	/**
	 * 
	 * @param fromAddress
	 * @param recipients
	 * @param ccRecipients
	 * @param bccRecipients
	 * @param subject
	 * @param text
	 * @param attachments
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void emailSend(String fromAddress, List<String> recipients, List<String> ccRecipients, List<String> bccRecipients, String subject, String text, List<ArxiuDto> attachments) throws PluginException {
		delegate.emailSend(fromAddress, recipients, ccRecipients, bccRecipients, subject, text, attachments);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isRegistreActiu() {
		return delegate.isRegistreActiu();
	}

	/**
	 * 
	 * @param anotacio
	 * @return
	 * @throws PluginException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public RegistreIdDto registreAnotacioEntrada(RegistreAnotacioDto anotacio) throws PluginException {
		return delegate.registreAnotacioEntrada(anotacio);
	}

	/**
	 * 
	 * @param anotacio
	 * @return
	 * @throws PluginException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public RegistreIdDto registreAnotacioSortida(RegistreAnotacioDto anotacio) throws PluginException {
		return delegate.registreAnotacioSortida(anotacio);
	}

	/**
	 * 
	 * @param notificacio
	 * @return
	 * @throws PluginException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public RegistreIdDto registreNotificacio(RegistreNotificacioDto notificacio) throws PluginException {
		return delegate.registreNotificacio(notificacio);
	}

	/**
	 * 
	 * @param registreNumero
	 * @return
	 * @throws PluginException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Date registreNotificacioComprovarRecepcio(String registreNumero) throws PluginException {
		return delegate.registreNotificacioComprovarRecepcio(registreNumero);
	}

	/**
	 * 
	 * @param oficinaCodi
	 * @return
	 * @throws PluginException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getRegistreOficinaNom(String oficinaCodi) throws PluginException {
		return delegate.getRegistreOficinaNom(oficinaCodi);
	}

	/**
	 * 
	 * @param documentId
	 * @param annexosId
	 * @param persona
	 * @param personesPas1
	 * @param minSignatarisPas1
	 * @param personesPas2
	 * @param minSignatarisPas2
	 * @param personesPas3
	 * @param minSignatarisPas3
	 * @param expedientId
	 * @param importancia
	 * @param dataLimit
	 * @param tokenId
	 * @param processInstanceId
	 * @param transicioOK
	 * @param transicioKO
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void portasignaturesEnviar(Long documentId, List<Long> annexosId, PersonaDto persona, List<PersonaDto> personesPas1, int minSignatarisPas1, List<PersonaDto> personesPas2, int minSignatarisPas2, List<PersonaDto> personesPas3, int minSignatarisPas3, Long expedientId, String importancia, Date dataLimit, Long tokenId, Long processInstanceId, String transicioOK, String transicioKO) {
		delegate.portasignaturesEnviar(documentId, annexosId, persona, personesPas1, minSignatarisPas1, personesPas2, minSignatarisPas2, personesPas3, minSignatarisPas3, expedientId, importancia, dataLimit, tokenId, processInstanceId, transicioOK, transicioKO);
	}

	/**
	 * 
	 * @param processInstanceId
	 * @param dadesExpedient
	 * @throws PluginException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void zonaperExpedientCrear(String processInstanceId, ZonaperExpedientDto dadesExpedient) throws PluginException {
		delegate.zonaperExpedientCrear(processInstanceId, dadesExpedient);
	}

	/**
	 * 
	 * @param processInstanceId
	 * @param dadesEvent
	 * @throws PluginException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void zonaperEventCrear(String processInstanceId, ZonaperEventDto dadesEvent) throws PluginException {
		delegate.zonaperEventCrear(processInstanceId, dadesEvent);
	}

	/**
	 * Retorna l'estat donat l'entorn, el tipus d'expedient i el codi.
	 * 
	 * @param expedientTipusId
	 * @param estatCodi
	 * @return
	 * @throws EntornNotFoundException
	 * @throws ExpedientTipusNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public EstatDto findEstatAmbEntornIExpedientTipusICodi(Long entornId, String expedientTipusCodi, String estatCodi) throws EntornNotFoundException, ExpedientTipusNotFoundException {
		return delegate.findEstatAmbEntornIExpedientTipusICodi(entornId, expedientTipusCodi, estatCodi);
	}

	/**
	 * Retorna el document de disseny donada una definició de procés i el codi del document.
	 * 
	 * @param definicioProcesId
	 * @param documentCodi
	 * @return
	 * @throws DefinicioProcesNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDissenyDto getDocumentDisseny(Long definicioProcesId, String documentCodi) throws DefinicioProcesNotFoundException {
		return delegate.getDocumentDisseny(definicioProcesId, documentCodi);
	}

	/**
	 * Relaciona dos expedients donats els seus ids.
	 * 
	 * @param expedientIdOrigen
	 * @param expedientIdDesti
	 * @throws ExpedientNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void expedientRelacionar(Long expedientIdOrigen, Long expedientIdDesti) throws ExpedientNotFoundException {
		delegate.expedientRelacionar(expedientIdOrigen, expedientIdDesti);
	}

	/**
	 * Redirigeix un token del procés.
	 * 
	 * @param tokenId
	 * @param nodeName
	 * @param cancelarTasques
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tokenRedirigir(long tokenId, String nodeName, boolean cancelarTasques) {
		delegate.tokenRedirigir(tokenId, nodeName, cancelarTasques);
	}

	/**
	 * Obté l'arxiu directament de la gestió documental.
	 * 
	 * @param id
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getArxiuGestorDocumental(String id) {
		return delegate.getArxiuGestorDocumental(id);
	}

	/**
	 * Obté informació del tràmit del SISTRA.
	 * 
	 * @param numero
	 * @param clau
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TramitDto getTramit(String numero, String clau) {
		return delegate.getTramit(numero, clau);
	}

	/**
	 * Obté el text d'una variable de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param varCodi
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDadaDto getDadaPerProcessInstance(String processInstanceId, String varCodi) {
		return delegate.getDadaPerProcessInstance(processInstanceId, varCodi);
	}

	/**
	 * Retorna el resultat d'una consulta d'expedients.
	 * 
	 * @param entornId
	 * @param titol
	 * @param numero
	 * @param dataInici1
	 * @param dataInici2
	 * @param expedientTipusId
	 * @param estatId
	 * @param iniciat
	 * @param finalitzat
	 * @param geoPosX
	 * @param geoPosY
	 * @param geoReferencia
	 * @param mostrarAnulats
	 * @return
	 * @throws EntornNotFoundException
	 * @throws ExpedientTipusNotFoundException
	 * @throws EstatNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDto> findExpedientsConsultaGeneral(Long entornId, String titol, String numero, Date dataInici1, Date dataInici2, Long expedientTipusId, Long estatId, boolean iniciat, boolean finalitzat, Double geoPosX, Double geoPosY, String geoReferencia, FiltreAnulat mostrarAnulats) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException {
		return delegate.findExpedientsConsultaGeneral(entornId, titol, numero, dataInici1, dataInici2, expedientTipusId, estatId, iniciat, finalitzat, geoPosX, geoPosY, geoReferencia, mostrarAnulats);
	}

	/**
	 * Inicialitza les definicions de procés per a que estiguin disponibles per als handlers
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void initializeDefinicionsProces() {
		delegate.initializeDefinicionsProces();
	}

	/**
	 * Inicialitza una mesura de temps
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraIniciar(String clau, String familia, String tipusExpedient, String tasca, String detall) {
		delegate.mesuraIniciar(clau, familia, tipusExpedient, tasca, detall);
	}

	/**
	 * Realitza un càlcul de mesura de temps
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void mesuraCalcular(String clau, String familia, String tipusExpedient, String tasca, String detall) {
		delegate.mesuraCalcular(clau, familia, tipusExpedient, tasca, detall);
	}

	/**
	 * Informa si mesura de temps està activa
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean mesuraIsActiu() {
		return delegate.mesuraIsActiu();
	}

	/**
	 * Actualitza els camps d'error de l'expedient
	 * 
	 * @param processInstanceId
	 * @param errorDesc
	 * @param errorFull
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void updateExpedientError(String processInstanceId, String errorDesc, String errorFull) {
		delegate.updateExpedientError(processInstanceId, errorDesc, errorFull);
	}

	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getHeliumProperty(String propertyName) {
		return delegate.getHeliumProperty(propertyName);
	}

	/**
	 * Obté la propera operació a realitzar massivament
	 * 
	 * @param ultimaExecucioMassiva
	 * @return
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public OperacioMassivaDto getExecucionsMassivesActiva(Long ultimaExecucioMassiva) throws Exception {
		return delegate.getExecucionsMassivesActiva(ultimaExecucioMassiva);
	}

	/**
	 * Executa la operació massiva
	 * 
	 * @param operacioMassiva
	 * @throws Exception
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void executarExecucioMassiva(OperacioMassivaDto operacioMassiva) throws Exception {
		delegate.executarExecucioMassiva(operacioMassiva);
	}

	/**
	 * @param operacioMassiva
	 * @param e
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void generaInformeError(OperacioMassivaDto operacioMassiva, Exception e) {
		delegate.generaInformeError(operacioMassiva, e);
	}

	/**
	 * @param operacioMassiva
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void actualitzaUltimaOperacio(OperacioMassivaDto operacioMassiva) {
		delegate.actualitzaUltimaOperacio(operacioMassiva);
	}
}
