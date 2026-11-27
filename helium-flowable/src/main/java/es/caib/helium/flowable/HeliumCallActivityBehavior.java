package es.caib.helium.flowable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.flowable.bpmn.model.CallActivity;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.javax.el.PropertyNotFoundException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.CallActivityBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.handler.HeliumBpmnXml;
import es.caib.helium.flowable.FlowableHeliumBpmn.Mapatge;
import es.caib.helium.logic.intf.bpmn.MotorHeliumService;
import lombok.extern.slf4j.Slf4j;

/**
 * Crida a un subprocés que, a més del comportament de Flowable, passa dades de l'expedient entre el procés i el
 * subprocés (les dades de Helium no són variables del motor: són per instància de procés a {@code ExpedientDades}).
 *
 * <ul>
 *   <li>{@code helium:entrada}: abans que el subprocés comenci (i, per tant, abans dels handlers que s'executen en
 *   iniciar-lo), copia la dada {@code origen} del procés (o el resultat de l'{@code expressio}, avaluada al procés) a
 *   la dada {@code desti} del subprocés.</li>
 *   <li>{@code helium:sortida}: quan el subprocés acaba, abans que el procés continuï, copia la dada {@code origen}
 *   del subprocés (o el resultat de l'{@code expressio}, avaluada al subprocés) a la dada {@code desti} del
 *   procés.</li>
 * </ul>
 *
 * <p>Equival al mapatge de variables {@code read} / {@code write} dels {@code process-state} de jBPM 3. Una dada
 * d'origen que no existeix (o una expressió que en fa referència) no es copia.</p>
 */
@Slf4j
public class HeliumCallActivityBehavior extends CallActivityBehavior {

	private static final long serialVersionUID = 1L;

	private final transient Supplier<MotorHeliumService> motorHeliumService;

	public HeliumCallActivityBehavior(CallActivity callActivity, Supplier<MotorHeliumService> motorHeliumService) {
		super(callActivity);
		this.motorHeliumService = motorHeliumService;
	}

	/**
	 * Crea el subprocés (Flowable) i hi copia les entrades. Quan {@code super.execute} acaba, el subprocés ja existeix
	 * però el seu primer pas només està planificat a l'agenda: s'executa quan acaba aquesta operació, i per tant
	 * després de copiar-hi les dades. ({@code initializeVariables} no serveix: Flowable només la crida si hi ha
	 * variables del motor per passar.)
	 */
	@Override
	public void execute(DelegateExecution execution) {
		super.execute(execution);
		List<Mapatge> entrades = FlowableHeliumBpmn.llegirMapatges(callActivity, HeliumBpmnXml.ELEMENT_ENTRADA);
		if (entrades.isEmpty()) {
			return;
		}
		ExecutionEntity subProcessInstance = ((ExecutionEntity) execution).getSubProcessInstance();
		if (subProcessInstance == null) {
			throw new FlowableException("No s'ha creat el subprocés de l'activitat de crida " + callActivity.getId());
		}
		copiar(entrades, execution, execution.getProcessInstanceId(), subProcessInstance.getId());
	}

	@Override
	public void completing(DelegateExecution execution, DelegateExecution subProcessInstance) throws Exception {
		List<Mapatge> sortides = FlowableHeliumBpmn.llegirMapatges(callActivity, HeliumBpmnXml.ELEMENT_SORTIDA);
		if (!sortides.isEmpty()) {
			copiar(sortides, subProcessInstance, subProcessInstance.getProcessInstanceId(), execution.getProcessInstanceId());
		}
		super.completing(execution, subProcessInstance);
	}

	/**
	 * Copia les dades indicades de la instància d'origen a la de destí. Primer es llegeixen tots els valors i després
	 * s'escriuen, de manera que una entrada no veu el resultat d'una altra.
	 */
	private void copiar(List<Mapatge> mapatges, VariableContainer contextOrigen, String processOrigen, String processDesti) {
		MotorHeliumService motor = motorHeliumService.get();
		Map<String, Object> dadesOrigen = motor.getDadesProces(processOrigen);
		Map<String, Object> valors = new java.util.LinkedHashMap<>();
		for (Mapatge mapatge : mapatges) {
			if (mapatge.getExpressio() != null) {
				Object valor = avaluar(mapatge.getExpressio(), contextOrigen);
				if (valor != null) {
					valors.put(mapatge.getDesti(), valor);
				}
			} else if (dadesOrigen != null && dadesOrigen.containsKey(mapatge.getOrigen())) {
				valors.put(mapatge.getDesti(), dadesOrigen.get(mapatge.getOrigen()));
			}
		}
		if (valors.isEmpty()) {
			return;
		}
		log.debug("Activitat de crida {}: es copien les dades {} de la instància {} a la {}",
			callActivity.getId(), valors.keySet(), processOrigen, processDesti);
		HeliumApi api = motor.getHeliumApi(processDesti);
		valors.forEach(api::setVariable);
	}

	/** Avalua l'expressió; si fa referència a una dada que no existeix, el resultat és null (no es copia). */
	private Object avaluar(String expressio, VariableContainer context) {
		try {
			return CommandContextUtil.getProcessEngineConfiguration().getExpressionManager()
				.createExpression(expressio)
				.getValue(context);
		} catch (FlowableException ex) {
			for (Throwable t = ex; t != null; t = t.getCause()) {
				if (t instanceof PropertyNotFoundException) {
					log.debug("Activitat de crida {}: l'expressió {} fa referència a una dada sense valor", callActivity.getId(), expressio);
					return null;
				}
			}
			throw ex;
		}
	}

}
