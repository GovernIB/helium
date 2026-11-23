package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;

/**
 * Implementació del handler per a crear una alerta a l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class AlertaCrearHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.AlertaCrearHandler {
	private String usuari;
	private String text;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.alertaCrear(
			usuari,
			text);
	}
}
