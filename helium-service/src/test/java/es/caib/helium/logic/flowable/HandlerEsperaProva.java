package es.caib.helium.logic.flowable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.logic.bpmn.ExecucioHandler;

/**
 * Handler de prova que, com el de portasignatures, envia una petició a un sistema extern i n'espera la resposta.
 * Guarda l'execució que s'haurà de reprendre.
 */
public class HandlerEsperaProva implements HeliumBpmnHandler {

	static final List<String> EXECUCIONS_EN_ESPERA = new CopyOnWriteArrayList<>();

	@Override
	public void execute(HeliumApi heliumApi) {
		EXECUCIONS_EN_ESPERA.add(ExecucioHandler.actual().esperarResposta());
	}

}
