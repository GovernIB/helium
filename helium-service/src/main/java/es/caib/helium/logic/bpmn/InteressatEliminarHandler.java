package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a eliminar interessats d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class InteressatEliminarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.InteressatEliminarHandler {

	private Expression codi;
	private Expression varCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.interessatEliminar(
			heliumApi.getVariableDefaultValue(
				getValue(varCodi),
				getValue(codi))
		);
	}

}
