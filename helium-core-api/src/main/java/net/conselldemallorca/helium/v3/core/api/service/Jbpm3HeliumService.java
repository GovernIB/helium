/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.codahale.metrics.MetricRegistry;

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
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaJustificantDetallRecepcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaJustificantRecepcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperExpedientDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreAnotacio;


/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface Jbpm3HeliumService {

	/**
	 * Onte el codi d'usuari actual (autenticat).
	 * 
	 * @return el codi de l'usuari.
	 */
	public String getUsuariCodiActual();

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
	 * Rellena el campo de descripción de la tabla de JBPM_TASKINSTANCE 
	 * 
	 * @param id
	 */
	public void createDadesTasca(Long id);
	
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
	public ExpedientDto getExpedientAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) throws NoTrobatException;
	
	/**
	 * Borra l'expedient arrel donada una instància de procés jBPM.
	 * 
	 * @param processInstanceId
	 */
	public void luceneDeleteExpedient(
			String processInstanceId);

	/**
	 * Obté l'expedient arrel donada una instància de procés jBPM.
	 * 
	 * @param executionContext
	 * @param expedientTipusCodi
	 * @param numero
	 * @return
	 * @throws ProcessInstanceNotFoundException
	 */
	public ExpedientDto getExpedientArrelAmbProcessInstanceId(
			String processInstanceId) throws NoTrobatException;
	
	/**
	 * Obté l'entorn donada una instància de procés jBPM.
	 * 
	 * @param processInstanceId
	 * @return
	 * @throws ProcessInstanceNotFoundException
	 */
	public EntornDto getEntornAmbProcessInstanceId(
			String processInstanceId) throws NoTrobatException;

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
	 * @throws EntornNotFoundException
	 */
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey) throws NoTrobatException;

	/**
	 * Obté la definició de procés donada una instància de procés.
	 * 
	 * @param processInstanceId Id de la instància de procés.
	 * @return la definició de procés.
	 * @throws ProcessInstanceNotFoundException
	 */
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(
			String processInstanceId) throws NoTrobatException;

	/**
	 * Obté la informació d'una persona donat el seu codi.
	 * 
	 * @param codi El codi de la persona.
	 * @return La informació de la persona o null si no existeix.
	 */
	public PersonaDto getPersonaAmbCodi(String codi);

	/**
	 * Obté l'àrea donat un entorn i el codi.
	 * 
	 * @param entornId
	 * @param codi
	 * @return
	 * @throws EntornNotFoundException
	 */
	public AreaDto getAreaAmbEntornICodi(
			Long entornId,
			String codi) throws NoTrobatException;

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
	public CarrecDto getCarrecAmbEntornIAreaICodi(
			Long entornId,
			String areaCodi,
			String carrecCodi) throws NoTrobatException;

	/**
	 * Obté el llistat de tots els festius.
	 * 
	 * @return
	 */
	public List<FestiuDto> findFestiusAll();

	/**
	 * 
	 * @param usuariCodi
	 * @return
	 */
	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(String usuariCodi);

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
	public void alertaCrear(
			Long entornId,
			Long expedientId,
			Date data,
			String usuariCodi,
			String text) throws NoTrobatException;
	/**
	 * Esborra totes les alertes relacionades amb una determinada
	 * instància de tasca.
	 * 
	 * @param taskInstanceId
	 */
	public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId);

	/**
	 * Modifica l'estat de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param estatCodi
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 * @throws EstatNotFoundException
	 */
	public void expedientModificarEstat(
			String processInstanceId,
			String estatCodi) throws NoTrobatException;

	/**
	 * Modifica el comentari de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param comentari
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	public void expedientModificarComentari(
			String processInstanceId,
			String comentari) throws NoTrobatException;

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
	public void expedientModificarGeoref(
			String processInstanceId,
			Double posx,
			Double posy,
			String referencia) throws NoTrobatException;

	/**
	 * Modifica el grup de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param grupCodi
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	public void expedientModificarGrup(
			String processInstanceId,
			String grupCodi) throws NoTrobatException;

	/**
	 * Modifica el número de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param numero
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	public void expedientModificarNumero(
			String processInstanceId,
			String numero) throws NoTrobatException;

	/**
	 * Modifica el responsable de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param responsableCodi
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 * @throws PersonaNotFoundException
	 */
	public void expedientModificarResponsable(
			String processInstanceId,
			String responsableCodi) throws NoTrobatException;

	/**
	 * Modifica el titol de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param titol
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	public void expedientModificarTitol(
			String processInstanceId,
			String titol) throws NoTrobatException;

	/**
	 * Atura la tramitació de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param motiu
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	public void expedientAturar(
			String processInstanceId,
			String motiu) throws NoTrobatException;

	/**
	 * Repren la tramitació d'un expedient aturat.
	 * 
	 * @param processInstanceId
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	public void expedientReprendre(
			String processInstanceId) throws NoTrobatException;

	/**
	 * Reindexa l'expedient.
	 * 
	 * @param processInstanceId
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	public void expedientReindexar(
			String processInstanceId) throws NoTrobatException;
	
	/**
	 * Buida els logs l'expedient.
	 * 
	 * @param processInstanceId
	 * @throws ProcessInstanceNotFoundException
	 * @throws ExpedientNotFoundException
	 */
	public void expedientBuidaLogs(
			String processInstanceId) throws NoTrobatException;

	/**
	 * Genera un document a partir d'una plantilla.
	 * 
	 * @param taskInstanceId
	 * @param processInstanceId
	 * @param documentCodi
	 * @param dataDocument
	 * @return
	 * @throws DefinicioProcesNotFoundException
	 * @throws DocumentNotFoundException
	 * @throws DocumentGenerarException
	 */
	public ArxiuDto documentGenerarAmbPlantilla(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date dataDocument) throws NoTrobatException, ValidacioException, TramitacioException;

	/**
	 * Obté el termini donada una instància de procés i el codi del termini.
	 * 
	 * @param processInstanceId
	 * @param terminiCodi
	 * @return
	 * @throws ProcessInstanceNotFoundException
	 */
	public TerminiDto getTerminiAmbProcessInstanceICodi(
			String processInstanceId,
			String terminiCodi) throws NoTrobatException;

	/**
	 * Obté el termini iniciat donada una instància de procés i el codi del termini.
	 * 
	 * @param processInstanceId
	 * @param terminiCodi
	 * @return
	 * @throws ProcessInstanceNotFoundException
	 * @throws TerminiNotFoundException
	 */
	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
			String processInstanceId,
			String terminiCodi) throws NoTrobatException;

	/**
	 * Configura una tasca o un timer amb un termini iniciat.
	 * 
	 * @param terminiIniciatId
	 * @param taskInstanceId
	 * @param timerId
	 * @throws TerminiIniciatNotFoundException
	 */
	public void configurarTerminiIniciatAmbDadesJbpm(
			Long terminiIniciatId,
			String taskInstanceId,
			Long timerId) throws NoTrobatException;

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
	public Date terminiCalcularDataInici(
			Date fi,
			int anys,
			int mesos,
			int dies,
			boolean laborable,
			String processInstanceId);

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
	public Date terminiCalcularDataFi(
			Date inici,
			int anys,
			int mesos,
			int dies,
			boolean laborable,
			String processInstanceId);

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
	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date inici,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) throws NoTrobatException;

	/**
	 * Inicia un termini amb l'interval que tengui configurat.
	 * 
	 * @param terminiCodi
	 * @param processInstanceId
	 * @param data
	 * @param esDataFi
	 * @throws TerminiNotFoundException
	 */
	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date data,
			boolean esDataFi) throws NoTrobatException;

	/**
	 * Cancela un termini iniciat.
	 * 
	 * @param terminiIniciatId
	 * @param data
	 * @throws TerminiIniciatNotFoundException
	 */
	public void terminiCancelar(
			Long terminiIniciatId,
			Date data) throws NoTrobatException;

	/**
	 * Posa un termini iniciat en pausa.
	 * 
	 * @param terminiIniciatId
	 * @param data
	 * @throws TerminiIniciatNotFoundException
	 */
	public void terminiPausar(
			Long terminiIniciatId,
			Date data) throws NoTrobatException;

	/**
	 * Continua un termini pausat.
	 * 
	 * @param terminiIniciatId
	 * @param data
	 * @throws TerminiIniciatNotFoundException
	 */
	public void terminiContinuar(
			Long terminiIniciatId,
			Date data) throws NoTrobatException;

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
	public List<DominiRespostaFilaDto> dominiConsultar(
			String processInstanceId,
			String dominiCodi,
			String dominiId,
			Map<String, Object> parametres) throws NoTrobatException, SistemaExternException;

	/**
	 * Consulta un domini i retorna el resultat.
	 * 
	 * @param processInstanceId
	 * @param dominiCodi
	 * @param dominiId
	 * @param parametres
	 * @return
	 * @throws ExpedientNotFoundException
	 * @throws DominiConsultaException
	 */
	public List<DominiRespostaFilaDto> dominiInternConsultar(
			String processInstanceId, 
			String id,
			Map<String, Object> parametres) throws Exception;
	
	/**
	 * Retorna tots els valors d'una enumeració.
	 * 
	 * @param processInstanceId
	 * @param enumeracioCodi
	 * @return
	 * @throws ExpedientNotFoundException
	 * @throws EnumeracioNotFoundException
	 */
	public List<EnumeracioValorDto> enumeracioConsultar(
			String processInstanceId,
			String enumeracioCodi) throws NoTrobatException, NoTrobatException;

	/**
	 * Retorna els camps del formulari d'una tasca.
	 * 
	 * @param taskInstanceId
	 * @return
	 * @throws TaskInstanceNotFoundException
	 * @throws DefinicioProcesNotFoundException
	 * @throws TascaNotFoundException
	 */
	public List<CampTascaDto> findCampsPerTaskInstance(
			long taskInstanceId) throws NoTrobatException;

	/**
	 * Retorna els documents d'una tasca.
	 * 
	 * @param taskInstanceId
	 * @return
	 * @throws TaskInstanceNotFoundException
	 * @throws DefinicioProcesNotFoundException
	 * @throws TascaNotFoundException
	 */
	public List<DocumentTascaDto> findDocumentsPerTaskInstance(
			long taskInstanceId) throws NoTrobatException;

	/**
	 * Retorna el codi de variable jBPM per un codi de document.
	 * 
	 * @param documentCodi
	 * @return
	 */
	public String getCodiVariablePerDocumentCodi(String documentCodi);

	/**
	 * Obté informació d'un document d'un expedient d'Helium.
	 * 
	 * @param documentCodi
	 * @return
	 */
	public DocumentDto getDocumentInfo(Long documentStoreId);

	/**
	 * Obté l'arxiu d'un document d'un expedient d'Helium.
	 * @param documentStoreId
	 * @return
	 */
	public ArxiuDto getArxiuPerMostrar(Long documentStoreId);

	/**
	 * 
	 * @param processInstanceId
	 * @param documentCodi
	 * @param data
	 * @param arxiuNom
	 * @param arxiuContingut
	 * @return
	 */
	public Long documentExpedientGuardar(
			String processInstanceId,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut);

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
	public Long documentExpedientAdjuntar(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date adjuntData,
			String arxiuNom,
			byte[] arxiuContingut);

	/**
	 * 
	 * @param taskInstanceId
	 * @param processInstanceId
	 * @param documentCodi
	 */
	public void documentExpedientEsborrar(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi);

	/**
	 * 
	 * @param documentStoreId
	 * @param registreNumero
	 * @param registreData
	 * @param registreOficinaCodi
	 * @param registreOficinaNom
	 * @param registreEntrada
	 */
	public void documentExpedientGuardarDadesRegistre(
			Long documentStoreId,
			String registreNumero,
			Date registreData,
			String registreOficinaCodi,
			String registreOficinaNom,
			boolean registreEntrada);

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
	public void emailSend(
			String fromAddress,
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<ArxiuDto> attachments) throws SistemaExternException;

	/**
	 * 
	 * @return
	 */
	public boolean isRegistreActiu();
	
	/**
	 * 
	 * @return
	 */
	public boolean isRegistreRegWeb3Actiu();

	/**
	 * 
	 * @param anotacio
	 * @return
	 * @throws PluginException
	 */
	public RegistreIdDto registreAnotacioEntrada(
			RegistreAnotacioDto anotacio,
			Long expedientId) throws SistemaExternException, NoTrobatException;

	/**
	 * 
	 * @param anotacio
	 * @return
	 * @throws PluginException
	 */
	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacioDto anotacio,
			Long expedientId) throws SistemaExternException, NoTrobatException;
	
	/**
	 * 
	 * @param anotacio
	 * @return
	 * @throws PluginException
	 */
	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacio anotacio,
			Long expedientId) throws SistemaExternException, NoTrobatException;

	/**
	 * 
	 * @param registreNumero
	 * @return
	 * @throws PluginException
	 */
	public Date registreNotificacioComprovarRecepcio(
			String registreNumero,
			Long expedientId) throws SistemaExternException, NoTrobatException;

	/**
	 * 
	 * @param oficinaCodi
	 * @return
	 * @throws PluginException
	 */
	public String registreObtenirOficinaNom(
			String oficinaCodi,
			Long expedientId) throws SistemaExternException, NoTrobatException;
	
	/**
	 * 
	 * @param numRegistre
	 * @param usuariCodi
	 * @param entitatCodi
	 * @return
	 * @throws PluginException
	 */
	public String registreObtenirOficinaNom(
			String numRegistre,
			String usuariCodi,
			String entitatCodi,
			Long expedientId) throws SistemaExternException, NoTrobatException;

	/**
	 * 
	 * @param notificacio
	 * @return
	 * @throws PluginException
	 */
	public RegistreIdDto notificacioCrear(
			RegistreNotificacioDto notificacio,
			Long expedientId,
			boolean crearExpedient) throws SistemaExternException, NoTrobatException;

	public void notificacioGuardar(
			ExpedientDto expedient,
			NotificacioDto notificacio);

	public boolean notificacioEsborrar(
			String numero,
			String clave,
			Long codigo);	

	public RespostaJustificantRecepcioDto notificacioElectronicaJustificant(
			String registreNumero) throws Exception;

	public RespostaJustificantDetallRecepcioDto notificacioElectronicaJustificantDetall(
			String registreNumero) throws Exception;

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
	public void portasignaturesEnviar(
			Long documentId,
			List<Long> annexosId,
			PersonaDto persona,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3,
			Long expedientId,
			String importancia,
			Date dataLimit,
			Long tokenId,
			Long processInstanceId,
			String transicioOK,
			String transicioKO) throws SistemaExternException;
	
	/**
	 * 
	 * @param documentsId
	 */
	public void portasignaturesEliminar(
			List<Integer> documentsId) throws SistemaExternException;

	/**
	 * 
	 * @param expedient 
	 * @param processInstanceId
	 * @param dadesExpedient
	 * @throws SistemaExternException
	 */
	public void zonaperExpedientCrear(
			ExpedientDto expedient,
			ZonaperExpedientDto dadesExpedient) throws SistemaExternException;

	/**
	 * 
	 * @param processInstanceId
	 * @param dadesEvent
	 * @throws SistemaExternException
	 */
	public void zonaperEventCrear(
			String processInstanceId,
			ZonaperEventDto dadesEvent) throws SistemaExternException;

	/**
	 * Retorna l'estat donat l'entorn, el tipus d'expedient i el codi.
	 * 
	 * @param expedientTipusId
	 * @param estatCodi
	 * @return
	 * @throws EntornNotFoundException
	 * @throws ExpedientTipusNotFoundException
	 */
	public EstatDto findEstatAmbEntornIExpedientTipusICodi(
			Long entornId,
			String expedientTipusCodi,
			String estatCodi) throws NoTrobatException;

	/**
	 * Retorna el document de disseny donada una definició de procés
	 * i el codi del document.
	 * 
	 * @param definicioProcesId
	 * @param documentCodi
	 * @return
	 * @throws DefinicioProcesNotFoundException
	 */
	public DocumentDissenyDto getDocumentDisseny(
			Long definicioProcesId,
			String processInstanceId,
			String documentCodi) throws NoTrobatException;

	/**
	 * Relaciona dos expedients donats els seus ids.
	 * 
	 * @param expedientIdOrigen
	 * @param expedientIdDesti
	 * @throws ExpedientNotFoundException
	 */
	public void expedientRelacionar(
			Long expedientIdOrigen,
			Long expedientIdDesti) throws NoTrobatException;

	/**
	 * Redirigeix un token del procés.
	 * 
	 * @param tokenId
	 * @param nodeName
	 * @param cancelarTasques
	 */
	public void tokenRedirigir(
			long tokenId,
			String nodeName,
			boolean cancelarTasques);

	/**
	 * Obté l'arxiu directament de la gestió documental.
	 * 
	 * @param id
	 * @return
	 */
	public ArxiuDto getArxiuGestorDocumental(
			String id);

	/**
	 * Obté informació del tràmit del SISTRA.
	 * 
	 * @param numero
	 * @param clau
	 * @return
	 */
	public TramitDto getTramit(
			String numero,
			String clau);

	/**
	 * Obté el text d'una variable de l'expedient.
	 * 
	 * @param processInstanceId
	 * @param varCodi
	 * @return
	 */
	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String varCodi);
	

	/**
	 * Obté el text d'una variable de la tasca.
	 * 
	 * @param taskInstanceId
	 * @param varCodi
	 * @return
	 */
	public TascaDadaDto getDadaPerTaskInstance(
			String processInstanceId,
			String taskInstanceId,
			String varCodi);

	/**
	 * Consulta els expedients amb el mateix tipus i número.
	 * 
	 * @param expedientTipusId
	 * @param numero
	 * @return
	 */
	public ExpedientDto findExpedientAmbMateixTipusINumero(
			Long entornId,
			Long expedientTipusId,
			String numero);
	
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
	 * @param nomesIniciats
	 * @param nomesFinalitzats
	 * @return
	 * @throws EntornNotFoundException
	 * @throws ExpedientTipusNotFoundException
	 * @throws EstatNotFoundException
	 */
	public List<ExpedientDto> findExpedientsConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long estatId,
			boolean nomesIniciats,
			boolean nomesFinalitzats) throws NoTrobatException;

	
	/**
	 * Inicialitza les definicions de procés per a que estiguin disponibles per als handlers
	 */
	public void initializeDefinicionsProces();
	
	/**
	 * Inicialitza una mesura de temps
	 */
	public void mesuraIniciar(String clau, String familia, String tipusExpedient, String tasca, String detall);
	
	/**
	 * Realitza un càlcul de mesura de temps
	 */
	public void mesuraCalcular(String clau, String familia, String tipusExpedient, String tasca, String detall);
	
	/**
	 * Informa si mesura de temps està activa
	 */
	public boolean mesuraIsActiu();

	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	public String getHeliumProperty(String propertyName);

	/**
	 * Actualitza els camps d'error de l'expedient
	 * 
	 * @param processInstanceId
	 * @param errorDesc
	 * @param errorFull
	 */
	public void updateExpedientError(Long jobId, Long expedientId, String errorDesc, String errorFull);

	/**
	 * Activa o desactiva un token
	 * 
	 * @param tokenId
	 * @param activar
	 * @return
	 */
	public boolean tokenActivar(long tokenId, boolean activar);

	/**
	 * Desfinalitzar un expedient
	 * 
	 * @param processInstanceId
	 * @throws Exception
	 */
	public void desfinalitzarExpedient(String processInstanceId) throws Exception;

	/**
	 * Retorna una referència al registre de mètriques
	 * 
	 */
	public MetricRegistry getMetricRegistry();
	
	/**
	 * Error completar tasca en segon pla
	 * 
	 */
	public void setErrorTascaSegonPla(Long taskId, Exception ex);
	
	/**
	 * Obtenir id de tasca a partir de Token
	 * 
	 */
	public Long getTaskInstanceIdByTokenId(Long tokenId);
	
	/**
	 * Afegir missatge d'execució a la informació de la tasca en segón pla
	 * 
	 */
	public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message);

	/**
	 * Obtenir la llista de rols a partir del codi d'un usuari
	 * 
	 * @param codi
	 * @return llista de rols. En cas que el plugin de persones no estigui actiu retorna una llista buida
	 */
	public List<String> getRolsByCodi(String codi);
	
	/**
	 * La tasca esta en segon pla
	 * 
	 * @param codi taska
	 * @return booleà si la tasca està en segón pla o no
	 */
	public boolean isTascaEnSegonPla(Long taskId);

	/** Retorna la llista de definicions de processos que siguin sub proces de l'indicat.
	 * 
	 * @param definicioProcesId
	 * @return
	 */
	public List<DefinicioProcesDto> findSubDefinicionsProces(Long definicioProcesId);

	/**
	 * Afegeix una instància de procés per a verificar la seva finalització.
	 * 
	 * @param processInstanceId
	 */
	public void afegirInstanciaProcesPerVerificarFinalitzacio(
			String processInstanceId);

}
