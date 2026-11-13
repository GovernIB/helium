package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a modificar el responsable d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientResponsableModificarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientResponsableModificarHandler {

	private Expression responsableCodi;
	private Expression varResponsableCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientResponsableModificar(
			heliumApi.getVariableDefaultValue(
				getValue(varResponsableCodi),
				getValue(responsableCodi))
		);
	}

}
