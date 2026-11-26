package es.caib.helium.flowable;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.disseny.handler.ExpedientComentariModificarHandler;

/** Implementació de prova del handler predefinit (a Helium és a helium-service). */
public class ExpedientComentariModificarHandlerProva implements HeliumBpmnHandler, ExpedientComentariModificarHandler {

	private String comentari;

	@Override
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}

	@Override
	public void execute(HeliumApi heliumApi) {
		heliumApi.expedientComentariModificar(comentari);
	}

}
