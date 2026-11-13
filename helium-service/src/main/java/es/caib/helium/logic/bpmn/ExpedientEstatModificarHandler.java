package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a modificar l'estat de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientEstatModificarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientEstatModificarHandler {

	private Expression estatCodi;
	private Expression varEstatCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientEstatModificar(
			heliumApi.getVariableDefaultValue(
				getValue(varEstatCodi),
				getValue(estatCodi)));
	}
}
