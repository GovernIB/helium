package es.caib.helium.flowable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.logic.intf.bpmn.ExecucioHandler;

/**
 * Handler de prova que, com el de portasignatures, envia una petició a un sistema extern amb resposta asíncrona.
 * Guarda l'execució del handler, que és la que s'haurà de reprendre quan arribi la resposta.
 */
public class HandlerEsperaProva implements HeliumBpmnHandler {

	static final List<String> EXECUCIONS_EN_ESPERA = new CopyOnWriteArrayList<>();

	@Override
	public void execute(HeliumApi heliumApi) {
		EXECUCIONS_EN_ESPERA.add(ExecucioHandler.actual().getExecutionId());
	}

}
