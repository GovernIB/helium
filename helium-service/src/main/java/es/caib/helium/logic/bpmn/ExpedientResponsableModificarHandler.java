package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a modificar el responsable d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientResponsableModificarHandler implements es.caib.helium.bpmn.handler.ExpedientResponsableModificarHandler {

	private String responsableCodi;
	private String varResponsableCodi;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientResponsableModificar(heliumApi.getVariableDefaultValue(varResponsableCodi, responsableCodi));
	}

}
