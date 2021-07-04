package es.caib.helium.logic.intf.service;

import es.caib.helium.logic.intf.dto.*;

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

	public List<ExpedientInfo> findExpedientsConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long estatId,
			boolean nomesIniciats,
			boolean nomesFinalitzats);

	public List<ExpedientInfo> findExpedientsConsultaDadesIndexades(
			Long entornId,
			String expedientTipusCodi,
			Map<String, String> filtreValors);

	public ExpedientDto getExpedientAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero);

	public String getProcessInstanceIdAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero);

	public ExpedientDto getExpedientArrelAmbProcessInstanceId(String processInstanceId);

	public void expedientRelacionar(
			Long expedientIdOrigen,
			Long expedientIdDesti);

	public void expedientAturar(
			String processInstanceId,
			String motiu);

	public void expedientReprendre(
			String processInstanceId);

	public void desfinalitzarExpedient(String processInstanceId);

	public void finalitzarExpedient(
			String processInstanceId,
			Date dataFianlitzacio);

	public void expedientModificarEstat(
			String processInstanceId,
			String estatCodi);

	public void expedientModificarEstatId(
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

	public void updateExpedientError(
			Long expedientId,
			Long jobId,
			String errorDesc,
			String errorFull);
	// Dades

	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String codi);


	// TASQUES
	////////////////////////////////////////////////////////////////////////////////

//	public boolean isTascaEnSegonPla(Long taskId);

	public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message);

	public void setErrorTascaSegonPla(Long taskId, String error);

	public List<CampTascaDto> findCampsPerTaskInstance(
			String processInstanceId,
			String processDefinitionId,
			String taskName);

	public List<DocumentTascaDto> findDocumentsPerTaskInstance(
			String processInstanceId,
			String processDefinitionId,
			String taskName);

	public String getDadaPerTaskInstance(
			String processInstanceId,
			String taskInstanceId,
			String varCodi);

	public CampTascaDto getCampTascaPerInstanciaTasca(
			String taskName,
			String processDefinitionId,
			String processInstanceId,
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
//			String taskInstanceId,
			String processInstanceId,
			Long documentStoreId);

	// TERMINIS
	////////////////////////////////////////////////////////////////////////////////

	public TerminiDto getTerminiAmbProcessInstanceICodi(String processInstanceId, String terminiCodi);

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
			Date inici,
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

	// DOMINIS
	////////////////////////////////////////////////////////////////////////////////

	public List<DominiRespostaFilaDto> dominiConsultar(
			String processInstanceId,
			String dominiCodi,
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

	public List<FestiuDto> getFestiusAll();

	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(
			String processInstanceId,
			String usuariCodi);

	// DEFINICIONS DE PROCES
	////////////////////////////////////////////////////////////////////////////////

	public Integer getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(
			String jbpmKey,
			String processInstanceId);

	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(String processInstanceId);

	public Long getDefinicioProcesIdPerProcessInstanceId(String processInstanceId);

	public Long getDefinicioProcesEntornAmbJbpmKeyIVersio(
			String jbpmKey,
			Integer version);

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
}
