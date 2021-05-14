package net.conselldemallorca.helium.v3.core.ejb;

import net.conselldemallorca.helium.core.api.WorkflowBridgeService;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
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

	public List<ExpedientDto> findExpedientsConsultaGeneral(
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

	public List<ExpedientDto> findExpedientsConsultaDadesIndexades(
            Long entornId,
            String expedientTipusCodi,
            Map<String, Object> filtreValors) {
		return delegate.findExpedientsConsultaDadesIndexades(entornId, expedientTipusCodi, filtreValors);
	}

	public ExpedientDto getExpedientAmbEntornITipusINumero(
            Long entornId,
            String expedientTipusCodi,
            String numero) {
		return delegate.getExpedientAmbEntornITipusINumero(entornId, expedientTipusCodi, numero);
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

	public void expedientModificarEstat(
            String processInstanceId,
            String estatCodi) {
		delegate.expedientModificarEstat(processInstanceId, estatCodi);
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

	public void setErrorTascaSegonPla(Long taskId, Exception ex) {
		delegate.setErrorTascaSegonPla(taskId, ex);
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

	public void interessatEliminar(InteressatDto interessat) {
		delegate.interessatEliminar(interessat);
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

	@Override
	public ExpedientDto getExpedientIniciant() {
		return delegate.getExpedientIniciant();
	}
}
