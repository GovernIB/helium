package es.caib.helium.logic.bpmn;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Context de l'execució d'un handler dins el motor de workflow.
 *
 * <p>L'adaptador del motor l'obre abans d'executar un handler i el tanca després. Les operacions de la
 * {@code HeliumApi} que envien una petició a un sistema extern amb resposta asíncrona (portasignatures, PINBAL
 * asíncron) en desen l'execució ({@link #getExecutionId()}). Com el token de jBPM que s'aturava al node d'estat,
 * aquesta execució continua fins a la <i>receive task</i> que hi ha després de la tasca del handler i s'hi atura; quan
 * arriba la resposta, es reprèn amb {@code WorkflowEngineApi.signalToken}.</p>
 *
 * <p>Els contextos s'apilen per fil d'execució, perquè un handler pot provocar l'execució d'altres handlers (p. ex.
 * iniciant un altre expedient).</p>
 */
public final class ExecucioHandler {

	private static final ThreadLocal<Deque<ExecucioHandler>> PILA = ThreadLocal.withInitial(ArrayDeque::new);

	private final String executionId;
	private final String processInstanceId;

	private ExecucioHandler(String executionId, String processInstanceId) {
		this.executionId = executionId;
		this.processInstanceId = processInstanceId;
	}

	/** Obre el context de l'execució d'un handler. S'ha de tancar amb {@link #tancar()} (en un {@code finally}). */
	public static ExecucioHandler obrir(String executionId, String processInstanceId) {
		ExecucioHandler execucio = new ExecucioHandler(executionId, processInstanceId);
		PILA.get().push(execucio);
		return execucio;
	}

	/** Context del handler que s'està executant en aquest fil, o null si no n'hi ha cap (p. ex. una acció). */
	public static ExecucioHandler actual() {
		return PILA.get().peek();
	}

	/** Tanca aquest context. */
	public void tancar() {
		Deque<ExecucioHandler> pila = PILA.get();
		pila.remove(this);
		if (pila.isEmpty()) {
			PILA.remove();
		}
	}

	/** Execució on s'executa el handler. */
	public String getExecutionId() {
		return executionId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

}
