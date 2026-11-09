package es.caib.helium.logic.bpmn;

import org.flowable.common.engine.impl.el.FixedValue;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
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

	private FixedValue comentari;
	private FixedValue varComentari;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		log.debug("Inici execució handler modificació comentari expedient");
		String c = "comentari";
		//TODO:
//				(String)getValorOVariable(
//				executionContext,
//				comentari,
//				varComentari);
//		ExpedientDto expedient = getExpedientActual(executionContext);
//		log.debug("Modificant comentari de l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ", comentari=" + c + ")");
		try {
//			Jbpm3HeliumBridge.getInstanceService().expedientModificarComentari(
//					getProcessInstanceId(executionContext),
//					c);
			heliumApi.expedientComentariModificar(c);
		} catch (Exception ex) {
			throw new HeliumHandlerException("Error al modificar l'expedient", ex);
		}
		log.debug("Handler modificació de comentari finalitzat amb èxit");
	}

}
