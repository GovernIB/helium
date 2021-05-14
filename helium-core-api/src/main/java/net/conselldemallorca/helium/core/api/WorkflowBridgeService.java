package net.conselldemallorca.helium.core.api;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;

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


	// TASQUES
	////////////////////////////////////////////////////////////////////////////////

	public boolean isTascaEnSegonPla(Long taskId);

	public void addMissatgeExecucioTascaSegonPla(Long taskId, String[] message);

	public void setErrorTascaSegonPla(Long taskId, Exception ex);

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

	public void terminiCancelar(
			Long terminiIniciatId,
			Date data);

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

}
