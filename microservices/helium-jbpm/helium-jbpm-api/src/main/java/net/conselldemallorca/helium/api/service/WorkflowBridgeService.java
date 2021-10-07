package net.conselldemallorca.helium.api.service;

import net.conselldemallorca.helium.api.dto.*;
import net.conselldemallorca.helium.api.dto.registre.RegistreAnotacio;
import net.conselldemallorca.helium.api.exception.HeliumJbpmException;

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

	List<ExpedientInfoDto> findExpedientsConsultaGeneral(
            Long entornId,
            String titol,
            String numero,
            Date dataInici1,
            Date dataInici2,
            Long expedientTipusId,
            Long estatId,
            boolean nomesIniciats,
            boolean nomesFinalitzats);

	ExpedientDto getExpedientAmbEntornITipusINumero(
            Long entornId,
            String expedientTipusCodi,
            String numero);

	String getProcessInstanceIdAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero);

	public ExpedientDto getExpedientArrelAmbProcessInstanceId(
			String processInstanceId);

	public void expedientRelacionar(
            Long expedientIdOrigen,
            Long expedientIdDesti);

	public void expedientAturar(
            String processInstanceId,
            String motiu) throws HeliumJbpmException;

	public void expedientReprendre(
            String processInstanceId) throws HeliumJbpmException;

	public void expedientModificarEstat(
            String processInstanceId,
            String estatCodi);

	public void expedientModificarEstat(
			String processInstanceId,
			Long estatId);

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

	public void expedientModificarGeoreferencia(
			String processInstanceId,
			String referencia);

	public void expedientModificarGeoX(
			String processInstanceId,
			Double posx);

	public void expedientModificarGeoY(
			String processInstanceId,
			Double posy);

	public void expedientModificarDataInici(
			String processInstanceId,
			Date dataInici);

	public void expedientModificarGrup(
			String processInstanceId,
			String grupCodi);

	public void expedientModificarResponsable(
			String processInstanceId,
			String responsableCodi);

	public void finalitzarExpedient(String processInstanceId) throws HeliumJbpmException;

	public void desfinalitzarExpedient(String processInstanceId) throws HeliumJbpmException;

	public void updateExpedientError(
			Long jobId,
			Long expedientId,
			String errorDesc,
			String errorFull);

	// Dades

	public List<ExpedientInfoDto> findExpedientsConsultaDadesIndexades(
			Long entornId,
			String expedientTipusCodi,
			Map<String, Object> filtreValors);

	public String getDadaPerProcessInstance(
			String processInstanceId,
			String varCodi);

	// TASQUES
	////////////////////////////////////////////////////////////////////////////////

//	public boolean isTascaEnSegonPla(Long taskId);

	public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message);

	public void setErrorTascaSegonPla(Long taskId, Exception ex);

	public List<CampTascaDto> findCampsPerTaskInstance(
			String processInstanceId,
			String processDefinitionId,
			String taskName);

	public List<DocumentTascaDto> findDocumentsPerTaskInstance(
			String processInstanceId,
			String processDefinitionId,
			String taskName
	);
	
	void tascaCrear(TascaDto tascaDto);

	void tascaFinalitzar(
			long tascaId, 
			Date end);

	void tascaAssignar(
			long tascaId, 
			String actorId);

	void tascaAssignarUsuaris(
			long tascaId,
			List<String> usuaris);

	void tascaAssignarGrups(
			long tascaId,
			List<String> grups);


	// Dades

	public String getDadaPerTaskInstance(
			String processInstanceId,
			String taskInstanceId,
			String varCodi);

	public CampTascaDto getCampTascaPerInstanciaTasca(
			String taskName,
			String processDefinitionId,
			String processInstanceId,
			String name);

//	public List<CampTascaDto> getCampsPerTaskInstance(String processDefinitionId, String taskName);
//	public List<DocumentTascaDto> getDocumentsPerTaskInstance(String processDefinitionId, String taskName);


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
            String processDefinitionId,
            String processInstanceId,
            String documentCodi,
            Date dataDocument);

	public Long documentExpedientCrear(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date documentData,
			boolean isAdjunt,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut);

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
            String documentCodi) throws HeliumJbpmException;

	// TERMINIS
	////////////////////////////////////////////////////////////////////////////////

	public TerminiDto getTerminiAmbProcessInstanceICodi(
			String processInstanceId,
			String terminiCodi);

	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
            String processDefinitionId,
            String processInstanceId,
            String terminiCodi);

	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date data,
			Integer anys,
			Integer mesos,
			Integer dies,
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

	public void interessatEliminar(String interessatCodi, Long expedientId);

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
	public Integer getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(
			String jbpmKey,
			String processInstanceId);
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(String processInstanceId);
	public Long getDefinicioProcesIdPerProcessInstanceId(String processInstanceId);
	public Long getDefinicioProcesEntornAmbJbpmKeyIVersio(
			String jbpmKey,
			int version);
	public Long getDarreraVersioEntornAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey);
	public void initializeDefinicionsProces();
	public String getProcessDefinitionIdHeretadaAmbPid(String processInstanceId);

	// VARIABLES
	////////////////////////////////////////////////////////////////////////////////
	public CampTipusIgnored getCampAndIgnored(
			String processDefinitionId,
			Long expedientId,
			String varCodi);

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
	public void expedientEliminaInformacioRetroaccio(String processInstanceId) throws HeliumJbpmException;
	public void afegirInstanciaProcesPerVerificarFinalitzacio(String processInstanceId);

	// PROCESSOS
	////////////////////////////////////////////////////////////////////////////////
	void procesCrear(ProcesDto proces);
	void procesFinalitzar(Long processInstanceId, Date end);



	
}
