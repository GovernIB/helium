package net.conselldemallorca.helium.v3.core.ejb;

import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.v3.core.api.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
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
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class WorkflowBridgeServiceBean implements WorkflowBridgeService {

	@Autowired
	WorkflowBridgeService delegate;

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
		return delegate.findExpedientsConsultaGeneral(
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
		return delegate.findExpedientsConsultaDadesIndexades(entornId, expedientTipusCodi, filtreValors);
	}

	public ExpedientDto getExpedientAmbEntornITipusINumero(
            Long entornId,
            String expedientTipusCodi,
            String numero) {
		return delegate.getExpedientAmbEntornITipusINumero(entornId, expedientTipusCodi, numero);
	}

	public String getProcessInstanceIdAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) {
		return delegate.getProcessInstanceIdAmbEntornITipusINumero(entornId, expedientTipusCodi, numero);
	}

	@Override
	public ExpedientDto getExpedientArrelAmbProcessInstanceId(String processInstanceId) {
		return delegate.getExpedientArrelAmbProcessInstanceId(processInstanceId);
	}

	public void expedientRelacionar(
            Long expedientIdOrigen,
            Long expedientIdDesti) {
		delegate.expedientRelacionar(expedientIdOrigen, expedientIdDesti);
	}

	public void expedientAturar(
            String processInstanceId,
            String motiu) {
		delegate.expedientAturar(processInstanceId, motiu);
	}

	public void expedientReprendre(
            String processInstanceId) {
		delegate.expedientReprendre(processInstanceId);
	}

	public void desfinalitzarExpedient(String processInstanceId) {
		delegate.desfinalitzarExpedient(processInstanceId);
		
	}

	public void expedientModificarEstat(
            String processInstanceId,
            String estatCodi) {
		delegate.expedientModificarEstat(processInstanceId, estatCodi);
	}

	public void expedientModificarEstatId(
			String processInstanceId,
			Long estatId) {
		delegate.expedientModificarEstatId(processInstanceId, estatId);
	}

	public void expedientModificarComentari(
            String processInstanceId,
            String comentari) {
		delegate.expedientModificarComentari(processInstanceId, comentari);
	}

	public void expedientModificarNumero(
            String processInstanceId,
            String numero) {
		delegate.expedientModificarNumero(processInstanceId, numero);
	}


	// TASQUES
	////////////////////////////////////////////////////////////////////////////////

	public boolean isTascaEnSegonPla(Long taskId) {
		return delegate.isTascaEnSegonPla(taskId);
	}

	public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message) {
		delegate.addMissatgeExecucioTascaSegonPla(taskId, message);
	}

	public void setErrorTascaSegonPla(Long taskId, String error) {
		delegate.setErrorTascaSegonPla(taskId, error);
	}

	// DOCUMENTS
	////////////////////////////////////////////////////////////////////////////////

	public DocumentDissenyDto getDocumentDisseny(
            Long definicioProcesId,
            String processInstanceId,
            String documentCodi) {
		return delegate.getDocumentDisseny(definicioProcesId, processInstanceId, documentCodi);
	}

	public DocumentDto getDocumentInfo(Long documentStoreId) {
		return delegate.getDocumentInfo(documentStoreId);
	}

	public DocumentDto getDocumentInfo(Long documentStoreId,
                                       boolean ambContingutOriginal,
                                       boolean ambContingutSignat,
                                       boolean ambContingutVista,
                                       boolean perSignar,
                                       boolean perNotificar,
                                       boolean ambSegellSignatura) {
		return delegate.getDocumentInfo(
				documentStoreId,
				ambContingutOriginal,
				ambContingutSignat,
				ambContingutVista,
				perSignar,
				perNotificar,
				ambSegellSignatura);
	}

	public String getCodiVariablePerDocumentCodi(String documentCodi) {
		return delegate.getCodiVariablePerDocumentCodi(documentCodi);
	}

	public ArxiuDto getArxiuPerMostrar(Long documentStoreId) {
		return delegate.getArxiuPerMostrar(documentStoreId);
	}

	public ArxiuDto documentGenerarAmbPlantilla(
            String taskInstanceId,
            String processDefinitionId,
            String processInstanceId,
            String documentCodi,
            Date dataDocument) {
		return delegate.documentGenerarAmbPlantilla(taskInstanceId, processDefinitionId, processInstanceId, documentCodi, dataDocument);
	}

	@Override
	public Long documentExpedientCrear(String taskInstanceId, String processInstanceId, String documentCodi, Date documentData, boolean isAdjunt, String adjuntTitol, String arxiuNom, byte[] arxiuContingut) {
		return delegate.documentExpedientCrear(taskInstanceId, processInstanceId, documentCodi, documentData, isAdjunt, adjuntTitol, arxiuNom, arxiuContingut);
	}

	public void documentExpedientGuardarDadesRegistre(
            Long documentStoreId,
            String registreNumero,
            Date registreData,
            String registreOficinaCodi,
            String registreOficinaNom,
            boolean registreEntrada) {
		delegate.documentExpedientGuardarDadesRegistre(documentStoreId, registreNumero, registreData, registreOficinaCodi, registreOficinaNom, registreEntrada);
	}

	public void documentExpedientEsborrar(
            String taskInstanceId,
            String processInstanceId,
            String documentCodi) {
		delegate.documentExpedientEsborrar(taskInstanceId, processInstanceId, documentCodi);
	}

	// TERMINIS
	////////////////////////////////////////////////////////////////////////////////

	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
            String processDefinitionId,
            String processInstanceId,
            String terminiCodi) {
		return delegate.getTerminiIniciatAmbProcessInstanceITerminiCodi(processDefinitionId, processInstanceId, terminiCodi);
	}

	public void terminiCancelar(
            Long terminiIniciatId,
            Date data) {
		delegate.terminiCancelar(terminiIniciatId, data);
	}

	public Date terminiCalcularDataFi(
            Date inici,
            int anys,
            int mesos,
            int dies,
            boolean laborable,
            String processInstanceId) {
		return delegate.terminiCalcularDataFi(inici, anys, mesos, dies, laborable, processInstanceId);
	}

	public void configurarTerminiIniciatAmbDadesWf(
            Long terminiIniciatId,
            String taskInstanceId,
            Long timerId) {
		delegate.configurarTerminiIniciatAmbDadesWf(terminiIniciatId, taskInstanceId, timerId);
	}

	// ALERTES
	////////////////////////////////////////////////////////////////////////////////

	public void alertaCrear(
            Long entornId,
            Long expedientId,
            Date data,
            String usuariCodi,
            String text) {
		delegate.alertaCrear(entornId, expedientId, data, usuariCodi, text);
	}

	@Override
	public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId) {
		delegate.alertaEsborrarAmbTaskInstanceId(taskInstanceId);
	}

	// ENUMERACIONS
	////////////////////////////////////////////////////////////////////////////////

	public List<EnumeracioValorDto> enumeracioConsultar(
            String processInstanceId,
            String enumeracioCodi) {
		return delegate.enumeracioConsultar(processInstanceId, enumeracioCodi);
	}

	public void enumeracioSetValor(
            String processInstanceId,
            String enumeracioCodi,
            String codi,
            String valor) {
		delegate.enumeracioSetValor(processInstanceId, enumeracioCodi, codi, valor);
	}

	// ESTATS
	////////////////////////////////////////////////////////////////////////////////

	public EstatDto findEstatAmbEntornIExpedientTipusICodi(
            Long entornId,
            String expedientTipusCodi,
            String estatCodi) {
		return delegate.findEstatAmbEntornIExpedientTipusICodi(entornId, expedientTipusCodi, estatCodi);
	}

	// DOMINIS
	////////////////////////////////////////////////////////////////////////////////

	public List<DominiRespostaFilaDto> dominiConsultar(
            String processInstanceId,
            String dominiCodi,
            String dominiId,
            Map<String, Object> parametres) {
		return delegate.dominiConsultar(processInstanceId, dominiCodi, dominiId, parametres);
	}

	// INTERESSATS
	////////////////////////////////////////////////////////////////////////////////

	public void interessatCrear(InteressatDto interessat) {
		delegate.interessatCrear(interessat);
	}

	public void interessatModificar(InteressatDto interessat) {
		delegate.interessatModificar(interessat);
	}

	public void interessatEliminar(String interessatCodi, Long expedientId) {
		delegate.interessatEliminar(interessatCodi, expedientId);
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
		delegate.emailSend(fromAddress, recipients, ccRecipients, bccRecipients, subject, text, attachments);
	}

	public String getHeliumProperty(String propertyName) {
		return delegate.getHeliumProperty(propertyName);
	}

	public String getUsuariCodiActual() {
		return delegate.getUsuariCodiActual();
	}

	public ExpedientDto getExpedientIniciant() {
		return delegate.getExpedientIniciant();
	}

	public void finalitzarExpedient(
			String processInstanceId,
			Date dataFinalitzacio) {
		delegate.finalitzarExpedient(
				processInstanceId,
				dataFinalitzacio);
	}

	public void expedientModificarTitol(String processInstanceId, String titol) {
		delegate.expedientModificarTitol(processInstanceId, titol);
	}

	public void expedientModificarGeoref(String processInstanceId, Double posx, Double posy, String referencia) {
		delegate.expedientModificarGeoref(processInstanceId, posx, posy, referencia);
	}

	@Override
	public void expedientModificarGeoreferencia(String processInstanceId, String referencia) {
		delegate.expedientModificarGeoreferencia(processInstanceId, referencia);
	}

	@Override
	public void expedientModificarGeoX(String processInstanceId, Double posx) {
		delegate.expedientModificarGeoX(processInstanceId, posx);
	}

	@Override
	public void expedientModificarGeoY(String processInstanceId, Double posy) {
		delegate.expedientModificarGeoY(processInstanceId, posy);
	}

	@Override
	public void expedientModificarDataInici(String processInstanceId, Date dataInici) {
		delegate.expedientModificarDataInici(processInstanceId, dataInici);
	}


	public void expedientModificarGrup(String processInstanceId, String grupCodi) {
		delegate.expedientModificarGrup(processInstanceId, grupCodi);
	}

	public void expedientModificarResponsable(String processInstanceId, String responsableCodi) {
		delegate.expedientModificarResponsable(processInstanceId, responsableCodi);
	}

	@Override
	public void updateExpedientError(Long expedientId, Long jobId, String errorDesc, String errorFull) {
		delegate.updateExpedientError(expedientId, jobId, errorDesc, errorFull);
	}

	@Override
    public ExpedientDadaDto getDadaPerProcessInstance(String processInstanceId, String codi) {
        return delegate.getDadaPerProcessInstance(processInstanceId, codi);
    }

    public List<CampTascaDto> findCampsPerTaskInstance(String processInstanceId, String processDefinitionId,
			String taskName) {
		return delegate.findCampsPerTaskInstance(processInstanceId, processDefinitionId, taskName);
	}

	public List<DocumentTascaDto> findDocumentsPerTaskInstance(String processInstanceId, String processDefinitionId,
			String taskName) {
		return delegate.findDocumentsPerTaskInstance(processInstanceId, processDefinitionId, taskName);
	}

	public TascaDadaDto getDadaPerTaskInstance(String processInstanceId, String taskInstanceId, String varCodi) {
		return delegate.getDadaPerTaskInstance(processInstanceId, taskInstanceId, varCodi);
	}

	@Override
	public CampTascaDto getCampTascaPerInstanciaTasca(String taskName, String processDefinitionId, String processInstanceId, String varCodi) {
		return delegate.getCampTascaPerInstanciaTasca(taskName, processDefinitionId, processInstanceId, varCodi);
	}

	public Long documentExpedientGuardar(String processInstanceId, String documentCodi, Date data, String arxiuNom,
			byte[] arxiuContingut) {
		return delegate.documentExpedientGuardar(processInstanceId, documentCodi, data, arxiuNom, arxiuContingut);
	}

	public Long documentExpedientAdjuntar(String processInstanceId, String adjuntId, String adjuntTitol,
			Date adjuntData, String arxiuNom, byte[] arxiuContingut) {
		return delegate.documentExpedientAdjuntar(processInstanceId, adjuntId, adjuntTitol, adjuntData, arxiuNom, arxiuContingut);
	}

	public TerminiDto getTerminiAmbProcessInstanceICodi(String processInstanceId, String terminiCodi) {
		return delegate.getTerminiAmbProcessInstanceICodi(processInstanceId, terminiCodi);
	}

	public void terminiIniciar(String terminiCodi, String processInstanceId, Date data, Integer anys, Integer mesos,
			Integer dies, boolean esDataFi) {
		delegate.terminiIniciar(terminiCodi, processInstanceId, data, anys, mesos, dies, esDataFi);
	}

	public void terminiPausar(Long terminiIniciatId, Date data) {
		delegate.terminiPausar(terminiIniciatId, data);
	}

	public void terminiContinuar(Long terminiIniciatId, Date data) {
		delegate.terminiContinuar(terminiIniciatId, data);
	}

	public Date terminiCalcularDataInici(Date inici, int anys, int mesos, int dies, boolean laborable,
			String processInstanceId) {
		return delegate.terminiCalcularDataInici(inici, anys, mesos, dies, laborable, processInstanceId);
	}

	public List<FestiuDto> getFestiusAll() {
		return delegate.getFestiusAll();
	}

	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(String processInstanceId, String usuariCodi) {
		return delegate.findReassignacioActivaPerUsuariOrigen(processInstanceId, usuariCodi);
	}

	@Override
	public Integer getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(String jbpmKey, String processInstanceId) {
		return delegate.getDefinicioProcesVersioAmbJbpmKeyIProcessInstanceId(jbpmKey, processInstanceId);
	}

	@Override
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(String processInstanceId) {
		return delegate.getDefinicioProcesPerProcessInstanceId(processInstanceId);
	}

	@Override
	public Long getDefinicioProcesIdPerProcessInstanceId(String processInstanceId) {
		return delegate.getDefinicioProcesIdPerProcessInstanceId(processInstanceId);
	}

	@Override
	public Long getDefinicioProcesEntornAmbJbpmKeyIVersio(String jbpmKey, Integer version) {
		return delegate.getDefinicioProcesEntornAmbJbpmKeyIVersio(jbpmKey, version);
	}

	@Override
	public Long getDarreraVersioEntornAmbEntornIJbpmKey(Long entornId, String jbpmKey) {
		return delegate.getDarreraVersioEntornAmbEntornIJbpmKey(entornId, jbpmKey);
	}

	@Override
	public void initializeDefinicionsProces() {
		delegate.initializeDefinicionsProces();
	}

	@Override
	public String getProcessDefinitionIdHeretadaAmbPid(String processInstanceId) {
		return delegate.getProcessDefinitionIdHeretadaAmbPid(processInstanceId);
	}

	@Override
	public CampTipusIgnored getCampAndIgnored(String processDefinitionId, Long expedientId, String varCodi) {
		return delegate.getCampAndIgnored(processDefinitionId, expedientId, varCodi);
	}
}
