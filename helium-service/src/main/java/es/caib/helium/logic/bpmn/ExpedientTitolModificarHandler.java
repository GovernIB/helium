package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a modificar el títol d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientTitolModificarHandler implements es.caib.helium.bpmn.handler.ExpedientTitolModificarHandler {

	private String titol;
	private String varTitol;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientTitolModificar(heliumApi.getVariableDefaultValue(varTitol, titol));
	}

}
