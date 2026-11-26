package es.caib.helium.flowable;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.disseny.handler.ExpedientAturarHandler;

/** Implementació de prova del handler predefinit (a Helium és a helium-service). */
public class ExpedientAturarHandlerProva implements HeliumBpmnHandler, ExpedientAturarHandler {

	private String motiu;

	@Override
	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}

	@Override
	public void execute(HeliumApi heliumApi) {
		heliumApi.expedientAturar(motiu);
	}

}
