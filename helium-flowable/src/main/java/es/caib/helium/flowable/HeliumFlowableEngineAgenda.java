package es.caib.helium.flowable;

import java.util.HashMap;
import java.util.Map;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.FlowableEngineAgenda;
import org.flowable.engine.FlowableEngineAgendaFactory;
import org.flowable.engine.impl.agenda.DefaultFlowableEngineAgenda;
import org.flowable.engine.impl.agenda.TakeOutgoingSequenceFlowsOperation;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;

/**
 * Agenda de Flowable que permet sortir d'una activitat per una transició concreta.
 *
 * <p>Amb jBPM 3 una tasca es completava indicant la transició de sortida. En BPMN, quan una activitat acaba,
 * Flowable segueix tots els <i>sequence flows</i> de sortida que no tenen condició o que la compleixen. Per
 * mantenir el comportament de Helium, abans de completar la tasca es registra al context de la comanda quin
 * <i>sequence flow</i> ha triat l'usuari per a l'execució de la tasca ({@link #seleccionarTransicio}). Quan
 * l'activitat surt, aquesta agenda només segueix aquell flux, sense avaluar-ne la condició (ha estat una
 * elecció explícita de l'usuari).</p>
 *
 * <p>La resta del comportament de sortida és el de Flowable: s'executen els <i>listeners</i> de fi
 * d'activitat, es registra la fi a l'historial, s'eliminen els esdeveniments de frontera i es respecta
 * l'<i>asynchronous leave</i>.</p>
 */
public class HeliumFlowableEngineAgenda extends DefaultFlowableEngineAgenda {

	/** Atribut del context de la comanda amb el mapa {@code executionId → SequenceFlow} triat. */
	static final String ATRIBUT_TRANSICIONS = "helium.transicionsSeleccionades";

	public HeliumFlowableEngineAgenda(CommandContext commandContext) {
		super(commandContext);
	}

	/**
	 * Indica que, quan l'execució surti de l'activitat on és ara, ha de seguir només el flux indicat. Només
	 * té efecte dins la mateixa comanda de Flowable.
	 */
	@SuppressWarnings("unchecked")
	public static void seleccionarTransicio(
			CommandContext commandContext,
			String executionId,
			SequenceFlow transicio) {
		Map<String, SequenceFlow> transicions = (Map<String, SequenceFlow>) commandContext.getAttribute(ATRIBUT_TRANSICIONS);
		if (transicions == null) {
			transicions = new HashMap<>();
			commandContext.addAttribute(ATRIBUT_TRANSICIONS, transicions);
		}
		transicions.put(executionId, transicio);
	}

	@Override
	public void planTakeOutgoingSequenceFlowsOperation(ExecutionEntity execution, boolean evaluateConditions) {
		SequenceFlow transicio = consumirTransicio(execution);
		if (transicio != null) {
			planOperation(new SortirPerTransicioOperation(commandContext, execution, transicio, false), execution);
		} else {
			super.planTakeOutgoingSequenceFlowsOperation(execution, evaluateConditions);
		}
	}

	@Override
	public void planTakeOutgoingSequenceFlowsSynchronousOperation(ExecutionEntity execution, boolean evaluateConditions) {
		SequenceFlow transicio = consumirTransicio(execution);
		if (transicio != null) {
			planOperation(new SortirPerTransicioOperation(commandContext, execution, transicio, true), execution);
		} else {
			super.planTakeOutgoingSequenceFlowsSynchronousOperation(execution, evaluateConditions);
		}
	}

	/** Retorna (i esborra) la transició seleccionada si l'execució surt de l'activitat origen d'aquesta. */
	@SuppressWarnings("unchecked")
	private SequenceFlow consumirTransicio(ExecutionEntity execution) {
		Map<String, SequenceFlow> transicions = (Map<String, SequenceFlow>) commandContext.getAttribute(ATRIBUT_TRANSICIONS);
		if (transicions == null || execution == null) {
			return null;
		}
		SequenceFlow transicio = transicions.get(execution.getId());
		FlowElement actual = execution.getCurrentFlowElement();
		if (transicio == null || !(actual instanceof FlowNode) || !actual.getId().equals(transicio.getSourceRef())) {
			return null;
		}
		transicions.remove(execution.getId());
		return transicio;
	}

	/** Factoria que registra l'agenda a la configuració del motor. */
	public static class Factory implements FlowableEngineAgendaFactory {
		@Override
		public FlowableEngineAgenda createAgenda(CommandContext commandContext) {
			return new HeliumFlowableEngineAgenda(commandContext);
		}
	}

	/** Operació de sortida que només segueix la transició seleccionada. */
	static class SortirPerTransicioOperation extends TakeOutgoingSequenceFlowsOperation {

		private final SequenceFlow transicio;

		SortirPerTransicioOperation(
				CommandContext commandContext,
				ExecutionEntity execution,
				SequenceFlow transicio,
				boolean forcedSynchronous) {
			super(commandContext, execution, false, forcedSynchronous);
			this.transicio = transicio;
		}

		@Override
		protected void handleAsynchronousLeave(FlowElement currentFlowElement, FlowNode sourceFlowNode) {
			// El job de sortida asíncrona continuarà directament per la transició seleccionada
			super.handleAsynchronousLeave(transicio, sourceFlowNode);
		}

		@Override
		protected void leaveFlowNode(FlowNode flowNode) {
			execution.setCurrentFlowElement(transicio);
			execution.setActive(false);
			agenda.planContinueProcessOperation(execution);
		}
	}

}
