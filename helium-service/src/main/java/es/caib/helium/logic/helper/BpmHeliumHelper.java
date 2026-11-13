package es.caib.helium.logic.helper;

import es.caib.helium.commons.dto.TerminiDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.engine.WProcessInstance;
import es.caib.helium.bpmn.exception.BpmnException;
import es.caib.helium.bpmn.model.TerminiInfo;
import es.caib.helium.logic.bpmn.HeliumApiFactory;
import es.caib.helium.bpmn.service.BpmnHeliumService;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.entity.*;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class BpmHeliumHelper implements BpmnHeliumService {

	@Autowired
	private DefinicioProcesRepository definicioProcesRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ExpedientDadaHelper expedientDadaHelper;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private WorkflowEngineApi workflowEngineApi;
	@Autowired
	private TerminiHelper terminiHelper;
	@Autowired
	private MailHelper mailHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private AlertaHelper alertaHelper;
	@Autowired
	private ExpedientDocumentHelper documentHelper;
	@Autowired
	private DocumentHelperV3 documentHelperV3;
	@Autowired
	private ExpedientInteressatHelper expedientInteressatHelper;

	@Override
	public HeliumApi buildHeliumApi(String processId, String taskId) {
		return HeliumApiFactory.createInstance(
			expedientHelper.findExpedientByProcessInstanceId(processId),
			processId,
			taskId,
			expedientHelper,
			expedientDadaHelper,
			documentHelper,
			documentHelperV3,
			pluginHelper,
			alertaHelper,
			expedientInteressatHelper,
			mailHelper);
	}

	@Override
	public TerminiInfo getTerminiIniciatAmbCodi(HeliumApi executionContext, String codi) throws BpmnException {
		TerminiDto termini = getTerminiAmbProcessInstanceICodi(
			executionContext.getProcessInstance().getId(),
			codi);
		if (termini != null) {
			return executionContext.getTerminiIniciatAmbProcessInstanceITerminiCodi(
				executionContext.getProcessInstance().getId(),
				termini.getCodi());
		} else {
			return null;
		}
	}

	private TerminiDto getTerminiAmbProcessInstanceICodi(
		String processInstanceId,
		String terminiCodi) {
		log.debug("Obtenint termini donada la instància de procés i el codi (" +
			"processInstanceId=" + processInstanceId + "," +
			"terminiCodi=" + terminiCodi + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Termini termini = null;
		if (expedient.getTipus().isAmbInfoPropia()) {
			termini = terminiHelper.findAmbExpedientTipusICodi(
				expedient.getTipus(),
				terminiCodi);
		} else {
			DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
			termini = terminiHelper.findAmbDefinicioProcesICodi(
				definicioProces,
				terminiCodi);
		}
		if (termini == null)
			throw new NoTrobatException(Termini.class, terminiCodi);
		return conversioTipusHelper.convertir(
			termini,
			TerminiDto.class);
	}

	private DefinicioProces getDefinicioProcesDonatProcessInstanceId(
		String processInstanceId) {
		WProcessInstance processInstance = workflowEngineApi.getProcessInstance(processInstanceId);
		if (processInstance == null)
			throw new NoTrobatException(WProcessInstance.class, processInstanceId);

		return definicioProcesRepository.findByBpmId(
			processInstance.getProcessDefinitionId());
	}

	@Override
	public void configurarTerminiIniciatAmbDadesBpm(
		Long terminiIniciatId,
		String taskInstanceId,
		Long timerId) {
		log.debug("Configurant termini iniciat (" +
			"terminiIniciatId=" + terminiIniciatId + ", " +
			"taskInstanceId=" + taskInstanceId + ", " +
			"timerId=" + timerId + ")");
		TerminiIniciat terminiIniciat = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (terminiIniciat == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		terminiIniciat.setTaskInstanceId(taskInstanceId);
		if (timerId != null)
			terminiIniciat.afegirTimerId(timerId);
	}

	@Override
	public Date terminiCalcularDataInici(
		Date fi,
		int anys,
		int mesos,
		int dies,
		boolean laborable,
		String processInstanceId) {
		log.debug("Calculant data d'inici de termini a partir d'una data de fi (" +
			"fi=" + fi + ", " +
			"anys=" + anys + ", " +
			"mesos=" + mesos + ", " +
			"dies=" + dies + ", " +
			"laborable=" + laborable + ")");
		return terminiHelper.getDataIniciTermini(
			fi,
			anys,
			mesos,
			dies,
			laborable,
			processInstanceId);
	}

	@Override
	public Date terminiCalcularDataFi(
		Date inici,
		int anys,
		int mesos,
		int dies,
		boolean laborable,
		String processInstanceId) {
		log.debug("Calculant data de fi de termini a partir d'una data d'inici (" +
			"inici=" + inici + ", " +
			"anys=" + anys + ", " +
			"mesos=" + mesos + ", " +
			"dies=" + dies + ", " +
			"laborable=" + laborable + ")");
		return terminiHelper.getDataFiTermini(inici, anys, mesos, dies, laborable, processInstanceId);
	}

	@Override
	public void terminiIniciar(
		String terminiCodi,
		String processInstanceId,
		Date data,
		int anys,
		int mesos,
		int dies,
		boolean esDataFi) {
		log.debug("Iniciant termini (" +
			"terminiCodi=" + terminiCodi + ", " +
			"processInstanceId=" + processInstanceId + ", " +
			"data=" + data + ", " +
			"anys=" + anys + ", " +
			"mesos=" + mesos + ", " +
			"dies=" + dies + ", " +
			"esDataFi=" + esDataFi + ")");

		Termini termini = this.getTermini(processInstanceId, terminiCodi);
		terminiHelper.iniciar(
			termini.getId(),
			processInstanceId,
			data,
			anys,
			mesos,
			dies,
			esDataFi,
			false);

	}


	@Override
	public void terminiIniciar(
		String terminiCodi,
		String processInstanceId,
		Date data,
		boolean esDataFi) {
		log.debug("Iniciant termini (" +
			"terminiCodi=" + terminiCodi + ", " +
			"processInstanceId=" + processInstanceId + ", " +
			"data=" + data + ", " +
			"esDataFi=" + esDataFi + ")");
		Termini termini = this.getTermini(processInstanceId, terminiCodi);
		terminiHelper.iniciar(
			termini.getId(),
			processInstanceId,
			data,
			esDataFi,
			false);
	}

	private Termini getTermini(String processInstanceId, String terminiCodi) {

		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		ExpedientTipus expedientTipus = definicioProces.getExpedientTipus() != null?
			definicioProces.getExpedientTipus()
			: null;
		Termini termini = null;
		if (expedientTipus != null && expedientTipus.isAmbInfoPropia()) {
			termini = terminiHelper.findAmbExpedientTipusICodi(
				expedientTipus,
				terminiCodi);
		} else {
			termini = terminiHelper.findAmbDefinicioProcesICodi(
				definicioProces,
				terminiCodi);
		}
		if (termini == null)
			throw new NoTrobatException(Termini.class, terminiCodi);
		return termini;

	}

	@Override
	public void terminiCancelar(
		Long terminiIniciatId,
		Date data) {
		log.debug("Cancelant termini iniciat (" +
			"terminiIniciatId=" + terminiIniciatId + ", " +
			"data=" + data + ")");
		TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (termini == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		terminiHelper.cancelar(terminiIniciatId, data, false);
	}

	@Override
	public void terminiPausar(
		Long terminiIniciatId,
		Date data) {
		log.debug("Pausant termini iniciat (" +
			"terminiIniciatId=" + terminiIniciatId + ", " +
			"data=" + data + ")");
		TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (termini == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		terminiHelper.pausar(terminiIniciatId, data, false);
	}

	@Override
	public void terminiContinuar(
		Long terminiIniciatId,
		Date data) {
		log.debug("Continuant termini iniciat (" +
			"terminiIniciatId=" + terminiIniciatId + ", " +
			"data=" + data + ")");
		TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (termini == null)
			throw new NoTrobatException(TerminiIniciat.class, terminiIniciatId);
		terminiHelper.continuar(terminiIniciatId, data, false);
	}


}
