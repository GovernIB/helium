package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a modificar el grup d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class ExpedientGrupModificarHandler implements es.caib.helium.bpmn.handler.ExpedientGrupModificarHandler {

	private String grup;
	private String varGrup;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.expedientGrupModificar(heliumApi.getVariableDefaultValue(varGrup, grup));
	}

}
