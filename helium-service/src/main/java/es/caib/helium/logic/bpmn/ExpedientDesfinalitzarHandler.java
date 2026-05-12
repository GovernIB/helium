package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a tornar a obrir un expedient finalitzat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientDesfinalitzarHandler implements es.caib.helium.disseny.handler.ExpedientDesfinalitzarHandler {

	private String reprendre;
	private String varReprendre;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		Boolean reprendreValue = heliumApi.getVariableDefaultValueAsBoolean(varReprendre, reprendre);
		heliumApi.expedientDesfinalitzar(reprendreValue != null && reprendreValue);
	}

}
