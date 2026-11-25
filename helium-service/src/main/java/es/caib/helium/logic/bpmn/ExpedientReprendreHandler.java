package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;

/**
 * Implementació del handler per reprendre un expedient aturat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientReprendreHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientReprendreHandler {

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientReprendre();
	}

}
