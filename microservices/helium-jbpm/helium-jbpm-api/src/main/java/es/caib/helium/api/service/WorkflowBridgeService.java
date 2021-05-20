package es.caib.helium.api.service;

import es.caib.helium.api.dto.*;
import es.caib.helium.api.dto.registre.RegistreAnotacio;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interfície comú per donar accés a funcionalitat d'Helium als motors de workflow.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public interface WorkflowBridgeService {

	// EXPEDIENTS
	////////////////////////////////////////////////////////////////////////////////

	public List<ExpedientDto> findExpedientsConsultaGeneral(
            Long entornId,
            String titol,
            String numero,
            Date dataInici1,
            Date dataInici2,
            Long expedientTipusId,
            Long estatId,
            boolean nomesIniciats,
            boolean nomesFinalitzats);

	public List<ExpedientDto> findExpedientsConsultaDadesIndexades(
            Long entornId,
            String expedientTipusCodi,
            Map<String, Object> filtreValors);

	public ExpedientDto getExpedientAmbEntornITipusINumero(
            Long entornId,
            String expedientTipusCodi,
            String numero);

	public ExpedientDto findExpedientAmbMateixTipusINumero(
			Long entornId,
			Long expedientTipusId,
			String numero);

	public ExpedientDto getExpedientArrelAmbProcessInstanceId(
			String processInstanceId);

	public void expedientRelacionar(
            Long expedientIdOrigen,
            Long expedientIdDesti);

	public void expedientAturar(
            String processInstanceId,
            String motiu);

	public void expedientReprendre(
            String processInstanceId);

	public void expedientModificarEstat(
            String processInstanceId,
            String estatCodi);

	public void expedientModificarComentari(
            String processInstanceId,
            String comentari);

	public void expedientModificarNumero(
            String processInstanceId,
            String numero);

	public void expedientModificarTitol(
			String processInstanceId,
			String titol);

	public void expedientModificarGeoref(
			String processInstanceId,
			Double posx,
			Double posy,
			String referencia);

	public void expedientModificarGrup(
			String processInstanceId,
			String grupCodi);

	public void expedientModificarResponsable(
			String processInstanceId,
			String responsableCodi);

	public void finalitzarExpedient(String processInstanceId);

	public void desfinalitzarExpedient(String processInstanceId);

	public void updateExpedientError(
			Long jobId,
			Long expedientId,
			String errorDesc,
			String errorFull);

	// Dades

	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String varCodi);

	// TASQUES
	////////////////////////////////////////////////////////////////////////////////

	public boolean isTascaEnSegonPla(Long taskId);

	public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message);

	public void setErrorTascaSegonPla(Long taskId, Exception ex);

	public List<CampTascaDto> findCampsPerTaskInstance(long taskInstanceId);

	public List<DocumentTascaDto> findDocumentsPerTaskInstance(long taskInstanceId);

	// Dades

	public void createDadesTasca(Long taskId);

	public TascaDadaDto getDadaPerTaskInstance(
			String processInstanceId,
			String taskInstanceId,
			String varCodi);


	// DOCUMENTS
	////////////////////////////////////////////////////////////////////////////////

	public DocumentDissenyDto getDocumentDisseny(
            Long definicioProcesId,
            String processInstanceId,
            String documentCodi);

	public DocumentDto getDocumentInfo(Long documentStoreId);

	public DocumentDto getDocumentInfo(Long documentStoreId,
                                       boolean ambContingutOriginal,
                                       boolean ambContingutSignat,
                                       boolean ambContingutVista,
                                       boolean perSignar,
                                       boolean perNotificar,
                                       boolean ambSegellSignatura);

	public String getCodiVariablePerDocumentCodi(String documentCodi);

	public ArxiuDto getArxiuPerMostrar(Long documentStoreId);

	public ArxiuDto documentGenerarAmbPlantilla(
            String taskInstanceId,
            String processInstanceId,
            String documentCodi,
            Date dataDocument);

	public Long documentExpedientGuardar(
			String processInstanceId,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut);

	public Long documentExpedientAdjuntar(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date adjuntData,
			String arxiuNom,
			byte[] arxiuContingut);

	public void documentExpedientGuardarDadesRegistre(
            Long documentStoreId,
            String registreNumero,
            Date registreData,
            String registreOficinaCodi,
            String registreOficinaNom,
            boolean registreEntrada);

	public void documentExpedientEsborrar(
            String taskInstanceId,
            String processInstanceId,
            String documentCodi);

	// TERMINIS
	////////////////////////////////////////////////////////////////////////////////

	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
            String processDefinitionId,
            String processInstanceId,
            String terminiCodi);

	public TerminiDto getTerminiAmbProcessInstanceICodi(
			String processInstanceId,
			String terminiCodi);

	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
			String processInstanceId,
			String terminiCodi);

	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi);

	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date data,
			boolean esDataFi);

	public void terminiPausar(
			Long terminiIniciatId,
			Date data);

	public void terminiContinuar(
			Long terminiIniciatId,
			Date data);

	public void terminiCancelar(
            Long terminiIniciatId,
            Date data);

	public Date terminiCalcularDataInici(
			Date fi,
			int anys,
			int mesos,
			int dies,
			boolean laborable,
			String processInstanceId);

	public Date terminiCalcularDataFi(
            Date inici,
            int anys,
            int mesos,
            int dies,
            boolean laborable,
            String processInstanceId);

	public void configurarTerminiIniciatAmbDadesWf(
            Long terminiIniciatId,
            String taskInstanceId,
            Long timerId);

	// ALERTES
	////////////////////////////////////////////////////////////////////////////////

	public void alertaCrear(
            Long entornId,
            Long expedientId,
            Date data,
            String usuariCodi,
            String text);

	public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId);

	// ENUMERACIONS
	////////////////////////////////////////////////////////////////////////////////

	public List<EnumeracioValorDto> enumeracioConsultar(
            String processInstanceId,
            String enumeracioCodi);

	public void enumeracioSetValor(
            String processInstanceId,
            String enumeracioCodi,
            String codi,
            String valor);

	// ESTATS
	////////////////////////////////////////////////////////////////////////////////

	public EstatDto findEstatAmbEntornIExpedientTipusICodi(
            Long entornId,
            String expedientTipusCodi,
            String estatCodi);

	// DOMINIS (MS DOMINIS)
	////////////////////////////////////////////////////////////////////////////////

	public List<DominiRespostaFilaDto> dominiConsultar(
            String processInstanceId,
            String dominiCodi,
            String dominiId,
            Map<String, Object> parametres);

	public List<DominiRespostaFilaDto> dominiInternConsultar(
			String processInstanceId,
			String dominiId,
			Map<String, Object> parametres);

	// INTERESSATS
	////////////////////////////////////////////////////////////////////////////////

	public void interessatCrear(InteressatDto interessat);

	public void interessatModificar(InteressatDto interessat);

	public void interessatEliminar(InteressatDto interessat);

	// GENERICS
	////////////////////////////////////////////////////////////////////////////////

	public void emailSend(
            String fromAddress,
            List<String> recipients,
            List<String> ccRecipients,
            List<String> bccRecipients,
            String subject,
            String text,
            List<ArxiuDto> attachments);

	public String getHeliumProperty(String propertyName);

	public String getUsuariCodiActual();

	public ExpedientDto getExpedientIniciant();

	public List<FestiuDto> findFestiusAll();

	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(
			String processInstanceId,
			String usuariCodi);

	// REGISTRE (MS Integració)
	////////////////////////////////////////////////////////////////////////////////

	public boolean isRegistreActiu();

	public RegistreIdDto registreAnotacioEntrada(
			RegistreAnotacioDto anotacio,
			Long expedientId);

	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacioDto anotacio,
			Long expedientId);

	public Date registreNotificacioComprovarRecepcio(
			String registreNumero,
			Long expedientId);

	public String registreObtenirOficinaNom(
			String oficinaCodi,
			Long expedientId);

	// Regweb3

	public boolean isRegistreRegWeb3Actiu();

	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacio anotacio,
			Long expedientId);

	public String registreObtenirOficinaNom(
			String numRegistre,
			String usuariCodi,
			String entitatCodi,
			Long expedientId);

	// TRAMITACIÓ (MS Integració)
	////////////////////////////////////////////////////////////////////////////////

	public TramitDto getTramit(String numero, String clau);

	public void zonaperExpedientCrear(
			ExpedientDto expedient,
			ZonaperExpedientDto dadesExpedient);

	public void zonaperEventCrear(
			String processInstanceId,
			ZonaperEventDto dadesEvent);

	public RegistreIdDto notificacioCrear(
			RegistreNotificacioDto notificacio,
			Long expedientId,
			boolean crearExpedient);

	public void notificacioGuardar(
			ExpedientDto expedient,
			NotificacioDto notificacio);

	public boolean notificacioEsborrar(
			String numero,
			String clave,
			Long codigo);

	public RespostaJustificantDetallRecepcioDto notificacioElectronicaJustificantDetall(
			String registreNumero);

	public RespostaJustificantRecepcioDto notificacioElectronicaJustificant(
			String registreNumero);

	// NOTIFICACIÓ (MS Integració)
	////////////////////////////////////////////////////////////////////////////////

	public RespostaNotificacio altaNotificacio(DadesNotificacioDto dadesNotificacio);

	// GESTIÓ DOCUMENTAL (MS Integració)
	////////////////////////////////////////////////////////////////////////////////

	public ArxiuDto getArxiuGestorDocumental(String id); // Eliminar?

	// FIRMA SERVIDOR (MS Integració)
	////////////////////////////////////////////////////////////////////////////////

	public void documentFirmaServidor(
			String processInstanceId,
			String documentCodi,
			String motiu);

	// PERSONES (MS Integració)
	////////////////////////////////////////////////////////////////////////////////

	public PersonaDto getPersonaAmbCodi(String codi);

	public List<String> getRolsByCodi(String codi);

	// PORTAFIRMES (MS Integració)
	////////////////////////////////////////////////////////////////////////////////

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
			Long processInstanceId,
			String transicioOK,
			String transicioKO);

	public void portasignaturesEliminar(List<Integer> documentIds);

	// DEFINICIO PROCES
	////////////////////////////////////////////////////////////////////////////////
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIProcessInstanceId(
			String jbpmKey,
			String processInstanceId);
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(String processInstanceId);
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIVersio(
			String jbpmKey,
			int version);
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey);
	public void initializeDefinicionsProces();


	// CARRECS
	////////////////////////////////////////////////////////////////////////////////
	public AreaDto getAreaAmbEntornICodi(
			Long entornId,
			String codi);

	public CarrecDto getCarrecAmbEntornIAreaICodi(
			Long entornId,
			String areaCodi,
			String carrecCodi);

	// MONITOR (Mètriques)
	////////////////////////////////////////////////////////////////////////////////

	public Long startMetric(
			Class clazz,
			String titol,
			Long entornId,
			Long expedientTipusId);

	public void endMetric(Long metricId);


	// LOCALS
	////////////////////////////////////////////////////////////////////////////////
	public Long getTaskInstanceIdByExecutionTokenId(Long executionTokenId);
	public boolean expedientReindexar(String processInstanceId); // Eliminar!!
	public EntornDto getEntornActual(); // Eliminar!!
	public void tokenRedirigir(
			long tokenId,
			String nodeName,
			boolean cancelarTasques);
	public boolean tokenActivar(long tokenId, boolean activar);
	public void expedientEliminaInformacioRetroaccio(String processInstanceId);
	public void afegirInstanciaProcesPerVerificarFinalitzacio(String processInstanceId);


}