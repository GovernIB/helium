package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a aturar la tramitació d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientAturarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientAturarHandler {

	private Expression motiu;
	private Expression varMotiu;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientAturar(
			heliumApi.getVariableDefaultValue(
				getValue(varMotiu),
				getValue(motiu))
		);
	}

}
