package es.caib.helium.logic.intf.service;

import com.codahale.metrics.MetricRegistry;
import es.caib.comanda.model.management.TascaEstat;
import es.caib.helium.commons.dto.*;
import es.caib.helium.commons.exception.*;
import es.caib.helium.commons.registre.RegistreAnotacio;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BpmnHeliumService {

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
	public void createDadesTasca(String id);

	/**
	 * Obté l'expedient donat l'entorn, el tipus d'expedient i el seu número.
	 *
	 * @param entornId
	 * @param expedientTipusCodi
	 * @param numero
	 * @return
	 * @throws NoTrobatException
	 */
	public ExpedientDto getExpedientAmbEntornITipusINumero(
		Long entornId,
		String expedientTipusCodi,
		String numero) throws NoTrobatException;

	/**
	 * Obté l'expedient arrel donada una instància de procés jBPM.
	 *
	 * @param processInstanceId
	 * @return
	 * @throws NoTrobatException
	 */
	public ExpedientDto getExpedientArrelAmbProcessInstanceId(
		String processInstanceId) throws NoTrobatException;

	/**
	 * Obté l'entorn donada una instància de procés jBPM.
	 *
	 * @param processInstanceId
	 * @return
	 * @throws NoTrobatException
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
	 * Obté la definició de procés donada la seva clau jBPM i el processInstanceId.
	 *
	 * @param jbpmKey la clau jBPM.
	 * @param processInstanceId
	 * @return La definició de procés o null si no s'ha trobat.
	 */
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIProcessInstanceId(
		String jbpmKey,
		String processInstanceId);

	/**
	 * Obté la darrera versió d'una definició de procés donat el
	 * seu entorn i la clau jBPM.
	 *
	 * @param entornId
	 * @param expedientTipusId
	 * @param jbpmKey
	 * @return
	 * @throws NoTrobatException
	 */
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
		Long entornId,
		Long expedientTipusId,
		String jbpmKey) throws NoTrobatException;

	/**
	 * Obté la definició de procés donada una instància de procés.
	 *
	 * @param processInstanceId Id de la instància de procés.
	 * @return la definició de procés.
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
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
	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(String processInstanceId, String usuariCodi);

	/**
	 * Crea una alerta.
	 *
	 * @param entornId
	 * @param expedientId
	 * @param data
	 * @param usuariCodi
	 * @param text
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
	 */
	public void expedientModificarEstat(
		String processInstanceId,
		String estatCodi) throws NoTrobatException;

	/**
	 * Modifica el comentari de l'expedient.
	 *
	 * @param processInstanceId
	 * @param comentari
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
	 */
	public void expedientModificarGrup(
		String processInstanceId,
		String grupCodi) throws NoTrobatException;

	/**
	 * Modifica el número de l'expedient.
	 *
	 * @param processInstanceId
	 * @param numero
	 * @throws NoTrobatException
	 */
	public void expedientModificarNumero(
		String processInstanceId,
		String numero) throws NoTrobatException;

	/**
	 * Modifica el responsable de l'expedient.
	 *
	 * @param processInstanceId
	 * @param responsableCodi
	 * @throws NoTrobatException
	 */
	public void expedientModificarResponsable(
		String processInstanceId,
		String responsableCodi) throws NoTrobatException;

	/**
	 * Modifica el titol de l'expedient.
	 *
	 * @param processInstanceId
	 * @param titol
	 * @throws NoTrobatException
	 */
	public void expedientModificarTitol(
		String processInstanceId,
		String titol) throws NoTrobatException;

	/**
	 * Atura la tramitació de l'expedient.
	 *
	 * @param processInstanceId
	 * @param motiu
	 * @throws NoTrobatException
	 */
	public void expedientAturar(
		String processInstanceId,
		String motiu) throws NoTrobatException;

	/**
	 * Repren la tramitació d'un expedient aturat.
	 *
	 * @param processInstanceId
	 * @throws NoTrobatException
	 */
	public void expedientReprendre(
		String processInstanceId) throws NoTrobatException;

	/**
	 * Buida els logs l'expedient.
	 *
	 * @param processInstanceId
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
	 */
	public ArxiuDto documentGenerarAmbPlantilla(
		String taskInstanceId,
		String processInstanceId,
		String documentCodi,
		Date dataDocument) throws NoTrobatException, ValidacioException, TramitacioException;

	/**
	 * Firma el document amb el plugin de firma de servidor.
	 *
	 * @param processInstanceId
	 * @param documentCodi
	 * @param motiu
	 * @param contingut
	 * @param documentStoreId
	 * @throws NoTrobatException
	 */
	public void documentFirmaServidor(
		String processInstanceId,
		String documentCodi,
		String motiu,
		byte[] contingut,
		Long documentStoreId) throws NoTrobatException, ValidacioException, TramitacioException;

	/**
	 * Obté el termini donada una instància de procés i el codi del termini.
	 *
	 * @param processInstanceId
	 * @param terminiCodi
	 * @return
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
	 */
	public void terminiCancelar(
		Long terminiIniciatId,
		Date data) throws NoTrobatException;

	/**
	 * Posa un termini iniciat en pausa.
	 *
	 * @param terminiIniciatId
	 * @param data
	 * @throws NoTrobatException
	 */
	public void terminiPausar(
		Long terminiIniciatId,
		Date data) throws NoTrobatException;

	/**
	 * Continua un termini pausat.
	 *
	 * @param terminiIniciatId
	 * @param data
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
	 * @throws SistemaExternException
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
	 * @param id
	 * @param parametres
	 * @return
	 * @throws Exception
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
	 * @throws NoTrobatException
	 */
	public List<EnumeracioValorDto> enumeracioConsultar(
		String processInstanceId,
		String enumeracioCodi) throws NoTrobatException;

	/**
	 * Fixa el valor pel camp de l'enumeració corresponent al codi.
	 *
	 * @param processInstanceId
	 * @param enumeracioCodi
	 * @param codi
	 * 			Codi de l'entrada a l'enumeració.
	 * @param valor
	 * 			Valor per fixar a l'enumeració.
	 * @throws NoTrobatException
	 */
	public void enumeracioSetValor(
		String processInstanceId,
		String enumeracioCodi,
		String codi,
		String valor) throws NoTrobatException;

	/**
	 * Retorna els camps del formulari d'una tasca.
	 *
	 * @param taskInstanceId
	 * @return
	 * @throws NoTrobatException
	 */
	public List<CampTascaDto> findCampsPerTaskInstance(
		String taskInstanceId) throws NoTrobatException;

	/**
	 * Retorna els documents d'una tasca.
	 *
	 * @param taskInstanceId
	 * @return
	 * @throws NoTrobatException
	 */
	public List<DocumentTascaDto> findDocumentsPerTaskInstance(
		String taskInstanceId) throws NoTrobatException;

	/**
	 * Obté informació d'un document d'un expedient d'Helium.
	 *
	 * @param documentStoreId
	 * @return
	 */
	public DocumentDto getDocumentInfo(Long documentStoreId);

	/**
	 * Obté informació d'un document d'un expedient d'Helium.
	 *
	 * @param documentStoreId,
	 * @param ambContingutOriginal,
	 * @param ambContingutSignat,
	 * @param ambContingutVista,
	 * @param perSignar,
	 * @param perNotificar,
	 * @param ambSegellSignatura
	 * @return
	 */
	public DocumentDto getDocumentInfo(
		Long documentStoreId,
		boolean ambContingutOriginal,
		boolean ambContingutSignat,
		boolean ambContingutVista,
		boolean perSignar,
		boolean perNotificar,
		boolean ambSegellSignatura);


	/**
	 * Obté l'arxiu d'un document d'un expedient d'Helium.
	 * Obté la versió imprimible de l'arxiu.
	 * @param documentStoreId
	 * @return
	 */
	public ArxiuDto getArxiuPerMostrar(Long documentStoreId);

	/**
	 * Obté la versió original d'un document d'un expedient d'Helium.
	 * @param expedientId
	 * @param documentStoreId
	 * @return
	 */
	public ArxiuDto getArxiuVersioOriginal(Long expedientId, Long documentStoreId);

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
	 * @param documentCodi
	 * @param data
	 * @param arxiuNom
	 * @param arxiuContingut
	 * @param firmat
	 * @return
	 */
	public Long documentExpedientGuardar(
		String processInstanceId,
		String documentCodi,
		Date data,
		String arxiuNom,
		byte[] arxiuContingut,
		boolean firmat);

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
	 * @throws SistemaExternException
	 * @throws NoTrobatException
	 */
	public RegistreIdDto registreAnotacioEntrada(
		RegistreAnotacioDto anotacio,
		Long expedientId) throws SistemaExternException, NoTrobatException;

	/**
	 *
	 * @param anotacio
	 * @return
	 * @throws SistemaExternException
	 * @throws NoTrobatException
	 */
	public RegistreIdDto registreAnotacioSortida(
		RegistreAnotacioDto anotacio,
		Long expedientId) throws SistemaExternException, NoTrobatException;

	/**
	 *
	 * @param anotacio
	 * @return
	 * @throws SistemaExternException
	 * @throws NoTrobatException
	 */
	public RegistreIdDto registreAnotacioSortida(
		RegistreAnotacio anotacio,
		Long expedientId) throws SistemaExternException, NoTrobatException;

	/**
	 *
	 * @param registreNumero
	 * @return
	 * @throws SistemaExternException
	 */
	public Date registreNotificacioComprovarRecepcio(
		String registreNumero,
		Long expedientId) throws SistemaExternException, NoTrobatException;

	/**
	 *
	 * @param oficinaCodi
	 * @return
	 * @throws SistemaExternException
	 * @throws NoTrobatException
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
	 * @throws SistemaExternException
	 * @throws NoTrobatException
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
	 * @throws SistemaExternException
	 * @throws NoTrobatException
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

	public RespostaNotificacio altaNotificacio(
		DadesNotificacioDto notificacio) throws SistemaExternException, NoTrobatException;


	/** PINBAL **/
	public Object consultaPinbal(
		DadesConsultaPinbalDto dadesConsultaPinbal,
		Long expedientId,
		String processInstanceId,
		Long tokenId) throws SistemaExternException, NoTrobatException;

	public Object consultaDadesIdentitatPinbalSVDDGPCIWS02(
		DadesConsultaPinbalDto dadesConsultaPinbal,
		Long expedientId,
		String processInstanceId,
		Long tokenId) throws SistemaExternException, NoTrobatException;

	public Object verificacioDadesIdentitatPinbalSVDDGPCIWS02(
		DadesConsultaPinbalDto dadesConsultaPinbal,
		Long expedientId,
		String processInstanceId,
		Long tokenId) throws SistemaExternException, NoTrobatException;

	public Object dadesTributariesPinbalSVDCCAACPASWS01(
		DadesConsultaPinbalDto dadesConsultaPinbal,
		Long expedientId,
		String processInstanceId,
		Long tokenId) throws SistemaExternException, NoTrobatException;



	/** Envia un document al portasignatures.
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
	 * @param portafirmesFluxId
	 *
	 * @return Retorna l'identificador del document donat pel portasignatures.
	 */
	public Integer portasignaturesEnviar(
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
		String processInstanceId,
		String transicioOK,
		String transicioKO,
		String portafirmesFluxId) throws SistemaExternException;

	/**
	 *
	 * @param documentId
	 */
	public void portasignaturesEliminar(
		Integer documentId) throws SistemaExternException;

	/**
	 *
	 * @param expedient
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
	 * @param entornId
	 * @param expedientTipusCodi
	 * @param estatCodi
	 * @return
	 * @throws NoTrobatException
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
	 * @param processInstanceId
	 * @param documentCodi
	 * @return
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
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
	 * @throws NoTrobatException
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

	/** Consulta els expedients filtrats a partir de les dades reindexades
	 * @param entornId
	 * @param expedientTipusCodi
	 * @param filtreValors
	 *
	 * @return Retorna el llistat d'expedients que compleixen amb el criteri.
	 */
	public List<ExpedientDto> findExpedientsConsultaDadesIndexades(
		Long entornId,
		String expedientTipusCodi,
		Map<String, Object> filtreValors);

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
	 * @param jobId
	 * @param expedientId
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
	 * Finalitzar un expedient
	 *
	 * @param processInstanceId
	 * @throws Exception
	 */
	public void finalitzarExpedient(String processInstanceId) throws Exception;

	/**
	 * Retorna una referència al registre de mètriques
	 *
	 */
	public MetricRegistry getMetricRegistry();

	/**
	 * Error completar tasca en segon pla
	 *
	 */
	public void setErrorTascaSegonPla(String taskId, Exception ex);

	/**
	 * Obtenir id de tasca a partir de Token
	 *
	 */
	public String getTaskInstanceIdByTokenId(String tokenId);

	/**
	 * Afegir missatge d'execució a la informació de la tasca en segón pla
	 *
	 */
	public void addMissatgeExecucioTascaSegonPla(String taskId, String[] message);

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
	 * @param taskId taska
	 * @return booleà si la tasca està en segón pla o no
	 */
	public boolean isTascaEnSegonPla(String taskId);

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

	public void interessatCrear(InteressatDto interessat);

	void interessatModificar(InteressatDto interessat);

	void interessatEliminar(InteressatDto interessat);

	/**
	 * Retorna un tipus d'expedient donat un entorn i un codi d'expedient
	 *
	 * @param entornId
	 *            L'atribut id del entorn.
	 * @param expedientTipusCodi
	 *            L'atribut codi del tipus d'expedient.
	 * @return
	 *            El tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si algun dels ids especificats no s'ha trobat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientTipusDto findExpedientTipusAmbEntorniCodi(
		Long entornId,
		String expedientTipusCodi) throws NoTrobatException, PermisDenegatException;

	/** Crea un .zip amb els documents a Notificar
	 *
	 * @param expedientId
	 * @param documentsPerAfegir
	 * @return Retorna el contingut del zip.
	 */
	byte[] getZipPerNotificar(Long expedientId, List<ExpedientDocumentDto> documentsPerAfegir);

	public DocumentDto findDocumentAmbId(Long documentStoreId) throws NoTrobatException;

	/**
	 * Retorna un document d'una instància de procés de
	 * l'expedient.
	 *
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @param documentStoreId
	 *            Atribut id de la taula document_store del document que
	 *            es vol consultar.
	 * @return El document de l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public ExpedientDocumentDto findOneAmbInstanciaProces(
		Long expedientId,
		String processInstanceId,
		Long documentStoreId) throws NoTrobatException, PermisDenegatException;

	/** Mètode per crear o actualitzar un document a un procés
	 *
	 * @param processInstanceId
	 * @param documentCodi
	 * @param titol
	 * @param data
	 * @param arxiu
	 * @param contingut
	 * @param annexosPerNotificar
	 * @return
	 */
	public Long guardarDocumentProces(
		String processInstanceId,
		String documentCodi,
		String titol,
		Date data,
		String arxiu,
		byte[] contingut,
		List<ExpedientDocumentDto> annexosPerNotificar);

	/**
	 * Retorna un document d'una instància de procés de
	 * l'expedient.
	 *
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @param documentCodi
	 *            Codi del document que es vol consultar.
	 * @return El document de l'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public ExpedientDocumentDto findOneAmbInstanciaProces(
		Long expedientId,
		String processInstanceId,
		String documentCodi) throws NoTrobatException, PermisDenegatException;

	/**
	 * Crea un nou document a dins la instància de procés.
	 *
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentStoreId
	 *             identificador del document a modificar.
	 * @param data
	 *             data del document.
	 * @param adjuntTitol
	 *             Títol per l'adjunt en cas que sigui un adjunt.
	 * @param arxiuNom
	 *             nom d'arxiu del document.
	 * @param arxiuContingut
	 *             contingut de l'arxiu del document.
	 * @param arxiuContentType
	 *             ContentType de l'arxiu.
	 * @param ntiOrigen
	 *             orígen NTI.
	 * @param ntiEstadoElaboracion
	 *             estat d'elaboració NTI.
	 * @param ntiTipoDocumental
	 *             tipus de document NTI.
	 * @param ntiIdOrigen
	 *             identificador NTI Del document original.
	 * @throws NoTrobatException
	 */
	public void update(
		Long expedientId,
		String processInstanceId,
		Long documentStoreId,
		Date data,
		String adjuntTitol,
		String arxiuNom,
		byte[] arxiuContingut,
		String arxiuContentType,
		boolean ambFirma,
		boolean firmaSeparada,
		byte[] firmaContingut,
		NtiOrigenEnumDto ntiOrigen,
		NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
		NtiTipoDocumentalEnumDto ntiTipoDocumental,
		String ntiIdOrigen) throws NoTrobatException;

	/**
	 * Crea un nou document a dins la instància de procés.
	 *
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentCodi
	 *             codi de document dins el disseny de l'expedient. Si aquest paràmetre no està informat llavors es tractarà
	 *             el document com un adjunt i s'aprofitarà el títol pel nou document i es tractarà com a tal.
	 * @param data
	 *             data del document.
	 * @param adjuntTitol
	 *             Títol per l'adjunt en el cas que no s'informi del codi del document.
	 * @param arxiuNom
	 *             nom d'arxiu del document.
	 * @param arxiuContingut
	 *             contingut de l'arxiu del document.
	 * @param arxiuContentType
	 *             ContentType de l'arxiu.
	 * @param ntiOrigen
	 *             orígen NTI.
	 * @param ntiEstadoElaboracion
	 *             estat d'elaboració NTI.
	 * @param ntiTipoDocumental
	 *             tipus de document NTI.
	 * @param ntiIdOrigen
	 *             identificador NTI Del document original.
	 * @param annexosPerNotificar
	 * @throws NoTrobatException
	 */
	public Long create(
		Long expedientId,
		String processInstanceId,
		String documentCodi,
		Date data,
		String adjuntTitol,
		String arxiuNom,
		byte[] arxiuContingut,
		String arxiuContentType,
		boolean ambFirma,
		boolean firmaSeparada,
		byte[] firmaContingut,
		NtiOrigenEnumDto ntiOrigen,
		NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
		NtiTipoDocumentalEnumDto ntiTipoDocumental,
		String ntiIdOrigen,
		List<ExpedientDocumentDto> annexosPerNotificar) throws NoTrobatException;


	/**
	 * Retorna l'arxiu del document.
	 *
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param processInstanceId
	 *             atribut id de la instància de procés.
	 * @param documentStoreId
	 *             atribut id del document emmagatzemat.
	 * @return L'arxiu del document.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos requerits per aquesta acció.
	 */
	public ArxiuDto arxiuFindAmbDocument(
		Long expedientId,
		String processInstanceId,
		Long documentStoreId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Rep una llista d'usuaris i retorna la llista d'usuaris filtrant els que tenen permís sobre l'expedient
	 * per permís de lectura directe o per permís de lectura sobre la UO del tipus d'expedient.
	 *
	 * @param expedientId
	 *             atribut id de l'expedient.
	 * @param usuaris
	 *             Llistat de codis d'usuari per revisar el permís.
	 * @return La llista d'usuaris amb permís de lectura directa o per la UO de l'expedient.
	 */
	public String[] filtrarUsuarisAmbPermisComu(
		Long expedientId,
		String usuaris[]);

	/**
	 * Refresca o crea la tasca a comanda
	 */
	public void refreshComandaTasca(String taskId, TascaEstat estat);

}
