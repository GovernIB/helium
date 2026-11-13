package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;

/**
 * Implementació del handler per a crear una alerta a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class AlertaCrearHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.AlertaCrearHandler {
	private Expression usuari;
	private Expression varUsuari;
	private Expression text;
	private Expression varText;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.alertaCrear(
			heliumApi.getVariableDefaultValue(getValue(varUsuari), getValue(usuari)),
			heliumApi.getVariableDefaultValue(getValue(varText), getValue(text)));
	}
}
