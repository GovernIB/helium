package es.caib.helium.logic.bpmn;

import java.util.Date;

import es.caib.helium.commons.dto.TerminiDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.BpmnException;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import es.caib.helium.disseny.handler.HeliumBpmnHandler;
import es.caib.helium.logic.helper.AlertaHelper;
import es.caib.helium.logic.helper.DocumentHelperV3;
import es.caib.helium.logic.helper.ExpedientDadaHelper;
import es.caib.helium.logic.helper.ExpedientDocumentHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientInteressatHelper;
import es.caib.helium.logic.helper.MailHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.disseny.model.TerminiInfo;
import es.caib.helium.persistence.common.bpmn.DominiCodiDescripcio;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 * Handler base amb accés a la funcionalitat de Helium
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
abstract class AbstractHeliumActionHandler implements JavaDelegate, HeliumBpmnHandler {

	@Override
	public void execute(DelegateExecution execution) {
		try {
			this.execute(buildHeliumApi(execution));
		} catch (BpmnException e) {
			throw new RuntimeException(e);
		}
	}

	private HeliumApi buildHeliumApi(DelegateExecution execution) {
		ApplicationContext context = GlobalProperties.getApplicationContext();
		ExpedientHelper expedientHelper = context.getBean(ExpedientHelper.class);
		return HeliumApiFactory.createInstance(
			expedientHelper.findExpedientByProcessInstanceId(execution.getProcessInstanceId()),
			execution.getProcessInstanceId(),
			null,
			expedientHelper,
			context.getBean(ExpedientDadaHelper.class),
			context.getBean(ExpedientDocumentHelper.class),
			context.getBean(DocumentHelperV3.class),
			context.getBean(PluginHelper.class),
			context.getBean(AlertaHelper.class),
			context.getBean(ExpedientInteressatHelper.class),
			context.getBean(MailHelper.class),
			execution);
	}

	@Override
	public abstract void execute(HeliumApi heliumApi) throws HeliumHandlerException, BpmnException;

	protected Object getValorOVariable(HeliumApi executionContext, Object value, String var) {
		if (value != null)
			return value;
		if (var != null && !var.isEmpty()) {
			Object returnVal;
			if (executionContext.getVariable(var) instanceof DominiCodiDescripcio)
				returnVal = ((DominiCodiDescripcio)executionContext.getVariable(var)).getCodi();
			else
				returnVal = executionContext.getVariable(var);
			return returnVal;
		}
		return null;
	}

	TerminiInfo getTerminiIniciatAmbCodi(HeliumApi executionContext, String codi) throws BpmnException {
		TerminiDto termini = getTerminiAmbCodi(
			executionContext,
			codi);
		if (termini != null) {
			return executionContext.getTerminiIniciatAmbProcessInstanceITerminiCodi(
				getProcessInstanceId(executionContext),
				termini.getCodi());
		} else {
			return null;
		}
	}

	protected String getProcessInstanceId(HeliumApi executionContext) {
		return executionContext.getProcessInstance().getId();
	}

	protected String getTaskInstanceId(HeliumApi executionContext) {
		return executionContext.getTaskInstance().getId();
	}

	protected Date getVariableComData(HeliumApi executionContext,
	                                  String var) throws BpmnException {
		Object obj = executionContext.getVariable(var);
		if (obj == null)
			throw new NoTrobatException(Date.class, var);
		if (obj instanceof Date)
			return (Date) obj;
		throw new BpmnException("La variable amb el codi '" + var + "' no és de tipus Date");
	}

	protected TerminiDto getTerminiAmbCodi(HeliumApi executionContext, String codi) throws BpmnException {
		try {
			return HeliumBridge.getInstanceService().getTerminiAmbProcessInstanceICodi(
				getProcessInstanceId(executionContext),
				codi);
		} catch (Exception ex) {
			throw new BpmnException("No s'ha trobat el termini amb codi: " + codi);
		}
	}
}
