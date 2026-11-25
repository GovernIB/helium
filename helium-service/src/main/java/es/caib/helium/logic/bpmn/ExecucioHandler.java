package es.caib.helium.logic.bpmn;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Context de l'execució d'un handler dins el motor de workflow.
 *
 * <p>L'adaptador del motor l'obre abans d'executar un handler i el tanca després. Mentrestant, les operacions de la
 * {@code HeliumApi} que envien una petició a un sistema extern i n'han d'esperar la resposta (portasignatures,
 * PINBAL asíncron...) criden {@link #esperarResposta()}: obtenen l'identificador de l'execució que s'ha de reprendre
 * quan arribi la resposta i el motor deixa l'execució aturada a la tasca del handler en lloc de continuar.</p>
 *
 * <p>Els contextos s'apilen per fil d'execució, perquè un handler pot provocar l'execució d'altres handlers (p. ex.
 * iniciant un altre expedient).</p>
 */
public final class ExecucioHandler {

	private static final ThreadLocal<Deque<ExecucioHandler>> PILA = ThreadLocal.withInitial(ArrayDeque::new);

	private final String executionId;
	private final String processInstanceId;
	private boolean esperaResposta;

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

	/** Context del handler que s'està executant en aquest fil, o null si no n'hi ha cap. */
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

	/**
	 * Indica que el handler espera la resposta d'un sistema extern: en acabar, el motor no continuarà fins que es
	 * reprengui l'execució retornada.
	 *
	 * @return l'identificador de l'execució que s'haurà de reprendre.
	 */
	public String esperarResposta() {
		this.esperaResposta = true;
		return executionId;
	}

	public boolean isEsperaResposta() {
		return esperaResposta;
	}

	public String getExecutionId() {
		return executionId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

}
