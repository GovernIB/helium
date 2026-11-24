package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a modificar el títol d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientTitolModificarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientTitolModificarHandler {

	private String titol;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientTitolModificar(
			titol
		);
	}

}
