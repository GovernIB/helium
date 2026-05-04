package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a aturar la tramitació d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientAturarHandler implements es.caib.helium.bpmn.handler.ExpedientAturarHandler {

	private String motiu;
	private String varMotiu;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientAturar(
			heliumApi.getVariableDefaultValue(varMotiu, motiu));
	}

}
