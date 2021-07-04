package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.*;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interfície comú per donar accés a funcionalitat d'Helium als motors de workflow.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Stateless
public class WorkflowBridgeService extends AbstractService<es.caib.helium.logic.intf.service.WorkflowBridgeService> implements es.caib.helium.logic.intf.service.WorkflowBridgeService {

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
            boolean nomesFinalitzats) {
		return getDelegateService().findExpedientsConsultaGeneral(
				entornId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipusId,
				estatId,
				nomesIniciats,
				nomesFinalitzats);
	}

	public List<ExpedientInfo> findExpedientsConsultaDadesIndexades(
            Long entornId,
            String expedientTipusCodi,
            Map<String, String> filtreValors) {
		return getDelegateService().findExpedientsConsultaDadesIndexades(entornId, expedientTipusCodi, filtreValors);
	}

	public ExpedientDto getExpedientAmbEntornITipusINumero(
            Long entornId,
            String expedientTipusCodi,
            String numero) {
		return getDelegateService().getExpedientAmbEntornITipusINumero(entornId, expedientTipusCodi, numero);
	}

	public String getProcessInstanceIdAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) {
		return getDelegateService().getProcessInstanceIdAmbEntornITipusINumero(entornId, expedientTipusCodi, numero);
	}

	@Override
	public ExpedientDto getExpedientArrelAmbProcessInstanceId(String processInstanceId) {
		return getDelegateService().getExpedientArrelAmbProcessInstanceId(processInstanceId);
	}

	public void expedientRelacionar(
            Long expedientIdOrigen,
            Long expedientIdDesti) {
		getDelegateService().expedientRelacionar(expedientIdOrigen, expedientIdDesti);
	}

	public void expedientAturar(
            String processInstanceId,
            String motiu) {
		getDelegateService().expedientAturar(processInstanceId, motiu);
	}

	public void expedientReprendre(
            String processInstanceId) {
		getDelegateService().expedientReprendre(processInstanceId);
	}

	public void desfinalitzarExpedient(String processInstanceId) {
		getDelegateService().desfinalitzarExpedient(processInstanceId);
		
	}

	public void expedientModificarEstat(
            String processInstanceId,
            String estatCodi) {
		getDelegateService().expedientModificarEstat(processInstanceId, estatCodi);
	}

	public void expedientModificarEstatId(
			String processInstanceId,
			Long estatId) {
		getDelegateService().expedientModificarEstatId(processInstanceId, estatId);
	}

	public void expedientModificarComentari(
            String processInstanceId,
            String comentari) {
		getDelegateService().expedientModificarComentari(processInstanceId, comentari);
	}

	public void expedientModificarNumero(
            String processInstanceId,
            String numero) {
		getDelegateService().expedientModificarNumero(processInstanceId, numero);
	}


	// TASQUES
	////////////////////////////////////////////////////////////////////////////////

