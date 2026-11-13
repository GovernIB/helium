package es.caib.helium.logic.bpmn;

import es.caib.helium.bpmn.handler.AbstractHeliumActionHandler;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import org.flowable.common.engine.api.delegate.Expression;
import lombok.Setter;

/**
 * Implementació del handler per a consultar la informació d'un document i desar-la a dins variables.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class DocumentConsultarHandler extends AbstractHeliumActionHandler implements es.caib.helium.disseny.handler.DocumentConsultarHandler {

	private Expression document;
	private Expression varDocument;
	private Expression varCsv;
	private Expression varUrl;

	@Override
	public void execute(HeliumApi heliumApi) throws HeliumHandlerException {
		heliumApi.documentConsultar(
			heliumApi.getVariableDefaultValue(getValue(varDocument), getValue(document)),
            getValue(varCsv),
			getValue(varUrl));
	}

}
