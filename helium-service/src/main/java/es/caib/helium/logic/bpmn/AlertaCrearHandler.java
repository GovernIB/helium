package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a crear una alerta a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class AlertaCrearHandler implements es.caib.helium.bpmn.handler.AlertaCrearHandler {

	private String usuari;
	private String varUsuari;
	private String text;
	private String varText;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.alertaCrear(
			heliumApi.getVariableDefaultValue(varUsuari, usuari),
			heliumApi.getVariableDefaultValue(varText, text));
	}

}
