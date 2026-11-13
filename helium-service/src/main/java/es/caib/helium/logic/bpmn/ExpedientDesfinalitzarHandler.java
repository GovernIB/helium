package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a tornar a obrir un expedient finalitzat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientDesfinalitzarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientDesfinalitzarHandler {

	private Expression reprendre;
	private Expression varReprendre;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		Boolean reprendreValue = heliumApi.getVariableDefaultValueAsBoolean(
			getValue(varReprendre),
			getValueAsBoolean(reprendre));
		heliumApi.expedientDesfinalitzar(reprendreValue != null && reprendreValue);
	}

}