//	public boolean isTascaEnSegonPla(Long taskId) {
//		return getDelegateService().isTascaEnSegonPla(taskId);
//	}


	public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message) {
		getDelegateService().addMissatgeExecucioTascaSegonPla(taskId, message);
	}

	public void setErrorTascaSegonPla(Long taskId, String error) {
		getDelegateService().setErrorTascaSegonPla(taskId, error);
	}

	// DOCUMENTS
	////////////////////////////////////////////////////////////////////////////////

	public DocumentDissenyDto getDocumentDisseny(
            Long definicioProcesId,
            String processInstanceId,
            String documentCodi) {
		return getDelegateService().getDocumentDisseny(definicioProcesId, processInstanceId, documentCodi);
	}

	public DocumentDto getDocumentInfo(Long documentStoreId) {
		return getDelegateService().getDocumentInfo(documentStoreId);
	}

	public DocumentDto getDocumentInfo(Long documentStoreId,
                                       boolean ambContingutOriginal,
                                       boolean ambContingutSignat,
                                       boolean ambContingutVista,
                                       boolean perSignar,
                                       boolean perNotificar,
                                       boolean ambSegellSignatura) {
		return getDelegateService().getDocumentInfo(
				documentStoreId,
				ambContingutOriginal,
				ambContingutSignat,
				ambContingutVista,
				perSignar,
				perNotificar,
				ambSegellSignatura);
	}

	public String getCodiVariablePerDocumentCodi(String documentCodi) {
		return getDelegateService().getCodiVariablePerDocumentCodi(documentCodi);
	}

	public ArxiuDto getArxiuPerMostrar(Long documentStoreId) {
		return getDelegateService().getArxiuPerMostrar(documentStoreId);
	}

	public ArxiuDto documentGenerarAmbPlantilla(
            String taskInstanceId,
            String processDefinitionId,
            String processInstanceId,
            String documentCodi,
            Date dataDocument) {
		return getDelegateService().documentGenerarAmbPlantilla(taskInstanceId, processDefinitionId, processInstanceId, documentCodi, dataDocument);
	}

	@Override
	public Long documentExpedientCrear(String taskInstanceId, String processInstanceId, String documentCodi, Date documentData, boolean isAdjunt, String adjuntTitol, String arxiuNom, byte[] arxiuContingut) {
		return getDelegateService().documentExpedientCrear(taskInstanceId, processInstanceId, documentCodi, documentData, isAdjunt, adjuntTitol, arxiuNom, arxiuContingut);
	}

	public void documentExpedientGuardarDadesRegistre(
            Long documentStoreId,
            String registreNumero,
            Date registreData,
            String registreOficinaCodi,
            String registreOficinaNom,
            boolean registreEntrada) {
		getDelegateService().documentExpedientGuardarDadesRegistre(documentStoreId, registreNumero, registreData, registreOficinaCodi, registreOficinaNom, registreEntrada);
	}

	public void documentExpedientEsborrar(
			String processInstanceId,
			Long documentStoreId) {
		getDelegateService().documentExpedientEsborrar(processInstanceId, documentStoreId);
	}

	// TERMINIS
	////////////////////////////////////////////////////////////////////////////////

	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
            String processDefinitionId,
            String processInstanceId,
            String terminiCodi) {
		return getDelegateService().getTerminiIniciatAmbProcessInstanceITerminiCodi(processDefinitionId, processInstanceId, terminiCodi);
	}

	public void terminiCancelar(
            Long terminiIniciatId,
            Date data) {
		getDelegateService().terminiCancelar(terminiIniciatId, data);
	}

	public Date terminiCalcularDataFi(
            Date inici,
            int anys,
            int mesos,
            int dies,
            boolean laborable,
            String processInstanceId) {
		return getDelegateService().terminiCalcularDataFi(inici, anys, mesos, dies, laborable, processInstanceId);
	}

	public void configurarTerminiIniciatAmbDadesWf(
            Long terminiIniciatId,
            String taskInstanceId,
            Long timerId) {
		getDelegateService().configurarTerminiIniciatAmbDadesWf(terminiIniciatId, taskInstanceId, timerId);
	}

	// ALERTES
	////////////////////////////////////////////////////////////////////////////////

	public void alertaCrear(
            Long entornId,
            Long expedientId,
            Date data,
            String usuariCodi,
            String text) {
		getDelegateService().alertaCrear(entornId, expedientId, data, usuariCodi, text);
	}

	@Override
	public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId) {
		getDelegateService().alertaEsborrarAmbTaskInstanceId(taskInstanceId);
	}

	// ENUMERACIONS
	////////////////////////////////////////////////////////////////////////////////

	public List<EnumeracioValorDto> enumeracioConsultar(
            String processInstanceId,
            String enumeracioCodi) {
		return getDelegateService().enumeracioConsultar(processInstanceId, enumeracioCodi);
	}

	public void enumeracioSetValor(
            String processInstanceId,
            String enumeracioCodi,
            String codi,
            String valor) {
		getDelegateService().enumeracioSetValor(processInstanceId, enumeracioCodi, codi, valor);
	}

	// ESTATS
	////////////////////////////////////////////////////////////////////////////////

	public EstatDto findEstatAmbEntornIExpedientTipusICodi(
            Long entornId,
            String expedientTipusCodi,
            String estatCodi) {
		return getDelegateService().findEstatAmbEntornIExpedientTipusICodi(entornId, expedientTipusCodi, estatCodi);
	}

	// DOMINIS
	////////////////////////////////////////////////////////////////////////////////

	public List<DominiRespostaFilaDto> dominiConsultar(
            String processInstanceId,
            String dominiCodi,
            String dominiId,
            Map<String, Object> parametres) {
		return getDelegateService().dominiConsultar(processInstanceId, dominiCodi, dominiId, parametres);
	}

	// INTERESSATS
	////////////////////////////////////////////////////////////////////////////////

	public void interessatCrear(InteressatDto interessat) {
		getDelegateService().interessatCrear(interessat);
	}

	public void interessatModificar(InteressatDto interessat) {
		getDelegateService().interessatModificar(interessat);
	}

	public void interessatEliminar(String interessatCodi, Long expedientId) {
		getDelegateService().interessatEliminar(interessatCodi, expedientId);
	}

	// GENERICS
	////////////////////////////////////////////////////////////////////////////////

	public void emailSend(
            String fromAddress,
            List<String> recipients,
            List<String> ccRecipients,
            List<String> bccRecipients,
            String subject,
            String text,
            List<ArxiuDto> attachments) {
		getDelegateService().emailSend(fromAddress, recipients, ccRecipients, bccRecipients, subject, text, attachments);
	}

	public String getHeliumProperty(String propertyName) {
		return getDelegateService().getHeliumProperty(propertyName);
	}

	public String getUsuariCodiActual() {
		return getDelegateService().getUsuariCodiActual();
	}

	public ExpedientDto getExpedientIniciant() {
		return getDelegateService().getExpedientIniciant();
	}

	public void finalitzarExpedient(
			String processInstanceId,
			Date dataFinalitzacio) {
		getDelegateService().finalitzarExpedient(
				processInstanceId,
				dataFinalitzacio);
	}

	public void expedientModificarTitol(String processInstanceId, String titol) {
		getDelegateService().expedientModificarTitol(processInstanceId, titol);
	}

	public void expedientModificarGeoref(String processInstanceId, Double posx, Double posy, String referencia) {
		getDelegateService().expedientModificarGeoref(processInstanceId, posx, posy, referencia);
	}

	@Override
	public void expedientModificarGeoreferencia(String processInstanceId, String referencia) {
		getDelegateService().expedientModificarGeoreferencia(processInstanceId, referencia);
	}

	@Override
	public void expedientModificarGeoX(String processInstanceId, Double posx) {
		getDelegateService().expedientModificarGeoX(processInstanceId, posx);
	}

	@Override
	public void expedientModificarGeoY(String processInstanceId, Double posy) {
		getDelegateService().expedientModificarGeoY(processInstanceId, posy);
	}

	@Override
	public void expedientModificarDataInici(String processInstanceId, Date dataInici) {
		getDelegateService().expedientModificarDataInici(processInstanceId, dataInici);
	}


	public void expedientModificarGrup(String processInstanceId, String grupCodi) {
		getDelegateService().expedientModificarGrup(processInstanceId, grupCodi);
	}

	public void expedientModificarResponsable(String processInstanceId, String responsableCodi) {
		getDelegateService().expedientModificarResponsable(processInstanceId, responsableCodi);
	}

	@Override
	public void updateExpedientError(Long expedientId, Long jobId, String errorDesc, String errorFull) {
		getDelegateService().updateExpedientError(expedientId, jobId, errorDesc, errorFull);
	}

	@Override
    public ExpedientDadaDto getDadaPerProcessInstance(String processInstanceId, String codi) {
        return getDelegateService().getDadaPerProcessInstance(processInstanceId, codi);
    }

    public List<CampTascaDto> findCampsPerTaskInstance(String processInstanceId, String processDefinitionId,
			String taskName) {
		return getDelegateService().findCampsPerTaskInstance(processInstanceId, processDefinitionId, taskName);
	}

	public List<DocumentTascaDto> findDocumentsPerTaskInstance(String processInstanceId, String processDefinitionId,
			String taskName) {
		return getDelegateService().findDocumentsPerTaskInstance(processInstanceId, processDefinitionId, taskName);
	}

	public String getDadaPerTaskInstance(String processInstanceId, String taskInstanceId, String varCodi) {
		return getDelegateService().getDadaPerTaskInstance(processInstanceId, taskInstanceId, varCodi);
	}

	@Override
	public CampTascaDto getCampTascaPerInstanciaTasca(String taskName, String processDefinitionId, String processInstanceId, String varCodi) {
		return getDelegateService().getCampTascaPerInstanciaTasca(taskName, processDefinitionId, processInstanceId, varCodi);
	}

	public Long documentExpedientGuardar(String processInstanceId, String documentCodi, Date data, String arxiuNom,
			byte[] arxiuContingut) {
		return getDelegateService().documentExpedientGuardar(processInstanceId, documentCodi, data, arxiuNom, arxiuContingut);
	}

	public Long documentExpedientAdjuntar(String processInstanceId, String adjuntId, String adjuntTitol,
			Date adjuntData, String arxiuNom, byte[] arxiuContingut) {
		return getDelegateService().documentExpedientAdjuntar(processInstanceId, adjuntId, adjuntTitol, adjuntData, arxiuNom, arxiuContingut);
	}

	public TerminiDto getTerminiAmbProcessInstanceICodi(String processInstanceId, String terminiCodi) {
		return getDelegateService().getTerminiAmbProcessInstanceICodi(processInstanceId, terminiCodi);
	}

	public void terminiIniciar(String terminiCodi, String processInstanceId, Date data, Integer anys, Integer mesos,
			Integer dies, boolean esDataFi) {
		getDelegateService().terminiIniciar(terminiCodi, processInstanceId, data, anys, mesos, dies, esDataFi);
	}

	public void terminiPausar(Long terminiIniciatId, Date data) {
		getDelegateService().terminiPausar(terminiIniciatId, data);
	}

	public void terminiContinuar(Long terminiIniciatId, Date data) {
		getDelegateService().terminiContinuar(terminiIniciatId, data);
	}

	public Date terminiCalcularDataInici(Date inici, int anys, int mesos, int dies, boolean laborable,
			String processInstanceId) {
		return getDelegateService().terminiCalcularDataInici(inici, anys, mesos, dies, laborable, processInstanceId);
	}

	public List<FestiuDto> getFestiusAll() {
		return getDelegateService().getFestiusAll();
	}

	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(String processInstanceId, String usuariCodi) {
		return getDelegateService().findReassignacioActivaPerUsuariOrigen(processInstanceId, usuariCodi);
	}

	@Override
	public Integer getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(String jbpmKey, String processInstanceId) {
		return getDelegateService().getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(jbpmKey, processInstanceId);
	}

	@Override
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(String processInstanceId) {
		return getDelegateService().getDefinicioProcesPerProcessInstanceId(processInstanceId);
	}

	@Override
	public Long getDefinicioProcesIdPerProcessInstanceId(String processInstanceId) {
		return getDelegateService().getDefinicioProcesIdPerProcessInstanceId(processInstanceId);
	}

	@Override
	public Long getDefinicioProcesEntornAmbJbpmKeyIVersio(String jbpmKey, Integer version) {
		return getDelegateService().getDefinicioProcesEntornAmbJbpmKeyIVersio(jbpmKey, version);
	}

	@Override
	public Long getDarreraVersioEntornAmbEntornIJbpmKey(Long entornId, String jbpmKey) {
		return getDelegateService().getDarreraVersioEntornAmbEntornIJbpmKey(entornId, jbpmKey);
	}

	@Override
	public void initializeDefinicionsProces() {
		getDelegateService().initializeDefinicionsProces();
	}

	@Override
	public String getProcessDefinitionIdHeretadaAmbPid(String processInstanceId) {
		return getDelegateService().getProcessDefinitionIdHeretadaAmbPid(processInstanceId);
	}

	@Override
	public CampTipusIgnored getCampAndIgnored(String processDefinitionId, Long expedientId, String varCodi) {
		return getDelegateService().getCampAndIgnored(processDefinitionId, expedientId, varCodi);
	}
}
