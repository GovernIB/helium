package es.caib.helium.logic.bpmn;

import es.caib.helium.logic.helper.PluginHelper;

/**
 * Implementació del handler que verifica a PINBAL les dades d'identitat del titular (SVDDGPVIWS02).
 *
 * @see AbstractPinbalHandler
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PinbalSvddgpviws02Handler extends AbstractPinbalHandler implements es.caib.helium.disseny.handler.PinbalSvddgpviws02Handler {

	@Override
	protected String getServeiCodi() {
		return PluginHelper.serveiVerificacioDades;
	}

}
