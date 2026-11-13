package es.caib.helium.bpmn.service;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.model.TerminiInfo;

import java.util.Date;

/**
 * Servei per a enllaçar les llibreries BPM amb la funcionalitat
 * de Helium.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BpmnHeliumService {
	public HeliumApi buildHeliumApi(String processId, String taskId);
	public void configurarTerminiIniciatAmbDadesBpm(
		Long terminiIniciatId,
		String taskInstanceId,
		Long timerId);
	public Date terminiCalcularDataFi(
		Date inici,
		int anys,
		int mesos,
		int dies,
		boolean laborable,
		String processInstanceId);
	public Date terminiCalcularDataInici(
		Date fi,
		int anys,
		int mesos,
		int dies,
		boolean laborable,
		String processInstanceId);
	public TerminiInfo getTerminiIniciatAmbCodi(HeliumApi executionContext, String codi) throws BpmnException;
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
	public void terminiCancelar(
		Long terminiIniciatId,
		Date data);
	public void terminiPausar(
		Long terminiIniciatId,
		Date data);
	public void terminiContinuar(
		Long terminiIniciatId,
		Date data);

}
