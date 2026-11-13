package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import org.flowable.common.engine.api.delegate.Expression;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del handler per a modificar el comentari de l'expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
@Slf4j
public class ExpedientComentariModificarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.ExpedientComentariModificarHandler {

	private Expression comentari;
	private Expression varComentari;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		log.debug("Inici execució handler modificació comentari expedient");
		try {
			heliumApi.expedientComentariModificar(
				heliumApi.getVariableDefaultValue(
					getValue(varComentari),
					getValue(comentari))
			);
		} catch (Exception ex) {
			throw new HeliumHandlerException("Error al modificar l'expedient", ex);
		}
		log.debug("Handler modificació de comentari finalitzat amb èxit");
	}
}
